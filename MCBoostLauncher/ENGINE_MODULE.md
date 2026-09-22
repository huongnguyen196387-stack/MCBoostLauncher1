# True Bedrock engine module roadmap

The requested features **Render Culling, Entity Culling, Particle Culling, Occlusion Culling and a true FPS Uncap** must execute in the Minecraft process / render pipeline. Android's app sandbox does not provide a normal companion launcher with a supported API to rewrite another app's renderer.

A production implementation therefore needs two layers:

1. **MCBoost Launcher (this project)**
   - verifies official Minecraft package and requested 26.0.x version
   - stores the selected performance profile
   - starts the official Minecraft package
   - hosts HUD, Accessibility zoom, thermal monitor and system settings

2. **MCBoost Bedrock Engine Module (next layer)**
   - in-process rendering hooks / supported modding API
   - real frustum + occlusion culling
   - entity/particle batching and distance culling
   - frame-pacing and actual FPS telemetry
   - frame-cap override / uncapped mode
   - optional dynamic resolution

The launcher intentionally does **not** claim to do layer 2 by itself and does not produce fake FPS numbers.
