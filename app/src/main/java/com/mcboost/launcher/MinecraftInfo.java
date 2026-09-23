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
        this.supported = installed && isSupported26(version);
    }

    private static boolean isSupported26(String version) {
        // Official Android Bedrock builds can expose version names such as
        // 1.26.0.2 while the release family is referred to as 26.0.x.
        // Accept both forms without relying on locale or extra suffixes.
        if (version == null) return false;
        String v = version.trim();
        if (v.isEmpty()) return false;

        // Strip an optional leading "v" and compare the numeric release prefix.
        if (v.startsWith("v") || v.startsWith("V")) v = v.substring(1).trim();

        return v.equals("26.0")
                || v.startsWith("26.0.")
                || v.equals("1.26.0")
                || v.startsWith("1.26.0.");
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
