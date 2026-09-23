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
        // Mojang's Android package may expose versions such as 1.26.0.2
        // while the Bedrock release family is presented as 26.0.x.
        if (version == null) return false;
        String v = version.trim();
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
