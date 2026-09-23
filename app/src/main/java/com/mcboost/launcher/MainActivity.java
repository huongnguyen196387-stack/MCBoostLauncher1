package com.mcboost.launcher;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private TextView tvInstall, tvVersion, tvPerfSummary, tvBuild;
    private Button btnPlay;
    private MinecraftInfo mc;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        tvInstall = findViewById(R.id.tvInstall);
        tvVersion = findViewById(R.id.tvVersion);
        tvPerfSummary = findViewById(R.id.tvPerfSummary);
        tvBuild = findViewById(R.id.tvBuild);
        tvBuild.setText("1.1.3");
        btnPlay = findViewById(R.id.btnPlay);
        findViewById(R.id.btnPerformance).setOnClickListener(v -> openPerformance());
        findViewById(R.id.btnOverlay).setOnClickListener(v -> openOverlay());
        findViewById(R.id.btnSettings).setOnClickListener(v -> openTools());
        btnPlay.setOnClickListener(v -> play());
        createChannel();
    }

    @Override protected void onResume() { super.onResume(); refresh(); }

    private void refresh() {
        mc = MinecraftInfo.read(this);
        if (!mc.installed) {
            tvInstall.setText("✕ Minecraft is not installed");
            tvInstall.setTextColor(getColorCompat(com.mcboost.launcher.R.color.danger));
            tvVersion.setText("Install the official Minecraft Bedrock app first.");
        } else if (!mc.supported) {
            tvInstall.setText("⚠ Minecraft installed, version not supported");
            tvInstall.setTextColor(getColorCompat(com.mcboost.launcher.R.color.danger));
            tvVersion.setText("Detected " + mc.version + " • supports Bedrock 26.0.x (including 1.26.0.x)");
        } else {
            tvInstall.setText("✓ Official Minecraft installed");
            tvInstall.setTextColor(getColorCompat(com.mcboost.launcher.R.color.accent));
            tvVersion.setText("Detected " + mc.version + " • supported");
        }
        btnPlay.setEnabled(mc.supported);
        String profile = Prefs.get(this).getString(Prefs.PERF, "Balanced");
        tvPerfSummary.setText(profile + " • " + (Prefs.get(this).getBoolean(Prefs.UNCAP, false) ? "Uncap requested" : "FPS cap managed by Minecraft"));
    }

    private void play() {
        if (mc == null || !mc.supported) { refresh(); return; }
        Intent launch = getPackageManager().getLaunchIntentForPackage(MinecraftInfo.PACKAGE);
        if (launch == null) { Toast.makeText(this, "Minecraft launcher activity not found", Toast.LENGTH_LONG).show(); return; }
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startServiceSafely();
        startActivity(launch);
    }

    private void startServiceSafely() {
        if (!Prefs.get(this).getBoolean(Prefs.OVERLAY, false)) return;
        if (!Settings.canDrawOverlays(this)) return;
        Intent i = new Intent(this, OverlayService.class);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
    }

    private void openPerformance() {
        startActivity(new Intent(this, PerformanceActivity.class));
    }

    private void openOverlay() {
        startActivity(new Intent(this, OverlayActivity.class));
    }

    private void openTools() {
        startActivity(new Intent(this, ToolsActivity.class));
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(new NotificationChannel("mcboost_overlay", "MCBoost overlay", NotificationManager.IMPORTANCE_LOW));
        }
    }

    private int getColorCompat(int id) { return getResources().getColor(id); }
}
