## 1.1.2
- Fix Android resource linking errors: add missing `card`/`card2` colors.
- Fix `textColor` resource references for AAPT.
- Fix Accessibility service description to use a string resource.
- Accept Minecraft Android versions `1.26.0.x` and Bedrock family `26.0.x`.

# 1.1.2

- Fixed Minecraft 26.0 detection for Android package versions such as `1.26.0.2`.
- Supports both `26.0.x` and `1.26.0.x` version-name formats.
- Updated unsupported-version messaging to explain the accepted Bedrock release family.
- Bumped Android app version to 1.1.2 (versionCode 3).

# 1.1.2

- Reworked UI into Home / Performance / Overlay / Tools flow.
- Added persistent performance profiles.
- Added Android thermal monitoring and thermal-aware overlay shutdown.
- Added battery optimization exemption flow.
- Added real display refresh-rate readout.
- Added Android Accessibility magnification zoom controls.
- Added floating zoom +/- and 1x controls to HUD.
- Marked Bedrock renderer culling and internal FPS uncapping as engine-module features instead of presenting fake switches.
