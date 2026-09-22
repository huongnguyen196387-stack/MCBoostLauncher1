package com.mcboost.launcher;

import android.content.Context;
import android.content.SharedPreferences;

final class Prefs {
    private static final String FILE = "mcboost_prefs";
    static final String PROFILE = "profile";
    static final String OVERLAY = "overlay";
    static final String THERMAL_GUARD = "thermal_guard";
    static final String MINIMAL_HUD = "minimal_hud";
    static final String ZOOM = "zoom";
    static final String ZOOM_LEVEL = "zoom_level";
    static final String FPS_TARGET = "fps_target";
    static final String PARTICLE_CULLING = "particle_culling";
    static final String ENTITY_CULLING = "entity_culling";
    static final String RENDER_CULLING = "render_culling";
    static final String OCCLUSION_CULLING = "occlusion_culling";
    static final String UNCAPPED_FPS = "uncapped_fps";

    private Prefs() {}

    static SharedPreferences get(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}
