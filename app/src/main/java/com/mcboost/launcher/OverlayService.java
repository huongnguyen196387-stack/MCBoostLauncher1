package com.mcboost.launcher;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Choreographer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    private static final String CHANNEL_ID = "mcboost_hud";
    private final Handler handler = new Handler();
    private WindowManager windowManager;
    private LinearLayout hud;
    private TextView stats;
    private Choreographer choreographer;
    private int frames;
    private long frameStartNs;
    private float measuredFps;
    private int updateMs = 500;

    private final Runnable update = new Runnable() {
        @Override public void run() {
            updateStats();
            handler.postDelayed(this, updateMs);
        }
    };

    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override public void doFrame(long frameTimeNanos) {
            frames++;
            if (frameStartNs == 0) frameStartNs = frameTimeNanos;
            long elapsed = frameTimeNanos - frameStartNs;
            if (elapsed >= 1_000_000_000L) {
                measuredFps = frames * 1_000_000_000f / elapsed;
                frames = 0;
                frameStartNs = frameTimeNanos;
            }
            if (choreographer != null) choreographer.postFrameCallback(this);
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(7, buildNotification());
        applyProfile();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        buildHud();
        if (choreographer != null) choreographer.postFrameCallback(frameCallback);
        handler.post(update);
    }

    private void applyProfile() {
        String profile = Prefs.get(this).getString(Prefs.PROFILE, "Balanced");
        if ("Low latency".equals(profile)) updateMs = 250;
        else if ("Battery saver".equals(profile)) updateMs = 1000;
        else updateMs = 500;
    }

    private void buildHud() {
        hud = new LinearLayout(this);
        hud.setOrientation(LinearLayout.HORIZONTAL);
        hud.setPadding(dp(10), dp(7), dp(10), dp(7));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(225, 13, 20, 29));
        bg.setCornerRadius(dp(16));
        bg.setStroke(dp(1), Color.argb(160, 110, 231, 183));
        hud.setBackground(bg);

        stats = new TextView(this);
        stats.setTextColor(Color.WHITE);
        stats.setTextSize(12f);
        stats.setTypeface(null, android.graphics.Typeface.BOLD);
        hud.addView(stats, new LinearLayout.LayoutParams(-2, -2));

        TextView close = new TextView(this);
        close.setText("  ×");
        close.setTextColor(Color.LTGRAY);
        close.setTextSize(15f);
        close.setOnClickListener(v -> stopSelf());
        hud.addView(close, new LinearLayout.LayoutParams(-2, -2));

        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                flags,
                android.graphics.PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.y = dp(48);
        try {
            windowManager.addView(hud, params);
        } catch (WindowManager.BadTokenException | SecurityException e) {
            stopSelf();
            return;
        }

        hud.setOnTouchListener(new View.OnTouchListener() {
            float downX, downY;
            int startX, startY;
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = event.getRawX(); downY = event.getRawY();
                    startX = params.x; startY = params.y;
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    params.x = startX + (int) (event.getRawX() - downX);
                    params.y = startY + (int) (event.getRawY() - downY);
                    windowManager.updateViewLayout(hud, params);
                    return true;
                }
                return event.getAction() == MotionEvent.ACTION_UP;
            }
        });
        choreographer = Choreographer.getInstance();
    }

    private void updateStats() {
        if (stats == null) return;
        int cps = CpsCounter.getAndMaybeReset();
        ThermalMonitor.Level thermal = ThermalMonitor.level(this);
        boolean guard = Prefs.get(this).getBoolean(Prefs.THERMAL_GUARD, true);
        boolean minimal = Prefs.get(this).getBoolean(Prefs.MINIMAL_HUD, false);
        if (guard) {
            if (thermal == ThermalMonitor.Level.SEVERE) updateMs = 2000;
            else if (thermal == ThermalMonitor.Level.THROTTLED) updateMs = Math.max(updateMs, 1000);
            else applyProfile();
        } else {
            applyProfile();
        }
        if (minimal) {
            stats.setText(String.format(java.util.Locale.US, "FPS≈%.0f • CPS=%d", measuredFps, cps));
            return;
        }
        String thermalText = thermal == ThermalMonitor.Level.COOL ? "Cool"
                : thermal == ThermalMonitor.Level.WARM ? "Warm"
                : thermal == ThermalMonitor.Level.THROTTLED ? "Throttled"
                : thermal == ThermalMonitor.Level.SEVERE ? "Hot" : "—";
        stats.setText(String.format(java.util.Locale.US,
                "FPS≈%.0f  •  CPS=%d  •  %s%s",
                measuredFps, cps, thermalText, guard ? "  •  Guard" : ""));
    }

    private Notification buildNotification() {
        return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("MCBoost HUD")
                .setContentText("Performance overlay is running")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setOngoing(true)
                .build();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MCBoost HUD", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Foreground notification for the optional Minecraft companion HUD");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    @Override public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (choreographer != null) choreographer.removeFrameCallback(frameCallback);
        if (windowManager != null && hud != null) {
            try { windowManager.removeView(hud); } catch (RuntimeException ignored) {}
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
