package com.mcboost.launcher;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.MagnificationConfig;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

public class BoostAccessibilityService extends AccessibilityService {
    private static BoostAccessibilityService instance;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event != null && event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            CpsCounter.record();
        }
    }

    @Override
    public void onInterrupt() {
        // No continuous accessibility work to interrupt.
    }

    @Override
    public void onDestroy() {
        if (instance == this) instance = null;
        super.onDestroy();
    }

    public static boolean isConnected() {
        return instance != null;
    }

    public static boolean setZoom(float scale) {
        BoostAccessibilityService service = instance;
        if (service == null) return false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                MagnificationConfig config = new MagnificationConfig.Builder()
                        .setMode(MagnificationConfig.MAGNIFICATION_MODE_FULLSCREEN)
                        .setScale(Math.max(1f, Math.min(8f, scale)))
                        .build();
                return service.getMagnificationController().setMagnificationConfig(config, true);
            }
            return service.getMagnificationController().setScale(scale, true);
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean resetZoom() {
        BoostAccessibilityService service = instance;
        if (service == null) return false;
        try {
            return service.getMagnificationController().reset(true);
        } catch (RuntimeException e) {
            return false;
        }
    }
}
