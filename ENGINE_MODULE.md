# Engine Module Boundary

MCBoost Launcher is intentionally split into a companion shell and an optional engine-side integration.

## Companion shell responsibilities

- Verify the official Minecraft package.
- Enforce the supported `26.0.x` version gate.
- Persist user profiles and preferences.
- Manage overlay, notification, Accessibility and battery/display shortcuts.
- Show transparent status/capability information.

## Engine module responsibilities

Only an engine-side component can truthfully implement controls such as:

- Render Culling
- Entity Culling
- Particle Culling
- Occlusion Culling
- True in-game FPS/frame timing
- True FPS cap / uncap
- Render-distance and renderer-specific controls

The launcher UI therefore marks these controls as `Requires engine module` instead of pretending to modify Minecraft.

## Suggested bridge contract

A future module may expose a small JSON capability document and a metrics endpoint with fields such as:

```json
{
  "engineVersion": "1.0",
  "minecraftVersion": "26.0.x",
  "renderCulling": true,
  "entityCulling": true,
  "particleCulling": true,
  "occlusionCulling": true,
  "trueFps": true,
  "uncapFps": true,
  "renderDistanceControl": true,
  "fps": 120.5,
  "frameTimeMs": 8.30
}
```

The JSON above is a contract example, not a claim that the current launcher can access those engine values.

## Compatibility rule

Do not silently accept an engine module built for another Minecraft version. The module should report its exact compatible range, and the launcher should show `INCOMPATIBLE` when it does not match the installed official build.
