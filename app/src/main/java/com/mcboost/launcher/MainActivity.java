package com.mcboost.launcher;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {
    private final SharedPreferences.OnSharedPreferenceChangeListener prefListener = (prefs, key) -> refresh();
    private LinearLayout content;
    private ScrollView scrollView;
    private TextView mcStatus;
    private TextView refreshStatus;

    private int dp(float v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(getColorSafe(com.mcboost.launcher.R.color.mc_bg));
        window.setNavigationBarColor(getColorSafe(com.mcboost.launcher.R.color.mc_bg));
        if (Build.VERSION.SDK_INT >= 23) window.getDecorView().setSystemUiVisibility(0);

        Prefs.get(this).registerOnSharedPreferenceChangeListener(prefListener);
        buildUi();
        requestNotificationPermissionIfNeeded();
    }

    @Override protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override protected void onDestroy() {
        Prefs.get(this).unregisterOnSharedPreferenceChangeListener(prefListener);
        super.onDestroy();
    }

    private int getColorSafe(int id) { return getResources().getColor(id, getTheme()); }

    private void buildUi() {
        scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(28));
        content.setBackgroundColor(getColorSafe(R.color.mc_bg));
        scrollView.addView(content, new ScrollView.LayoutParams(-1, -1));
        setContentView(scrollView);
    }

    private void refresh() {
        if (content == null) return;
        content.removeAllViews();
        addHeader();
        addMinecraftCard();
        addNavRow();
        addPerformanceSection();
        addEngineSection();
        addToolsSection();
        addAboutSection();
    }

    private void addHeader() {
        TextView title = text("MCBoost Launcher", 28, R.color.mc_text, true);
        content.addView(title, margins(0, 0, 0, 2));
        TextView sub = text("Official Minecraft Bedrock companion • 26.0 / 26.0.x", 13, R.color.mc_muted, false);
        content.addView(sub, margins(0, 0, 0, 16));
    }

    private void addMinecraftCard() {
        LinearLayout card = card();
        LinearLayout head = row();
        TextView label = text("Minecraft Bedrock", 18, R.color.mc_text, true);
        head.addView(label, new LinearLayout.LayoutParams(0, -2, 1));
        TextView badge = text(DeviceInfo.isSupportedMinecraft(this) ? "READY" : "CHECK", 11,
                DeviceInfo.isSupportedMinecraft(this) ? R.color.mc_accent : R.color.mc_warning, true);
        head.addView(badge);
        card.addView(head, margins(0,0,0,8));

        mcStatus = text("Installed: " + DeviceInfo.minecraftVersion(this), 13, R.color.mc_muted, false);
        card.addView(mcStatus);
        refreshStatus = text(String.format(Locale.US, "Display refresh: %.0f Hz • %s", DeviceInfo.refreshRate(this), DeviceInfo.androidSummary()),
                12, R.color.mc_muted, false);
        card.addView(refreshStatus, margins(0, 4, 0, 12));

        Button play = button(DeviceInfo.isSupportedMinecraft(this) ? "▶  Play Minecraft" : "Install/check Minecraft");
        play.setOnClickListener(v -> launchMinecraft());
        card.addView(play);
        content.addView(card, margins(0, 0, 0, 12));
    }

    private void addNavRow() {
        LinearLayout nav = row();
        Button performance = button("Performance");
        Button tools = button("Tools");
        Button help = button("Capabilities");
        performance.setOnClickListener(v -> scrollTo("performance"));
        tools.setOnClickListener(v -> scrollTo("tools"));
        help.setOnClickListener(v -> scrollTo("engine"));
        nav.addView(performance, weightParams(1, 0));
        nav.addView(tools, weightParams(1, 8));
        nav.addView(help, weightParams(1, 8));
        content.addView(nav, margins(0, 0, 0, 16));
    }

    private void scrollTo(String section) {
        if (scrollView == null || content == null) return;
        View target = content.findViewWithTag(section);
        if (target != null) scrollView.post(() -> scrollView.smoothScrollTo(0, Math.max(0, target.getTop() - dp(12))));
    }

    private void addPerformanceSection() {
        TextView t = text("Performance", 22, R.color.mc_text, true);
        t.setTag("performance");
        content.addView(t, margins(0, 0, 0, 8));
        TextView note = text("Các profile tác động vào launcher/overlay và tích hợp Android. Chúng không tự sửa renderer nội bộ của Minecraft.",
                12, R.color.mc_muted, false);
        content.addView(note, margins(0, 0, 0, 10));

        addProfileCard();
        addSwitchRow("Minimal HUD", "Giảm tần suất/chi tiết HUD để launcher nhẹ hơn.", Prefs.MINIMAL_HUD, false);
        addSwitchRow("Thermal Guard", "Theo dõi thermal status; khi nóng, giảm hoạt động HUD và cảnh báo.", Prefs.THERMAL_GUARD, false);
        addSwitchRow("Floating HUD", "Hiển thị HUD nổi khi Minecraft đang chạy.", Prefs.OVERLAY, true);
        addSwitchRow("System Zoom", "Zoom toàn màn hình bằng AccessibilityService. Cần bật quyền trợ năng.", Prefs.ZOOM, false);
        addZoomLevel();
        addFpsTarget();

        LinearLayout real = card();
        real.addView(text("Tác động thực tế", 16, R.color.mc_text, true));
        real.addView(text("✓ Thermal Guard: giảm overhead của launcher khi máy nóng.\n✓ Minimal HUD: giảm công việc vẽ HUD.\n✓ Floating HUD: cung cấp thông tin ngoài game.\n✓ Zoom: thay đổi hiển thị hệ thống, không thay đổi engine Minecraft.",
                12, R.color.mc_muted, false), margins(0, 8, 0, 0));
        content.addView(real, margins(0, 0, 0, 16));
    }

    private void addProfileCard() {
        LinearLayout card = card();
        LinearLayout head = row();
        head.addView(text("Profile", 16, R.color.mc_text, true), new LinearLayout.LayoutParams(0, -2, 1));
        String profile = Prefs.get(this).getString(Prefs.PROFILE, "Balanced");
        TextView value = text(profile, 12, R.color.mc_accent, true);
        head.addView(value);
        card.addView(head);

        TextView desc = text("Balanced = HUD 500ms • Low latency = 250ms • Battery saver = 1000ms", 12, R.color.mc_muted, false);
        card.addView(desc, margins(0, 6, 0, 10));
        Button choose = button("Change profile");
        choose.setOnClickListener(v -> chooseProfile());
        card.addView(choose);
        content.addView(card, margins(0, 0, 0, 10));
    }

    private void chooseProfile() {
        String[] items = {"Balanced", "Low latency", "Battery saver"};
        String current = Prefs.get(this).getString(Prefs.PROFILE, "Balanced");
        int checked = 0;
        for (int i = 0; i < items.length; i++) if (items[i].equals(current)) checked = i;
        new AlertDialog.Builder(this)
                .setTitle("Performance profile")
                .setSingleChoiceItems(items, checked, (dialog, which) -> {
                    String selected = items[which];
                    SharedPreferences.Editor e = Prefs.get(this).edit().putString(Prefs.PROFILE, selected);
                    if ("Battery saver".equals(selected)) {
                        e.putBoolean(Prefs.MINIMAL_HUD, true).putBoolean(Prefs.THERMAL_GUARD, true);
                    } else if ("Low latency".equals(selected)) {
                        e.putBoolean(Prefs.MINIMAL_HUD, false);
                    }
                    e.apply();
                    dialog.dismiss();
                    toast("Profile: " + selected);
                }).show();
    }

    private void addSwitchRow(String title, String desc, String key, boolean startsService) {
        LinearLayout card = card();
        LinearLayout head = row();
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.addView(text(title, 15, R.color.mc_text, true));
        labels.addView(text(desc, 11, R.color.mc_muted, false), margins(0, 3, 0, 0));
        head.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
        Switch sw = new Switch(this);
        sw.setChecked(Prefs.get(this).getBoolean(key, defaultFor(key)));
        sw.setOnCheckedChangeListener((buttonView, checked) -> {
            Prefs.get(this).edit().putBoolean(key, checked).apply();
            if (startsService) {
                if (checked) startOverlay(); else stopOverlay();
            }
            if (Prefs.ZOOM.equals(key)) handleZoom(checked);
        });
        head.addView(sw);
        card.addView(head);
        content.addView(card, margins(0, 0, 0, 8));
    }

    private boolean defaultFor(String key) {
        if (Prefs.OVERLAY.equals(key)) return false;
        if (Prefs.THERMAL_GUARD.equals(key)) return true;
        if (Prefs.MINIMAL_HUD.equals(key)) return false;
        if (Prefs.ZOOM.equals(key)) return false;
        return false;
    }

    private void addZoomLevel() {
        LinearLayout card = card();
        card.addView(text("Zoom level", 15, R.color.mc_text, true));
        int current = Prefs.get(this).getInt(Prefs.ZOOM_LEVEL, 2);
        TextView value = text("Scale: " + current + "×", 12, R.color.mc_muted, false);
        card.addView(value, margins(0, 4, 0, 3));
        SeekBar bar = new SeekBar(this);
        bar.setMax(6);
        bar.setProgress(Math.max(1, Math.min(6, current)) - 1);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int scale = progress + 1;
                value.setText("Scale: " + scale + "×");
                Prefs.get(MainActivity.this).edit().putInt(Prefs.ZOOM_LEVEL, scale).apply();
                if (Prefs.get(MainActivity.this).getBoolean(Prefs.ZOOM, false)) handleZoom(true);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        card.addView(bar);
        content.addView(card, margins(0, 0, 0, 8));
    }

    private void addFpsTarget() {
        LinearLayout card = card();
        card.addView(text("FPS target / Uncap", 15, R.color.mc_text, true));
        card.addView(text("Chỉ lưu mục tiêu bạn muốn dùng trong engine module. Launcher không giả lập FPS và không ghi giá trị FPS giả.",
                11, R.color.mc_muted, false), margins(0, 4, 0, 8));
        String[] values = {"60 FPS", "90 FPS", "120 FPS", "Max / Uncap"};
        int[] numbers = {60, 90, 120, 0};
        LinearLayout chips = row();
        for (int i = 0; i < values.length; i++) {
            final int n = numbers[i];
            Button b = button(values[i]);
            b.setOnClickListener(v -> {
                Prefs.get(this).edit().putInt(Prefs.FPS_TARGET, n).apply();
                toast("Target saved: " + (n == 0 ? "Uncap" : n + " FPS"));
            });
            chips.addView(b, weightParams(1, i == 0 ? 0 : 6));
        }
        card.addView(chips);
        content.addView(card, margins(0, 0, 0, 8));
    }

    private void addEngineSection() {
        TextView t = text("Engine-side integration", 22, R.color.mc_text, true);
        t.setTag("engine");
        content.addView(t, margins(0, 8, 0, 6));
        TextView note = text("Các mục dưới đây cần một module chạy bên trong/đồng bộ với engine của Minecraft. Launcher độc lập không thể bật chúng trực tiếp.",
                12, R.color.mc_muted, false);
        content.addView(note, margins(0, 0, 0, 10));

        addEngineRow("Render Culling", "Frustum/render culling thực sự trong renderer", "Requires engine module");
        addEngineRow("Entity Culling", "Bỏ qua entity không cần render", "Requires engine module");
        addEngineRow("Particle Culling", "Cắt particle ngoài vùng cần thiết", "Requires engine module");
        addEngineRow("Occlusion Culling", "Ẩn geometry bị che khuất", "Requires engine module");
        addEngineRow("True FPS Counter", "Đọc frame timing từ engine Minecraft", "Requires engine module");
        addEngineRow("True Uncap FPS", "Thay đổi frame cap nội bộ của game", "Requires engine module");
        addEngineRow("In-game Render Distance", "Thay đổi render distance từ engine", "Requires engine module");

        LinearLayout architecture = card();
        architecture.addView(text("Kiến trúc đề xuất", 15, R.color.mc_text, true));
        architecture.addView(text("Launcher → xác thực Minecraft chính thức → profile/permission/overlay\nEngine module → renderer/frame pacing/culling → trả capability + metrics về launcher", 12, R.color.mc_muted, false),
                margins(0, 6, 0, 0));
        content.addView(architecture, margins(0, 0, 0, 16));
    }

    private void addEngineRow(String title, String desc, String status) {
        LinearLayout card = card();
        LinearLayout head = row();
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.addView(text(title, 14, R.color.mc_text, true));
        labels.addView(text(desc, 11, R.color.mc_muted, false), margins(0, 3, 0, 0));
        head.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
        TextView badge = text(status, 10, R.color.mc_warning, true);
        head.addView(badge);
        card.addView(head);
        content.addView(card, margins(0, 0, 0, 8));
    }

    private void addToolsSection() {
        TextView t = text("Tools & permissions", 22, R.color.mc_text, true);
        t.setTag("tools");
        content.addView(t, margins(0, 8, 0, 6));

        LinearLayout card = card();
        Button overlay = button("Overlay permission");
        overlay.setOnClickListener(v -> openOverlaySettings());
        card.addView(overlay);
        Button accessibility = button("Accessibility / Zoom settings");
        accessibility.setOnClickListener(v -> openAccessibilitySettings());
        card.addView(accessibility, margins(0, 8, 0, 0));
        Button battery = button("Battery optimization settings");
        battery.setOnClickListener(v -> requestBatteryOptimization());
        card.addView(battery, margins(0, 8, 0, 0));
        Button display = button("Display refresh-rate settings");
        display.setOnClickListener(v -> openDisplaySettings());
        card.addView(display, margins(0, 8, 0, 0));
        content.addView(card, margins(0, 0, 0, 16));
    }

    private void addAboutSection() {
        LinearLayout card = card();
        card.addView(text("About", 16, R.color.mc_text, true));
        card.addView(text("MCBoost Launcher 1.2.0\nPackage: " + getPackageName() + "\nOfficial Minecraft only: com.mojang.minecraftpe\nNo root, no native hooking, no fake FPS.",
                12, R.color.mc_muted, false), margins(0, 6, 0, 0));
        content.addView(card);
    }

    private void launchMinecraft() {
        if (!DeviceInfo.isSupportedMinecraft(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Minecraft 26.0.x not detected")
                    .setMessage("MCBoost chỉ khởi chạy bản Minecraft chính thức com.mojang.minecraftpe có version 26.0.x.\n\nĐã phát hiện: " + DeviceInfo.minecraftVersion(this))
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Mở cài đặt ứng dụng", (d, w) -> openMinecraftAppSettings())
                    .show();
            return;
        }
        if (Prefs.get(this).getBoolean(Prefs.OVERLAY, false)) startOverlay();
        Intent intent = getPackageManager().getLaunchIntentForPackage(DeviceInfo.MINECRAFT_PACKAGE);
        if (intent == null) {
            toast("Không tìm thấy màn hình khởi chạy Minecraft");
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (RuntimeException e) {
            toast("Không thể mở Minecraft: " + e.getMessage());
        }
    }

    private void startOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            toast("Hãy cấp quyền Overlay trước");
            openOverlaySettings();
            Prefs.get(this).edit().putBoolean(Prefs.OVERLAY, false).apply();
            return;
        }
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1201);
        }
        try {
            startForegroundService(new Intent(this, OverlayService.class));
            toast("HUD started");
        } catch (RuntimeException e) {
            toast("Không thể khởi động HUD: " + e.getMessage());
        }
    }

    private void stopOverlay() {
        stopService(new Intent(this, OverlayService.class));
        toast("HUD stopped");
    }

    private void handleZoom(boolean enabled) {
        if (!enabled) {
            BoostAccessibilityService.resetZoom();
            return;
        }
        if (!AccessibilityState.isEnabled(this) && !BoostAccessibilityService.isConnected()) {
            toast("Hãy bật MCBoost Accessibility trước");
            openAccessibilitySettings();
            Prefs.get(this).edit().putBoolean(Prefs.ZOOM, false).apply();
            return;
        }
        int scale = Prefs.get(this).getInt(Prefs.ZOOM_LEVEL, 2);
        if (!BoostAccessibilityService.setZoom(scale)) {
            toast("Accessibility service chưa sẵn sàng");
        }
    }

    private void openOverlaySettings() {
        try {
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(i);
        } catch (RuntimeException e) {
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
        }
    }

    private void openAccessibilitySettings() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    private void openDisplaySettings() {
        try { startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS)); }
        catch (RuntimeException e) { toast("Thiết bị không cung cấp trang Display settings"); }
    }

    private void requestBatteryOptimization() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            toast("Android này không cần mục này");
            return;
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName())) {
            toast("MCBoost đã được miễn Battery Optimization");
            return;
        }
        try {
            Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(i);
        } catch (RuntimeException e) {
            startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
        }
    }

    private void openMinecraftAppSettings() {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + DeviceInfo.MINECRAFT_PACKAGE));
        startActivity(i);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1201);
        }
    }

    private LinearLayout card() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(14), dp(14), dp(14), dp(14));
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(getColorSafe(R.color.mc_surface));
        bg.setCornerRadius(dp(16));
        layout.setBackground(bg);
        return layout;
    }

    private LinearLayout row() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        return row;
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(getColorSafe(color));
        v.setIncludeFontPadding(true);
        if (bold) v.setTypeface(null, android.graphics.Typeface.BOLD);
        return v;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(12f);
        b.setTextColor(getColorSafe(R.color.mc_text));
        b.setMinHeight(dp(42));
        return b;
    }

    private LinearLayout.LayoutParams margins(int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(dp(l), dp(t), dp(r), dp(b));
        return p;
    }

    private LinearLayout.LayoutParams weightParams(float weight, int left) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, weight);
        p.setMargins(dp(left), 0, 0, 0);
        return p;
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
