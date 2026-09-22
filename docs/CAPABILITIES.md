# Capability matrix

## Launcher-side: implemented

| Feature | Status | What it really does |
|---|---|---|
| Official Minecraft package check | ✅ | Requires `com.mojang.minecraftpe`. |
| Version gate | ✅ | Only launches `26.0.x`. |
| Normal launch | ✅ | Uses Android's normal launch intent. |
| Performance profiles | ✅ | Stores Balanced / Low latency / Battery saver settings and changes companion HUD workload. |
| Thermal Guard | ✅ | Reads Android thermal state and reduces HUD update activity when throttling is detected. |
| Minimal HUD | ✅ | Reduces overlay content. |
| Floating HUD | ✅ | Optional foreground overlay with FPS≈/CPS/thermal information. |
| Display refresh reporting | ✅ | Reads the current display refresh rate. |
| Zoom | ✅ | Optional Accessibility magnification, 1×–6× UI with Android APIs. |
| Battery optimization shortcut | ✅ | Opens the Android battery optimization flow. |
| Display settings shortcut | ✅ | Opens Android display settings. |
| Permission shortcuts | ✅ | Overlay, notification and Accessibility guidance. |

## Engine-side: not implemented in the companion shell

| Feature | Status | Reason |
|---|---|---|
| Render Culling | ⏳ | Requires access to Minecraft's renderer/engine. |
| Entity Culling | ⏳ | Requires engine-side visibility decisions. |
| Particle Culling | ⏳ | Requires engine-side particle/render control. |
| Occlusion Culling | ⏳ | Requires renderer/scene access. |
| True in-game FPS | ⏳ | The launcher cannot read Minecraft's internal frame timing from a normal third-party process. |
| True Uncap FPS | ⏳ | Requires changing the game's own frame cap/pacing logic. |
| In-game render distance | ⏳ | Requires game/engine integration. |

## Important metric labels

The HUD deliberately uses `FPS≈` because this shell measures its own display cadence, not Minecraft's renderer. `CPS` depends on Android accessibility click events and may show limited/zero data for a custom game surface. The app does not fabricate Minecraft FPS values.

## Engine module boundary

The companion shell should remain independent of Minecraft's official APK. A future engine module can expose explicit capabilities and metrics through a documented IPC contract without requiring the launcher to patch the official package.
