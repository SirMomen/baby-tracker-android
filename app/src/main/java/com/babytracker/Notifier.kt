package com.babytracker

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import org.json.JSONArray
import org.json.JSONObject

/** Schedules alarms with Android's AlarmManager and shows the notifications. */
object Notifier {
    private const val CHANNEL = "baby_alarms_v2"
    private const val PREFS = "alarms"
    private const val KEY = "json"

    fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = ctx.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CHANNEL) != null) return
        val ch = NotificationChannel(CHANNEL, "Baby alarms", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Feeding, diaper and medication reminders"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500)
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
        }
        nm.createNotificationChannel(ch)
    }

    fun hasPermission(ctx: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun pending(ctx: Context, code: Int, extras: JSONObject?, create: Boolean): PendingIntent? {
        val i = Intent(ctx, AlarmReceiver::class.java)
        if (extras != null) {
            i.putExtra("title", extras.optString("title"))
            i.putExtra("body", extras.optString("body"))
        }
        val flags = PendingIntent.FLAG_IMMUTABLE or
            (if (create) PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_NO_CREATE)
        return PendingIntent.getBroadcast(ctx, code, i, flags)
    }

    private fun openApp(ctx: Context): PendingIntent =
        PendingIntent.getActivity(
            ctx, 0,
            Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun cancelAll(ctx: Context) {
        val am = ctx.getSystemService(AlarmManager::class.java)
        val old = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        val n = try { JSONArray(old).length() } catch (e: Exception) { 0 }
        for (i in 0 until n) pending(ctx, i, null, false)?.let { am.cancel(it); it.cancel() }
    }

    /** [json] = [{time: epochMillis, title, body}, ...]. Replaces everything scheduled before. */
    fun schedule(ctx: Context, json: String) {
        cancelAll(ctx)
        val now = System.currentTimeMillis()
        val src = try { JSONArray(json) } catch (e: Exception) { JSONArray() }
        val keep = JSONArray()
        for (i in 0 until src.length()) {
            val o = src.getJSONObject(i)
            if (o.getLong("time") > now) keep.put(o)
        }
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, keep.toString()).apply()
        val am = ctx.getSystemService(AlarmManager::class.java)
        for (i in 0 until keep.length()) {
            val o = keep.getJSONObject(i)
            val pi = pending(ctx, i, o, true)!!
            // setAlarmClock is exact, survives Doze and needs no special permission.
            am.setAlarmClock(AlarmManager.AlarmClockInfo(o.getLong("time"), openApp(ctx)), pi)
        }
    }

    fun restore(ctx: Context) {
        val saved = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        schedule(ctx, saved)
    }

    fun show(ctx: Context, id: Int, title: String, body: String) {
        if (!hasPermission(ctx)) return
        ensureChannel(ctx)
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(body)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(openApp(ctx))
            .setAutoCancel(true)
            .build()
        ctx.getSystemService(NotificationManager::class.java).notify(id, n)
    }

    fun clearShown(ctx: Context) {
        ctx.getSystemService(NotificationManager::class.java).cancelAll()
    }
}
