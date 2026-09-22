# MCBoost Launcher 1.2.0

A real Android companion launcher for the official Minecraft Bedrock package `com.mojang.minecraftpe`.

## Supported target

- Official Minecraft Bedrock: `26.0.x`
- Android min SDK: 26
- Compile/target SDK: 35
- Android Gradle Plugin: 8.6.1
- Java: 17

The launcher refuses to start an unsupported Minecraft version.

## What is implemented

### Launcher-side

- Official package/version check.
- Normal Android launch.
- Persistent Balanced / Low latency / Battery saver companion profiles.
- Thermal Guard using Android thermal APIs.
- Minimal HUD mode.
- Floating HUD with `FPS≈`, CPS and thermal status.
- Display refresh-rate reporting.
- Accessibility-based system zoom (1×–6×).
- Battery optimization and Display settings shortcuts.
- Clear capability separation between shell and engine-side features.

### Engine-side boundary

Render Culling, Entity Culling, Particle Culling, Occlusion Culling, true in-game FPS, true Uncap FPS and renderer-specific controls are marked as **Requires engine module**. A normal third-party launcher cannot truthfully toggle Minecraft's internal renderer or frame cap just by launching another app.

The HUD uses `FPS≈` intentionally: it measures the companion overlay's own display cadence, not Minecraft's internal renderer. CPS depends on Android accessibility click events and may be unavailable for a custom-rendered game surface. No fake Minecraft FPS values are generated.

See [CAPABILITIES.md](docs/CAPABILITIES.md) and [ENGINE_MODULE.md](ENGINE_MODULE.md).

## Build locally

### Android Studio

Open the project root in Android Studio and build `app` with a JDK 17 toolchain.

### Command line / Termux

Install a JDK 17 and Gradle, then from the project root run:

```bash
gradle assembleDebug
```

APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

If your environment has an Android SDK installation, make sure `ANDROID_HOME` / `ANDROID_SDK_ROOT` and the required SDK 35 platform/build-tools are configured.

## GitHub Actions

`.github/workflows/android-ci.yml` builds the debug APK on pushes to `main`, pull requests, and manual runs. The APK is available from the workflow's **Artifacts** section.

`.github/workflows/release.yml` builds the installable debug APK and creates a GitHub Release whenever you push a tag matching `v*`.

Example:

```bash
git add .
git commit -m "MCBoost Launcher 1.2.0"
git push origin main

git tag v1.2.0
git push origin v1.2.0
```

GitHub will then build and attach:

```text
MCBoostLauncher-v1.2.0.apk
```

to the Release page.

### About signing

The automated release intentionally uses the debug signing path so the workflow can publish a directly installable APK without storing a private keystore in the repository. For a production-signed APK, configure a private keystore through GitHub Actions secrets and change the release build to use that signing configuration.

## Permissions

The optional HUD uses `SYSTEM_ALERT_WINDOW` and a foreground service. Zoom uses an AccessibilityService and must be enabled manually in Android Settings. Android 13+ may also request notification permission for the foreground-service notification.

## License

This generated project contains only the launcher source and does not include or modify Minecraft code/assets.
