package com.babytracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Baby Tracker"
        val body = intent.getStringExtra("body") ?: "Time for a reminder"
        Notifier.show(context, (System.currentTimeMillis() % 100000).toInt(), title, body)
    }
}

/** Alarms are cleared on reboot or app update, so put them back. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Notifier.ensureChannel(context)
        Notifier.restore(context)
    }
}
