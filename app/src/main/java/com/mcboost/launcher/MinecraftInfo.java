package com.mcboost.launcher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

final class MinecraftInfo {
    static final String PACKAGE = "com.mojang.minecraftpe";
    final boolean installed;
    final String version;
    final long versionCode;
    final boolean supported;

    private MinecraftInfo(boolean installed, String version, long versionCode) {
        this.installed = installed;
        this.version = version;
        this.versionCode = versionCode;
        this.supported = installed && (version.equals("26.0") || version.startsWith("26.0."));
    }

    static MinecraftInfo read(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(PACKAGE, 0);
            long code = android.os.Build.VERSION.SDK_INT >= 28 ? info.getLongVersionCode() : info.versionCode;
            return new MinecraftInfo(true, info.versionName == null ? "unknown" : info.versionName, code);
        } catch (PackageManager.NameNotFoundException e) {
            return new MinecraftInfo(false, "not installed", 0);
        }
    }
}
