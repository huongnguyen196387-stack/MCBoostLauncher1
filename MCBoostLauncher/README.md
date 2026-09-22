# MCBoost Launcher 1.1.0

A real Android companion launcher for **official Minecraft Bedrock**. It targets the requested **26.0 / 26.0.x** and refuses to launch unsupported versions.

## Implemented directly
- Official package/version check (`com.mojang.minecraftpe`).
- Launches the installed Minecraft app normally.
- Performance profiles (Balanced / Low latency / Battery saver) stored persistently.
- Thermal Guard monitoring using Android thermal APIs.
- Battery optimization exception request through Android settings.
- Lightweight foreground floating HUD using `SYSTEM_ALERT_WINDOW`.
- Device display refresh-rate reporting in the HUD (this is display Hz, not Minecraft FPS).
- Accessibility Magnification service for system-level Zoom on Android 11+.
- Settings shortcuts for required permissions.

## About Render Culling / real FPS / Uncap FPS
These are **engine-side Bedrock features**. A normal third-party launcher process cannot reach Minecraft's renderer or change its internal frame cap, so the UI intentionally marks those switches as requiring an in-game engine module. No fake FPS values are generated.

To get genuine Render Culling / Entity Culling / Particle Culling / Occlusion Culling / true Uncap FPS, the next architecture needs a Minecraft-side module compatible with the official installation (for example a supported modding/instrumentation route). This launcher is the companion shell and permission/overlay layer.

## Build
Open the project in Android Studio. It uses Android Gradle Plugin 8.6.1, compileSdk 35, targetSdk 35, minSdk 26.
