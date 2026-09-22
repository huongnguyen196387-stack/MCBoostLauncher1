package com.mcboost.launcher;

import android.content.Context;
import android.content.SharedPreferences;

final class Prefs {
    private static final String FILE = "mcboost";
    static final String PERF = "perf";
    static final String OVERLAY = "overlay";
    static final String FPS = "fps";
    static final String CPS = "cps";
    static final String ZOOM = "zoom";
    static final String CULLING = "culling";
    static final String ENTITY_CULLING = "entity_culling";
    static final String PARTICLES = "particles";
    static final String OCCLUSION = "occlusion";
    static final String FRAME_PACING = "frame_pacing";
    static final String THERMAL = "thermal";
    static final String UNCAP = "uncap";

    static SharedPreferences get(Context c) { return c.getSharedPreferences(FILE, Context.MODE_PRIVATE); }
    private Prefs() {}
}
