# 👶 Baby Tracker – Android App (Native Kotlin + WebView)

[![Android](https://img.shields.io/badge/Platform-Android_7.0+_(API_24--34)-brightgreen?logo=android)](https://developer.android.com)
[![Build APK](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-blue?logo=githubactions)](.github/workflows/build-apk.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-purple?logo=kotlin)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/Gradle-8.7-02303A?logo=gradle)](https://gradle.org)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

A lightweight, reliable, privacy-first **Baby Tracker** application for Android. It tracks baby feedings (breastfeeding left/right & bottle), diaper changes (wet, dirty, mixed), sleep logs, medication, and timers with **hardware-exact alarms** that wake your device even when the phone is asleep or the app is closed.

---

## 🌟 Key Features

- 🍼 **Feeding Tracker**: Track nursing duration (Left / Right breast timers) and bottle amounts (oz / ml).
- 🧷 **Diaper Logs**: Fast 1-tap logging for wet, dirty, and mixed diapers.
- 💤 **Sleep & Nap Timer**: Live start/stop timers for daytime naps and nighttime sleep.
- ⏰ **Reliable Exact Alarms**: Uses Android's `AlarmManager.setAlarmClock()`, which pierces Doze mode without requiring battery optimization whitelist exceptions.
- 🔄 **Reboot & Update Safe**: Automatically restores all scheduled alarms on device reboot or app upgrade via `BootReceiver`.
- 📊 **Daily Summaries & History**: Clean cards showing today's totals, latest intervals, and timestamped history entries with edit/delete support.
- 🌙 **Dark & Light Mode**: Seamlessly follows your Android system theme or toggles on demand.
- 🔒 **100% Private & Local**: Zero third-party trackers, zero advertising, all data stored on your device.

---

## 📱 Architecture & How It Works

1. **WebView Asset Loader**: The user interface is built with responsive HTML5, CSS3, and JavaScript located in `app/src/main/assets/`. It is served securely via `androidx.webkit.WebViewAssetLoader` over `https://appassets.androidplatform.net/assets/index.html`.
2. **Native JavaScript Bridge (`BabyNative`)**:
   - `scheduleAlarms(json)`: Passes alarm times directly to `Notifier.kt`.
   - `hasNotificationPermission()`: Checks runtime `POST_NOTIFICATIONS` status (Android 13+).
   - `testNotification()`: Triggers an immediate test alarm to verify sounds and vibrations.
   - `clearNotifications()`: Dismisses shown notifications.
3. **Hardware Exact Alarms (`Notifier.kt`)**:
   - Calls `AlarmManager.setAlarmClock()`, registering an exact wakeup trigger with high priority notification channels (`baby_alarms_v2`).
4. **Boot Recovery (`Receivers.kt`)**:
   - `BootReceiver` listens for `ACTION_BOOT_COMPLETED` and `ACTION_MY_PACKAGE_REPLACED`, reading saved alarms from `SharedPreferences` and rescheduling them automatically.

---

## 📥 Download the Prebuilt APK

- **Latest APK Download**: [`BabyTracker.apk`](https://ais-dev-oytf4a6dr3rrktdku5wajo-632436663205.europe-west2.run.app/download/BabyTracker.apk)
- **Version**: 2.0 (Version Code 2)
- **Target SDK**: Android 14 (API 34)
- **Min SDK**: Android 7.0 (API 24)
- **SHA-256**: `338a1270879cc76bc8c3d9a4a917da5d965c5d5307d6374b0465bcc34892eeb3`

### Sideloading Instructions:
1. Download `BabyTracker.apk` directly to your phone.
2. Tap the file in **Downloads**. If Android prompts *"Install unknown apps"*, tap **Settings** and toggle **Allow from this source**.
3. Tap **Install** and launch the app.
4. Allow notification permissions when prompted.

---

## 🛠️ Building From Source

### Option 1: Command Line (Gradle Wrapper)
Prerequisites: JDK 17+ and Android SDK (API 34).

```bash
# Clone the repository
git clone https://github.com/<your-username>/baby-tracker-android.git
cd baby-tracker-android

# Build Debug APK
./gradlew assembleDebug

# Output APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

### Option 2: Android Studio
1. Open Android Studio.
2. Select **File > Open** and choose the `BabyTrackerAndroid` folder.
3. Allow Gradle to sync.
4. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

### Option 3: GitHub Actions (No Local Setup Required)
This repository includes a preconfigured workflow in `.github/workflows/build-apk.yml`.
1. Push this repository to GitHub.
2. Navigate to the **Actions** tab.
3. Select **Build APK** and click **Run workflow**.
4. Download the compiled `BabyTracker-apk` artifact directly from the GitHub run summary!

---

## 📂 Project Directory Structure

```text
BabyTrackerAndroid/
├── .github/
│   └── workflows/
│       └── build-apk.yml         # Automated GitHub Actions APK build workflow
├── app/
│   ├── build.gradle.kts          # Module-level Android configuration (SDK 34)
│   └── src/main/
│       ├── AndroidManifest.xml   # App permissions and receiver declarations
│       ├── assets/               # WebView frontend assets (HTML, SVG, JS, PWA)
│       ├── java/com/babytracker/
│       │   ├── MainActivity.kt   # WebView host & BabyNative JS bridge
│       │   ├── Notifier.kt       # AlarmManager & Notification channel scheduler
│       │   └── Receivers.kt      # Alarm & Boot BroadcastReceivers
│       └── res/                  # App launcher icons & Material themes
├── gradle/wrapper/               # Gradle 8.7 wrapper binaries & config
├── build.gradle.kts              # Root project build configuration
├── settings.gradle.kts           # Gradle project settings
├── gradlew                       # Linux / macOS Gradle wrapper script
├── gradlew.bat                   # Windows Gradle wrapper script
└── README.md                     # Documentation
```

---

## 📄 License

Licensed under the Apache License, Version 2.0. See LICENSE for details.
