package com.mcboost.launcher;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

final class AccessibilityState {
    private AccessibilityState() {}

    static boolean isEnabled(Context context) {
        String enabled = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (TextUtils.isEmpty(enabled)) return false;
        ComponentName expected = new ComponentName(context, BoostAccessibilityService.class);
        for (String raw : enabled.split(":")) {
            ComponentName parsed = ComponentName.unflattenFromString(raw);
            if (expected.equals(parsed)) return true;
        }
        return false;
    }
}
