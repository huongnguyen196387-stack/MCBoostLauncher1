# MCBoost Launcher 1.1.1

Android companion launcher for **official Minecraft Bedrock**.

## Minecraft 26.0 detection

The launcher accepts the Bedrock 26.0 release family when Android reports either of these common version-name formats:

- `26.0`, `26.0.x`
- `1.26.0`, `1.26.0.x` (for example `1.26.0.2`)

The app launches only the installed official package `com.mojang.minecraftpe`.

## Implemented directly

- Official Minecraft package/version check.
- Normal launch of the installed Minecraft app.
- Performance profiles: Balanced / Low latency / Battery saver.
- Thermal Guard using Android thermal APIs.
- Battery optimization exception flow.
- Lightweight foreground floating HUD using `SYSTEM_ALERT_WINDOW`.
- Device display refresh-rate reporting in the HUD (display Hz, not Minecraft FPS).
- Accessibility Magnification service for system-level Zoom.
- Settings shortcuts for required permissions.

## Performance / engine-side features

The UI separates Android-side optimization from Bedrock engine features that require an in-process module:

- Render Culling
- Entity Culling
- Particle Culling
- Occlusion Culling
- Chunk / world render optimization
- True FPS telemetry and 1% low FPS
- True Uncap FPS / frame-cap override
- Dynamic resolution

A normal third-party launcher process does not have a supported Android API for rewriting another app's renderer or internal frame cap. Therefore this project does **not** claim that these engine features are active merely because a switch exists. No fake FPS values are generated.

## Build

Android Gradle Plugin: 8.6.1  
compileSdk: 35  
targetSdk: 35  
minSdk: 26

Open in Android Studio, or build with Gradle after installing the Android SDK.

## GitHub Actions

The repository includes a workflow under `.github/workflows/android.yml` that builds a debug APK on push and stores it as a workflow artifact.
