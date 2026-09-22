#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

if ! command -v java >/dev/null 2>&1; then
  echo "Java 17 is required. Install an OpenJDK 17 package in Termux first."
  exit 1
fi

if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle is required. Install Gradle in Termux first or open the project in Android Studio."
  exit 1
fi

gradle assembleDebug --stacktrace

echo
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
