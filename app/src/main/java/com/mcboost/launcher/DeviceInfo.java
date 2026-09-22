package com.mcboost.launcher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

final class DeviceInfo {
    static final String MINECRAFT_PACKAGE = "com.mojang.minecraftpe";
    static final String REQUESTED_VERSION = "26.0";

    private DeviceInfo() {}

    static PackageInfo minecraftInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(MINECRAFT_PACKAGE, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    static String minecraftVersion(Context context) {
        PackageInfo info = minecraftInfo(context);
        return info == null || info.versionName == null ? "Not installed" : info.versionName;
    }

    static boolean isSupportedMinecraft(Context context) {
        PackageInfo info = minecraftInfo(context);
        return info != null && info.versionName != null && info.versionName.startsWith(REQUESTED_VERSION);
    }

    static float refreshRate(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return 0f;
        Display display = wm.getDefaultDisplay();
        return display == null ? 0f : display.getRefreshRate();
    }

    static String androidSummary() {
        return "Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")";
    }
}
