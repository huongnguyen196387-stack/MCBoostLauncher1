package com.mcboost.launcher;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

final class ThermalMonitor {
    enum Level { COOL, WARM, THROTTLED, SEVERE, UNKNOWN }

    private ThermalMonitor() {}

    static Level level(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return Level.UNKNOWN;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm == null) return Level.UNKNOWN;
        switch (pm.getCurrentThermalStatus()) {
            case PowerManager.THERMAL_STATUS_NONE:
                return Level.COOL;
            case PowerManager.THERMAL_STATUS_LIGHT:
                return Level.WARM;
            case PowerManager.THERMAL_STATUS_MODERATE:
            case PowerManager.THERMAL_STATUS_SEVERE:
                return Level.THROTTLED;
            case PowerManager.THERMAL_STATUS_CRITICAL:
            case PowerManager.THERMAL_STATUS_EMERGENCY:
            case PowerManager.THERMAL_STATUS_SHUTDOWN:
                return Level.SEVERE;
            default:
                return Level.UNKNOWN;
        }
    }

    static float headroom(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return Float.NaN;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm == null) return Float.NaN;
        try {
            return pm.getThermalHeadroom(10);
        } catch (RuntimeException e) {
            return Float.NaN;
        }
    }
}
