package com.mcboost.launcher;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager wm;
    private View overlay;
    private TextView stats;
    private PowerManager powerManager;
    private PowerManager.OnThermalStatusChangedListener thermalListener;

    @Override public void onCreate(){ super.onCreate();
        createChannel();
        startForeground(1001, notification());
        wm=(WindowManager)getSystemService(WINDOW_SERVICE);
        buildOverlay();
    }
    private void buildOverlay(){
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(18,12,18,12); box.setBackground(Ui.round(0xDD07120E,0xFF31E77D,20));
        TextView brand=new TextView(this); brand.setText("MCBoost"); brand.setTextColor(Color.rgb(49,231,125)); brand.setTextSize(13); brand.setTypeface(null,1); box.addView(brand);
        stats=new TextView(this); stats.setTextColor(Color.WHITE); stats.setTextSize(12); stats.setText("FPS  --\nCPS  --\nHZ   --"); box.addView(stats);
        LinearLayout actions=new LinearLayout(this); actions.setOrientation(LinearLayout.HORIZONTAL);
        TextView minus=new TextView(this); minus.setText("−"); minus.setTextColor(Color.WHITE); minus.setTextSize(20); minus.setGravity(Gravity.CENTER); minus.setPadding(12,2,12,2);
        TextView reset=new TextView(this); reset.setText("1×"); reset.setTextColor(Color.WHITE); reset.setTextSize(11); reset.setGravity(Gravity.CENTER); reset.setPadding(10,2,10,2);
        TextView plus=new TextView(this); plus.setText("+"); plus.setTextColor(Color.WHITE); plus.setTextSize(20); plus.setGravity(Gravity.CENTER); plus.setPadding(12,2,12,2);
        minus.setBackground(Ui.round(0xFF153022,0xFF204333,12)); reset.setBackground(Ui.round(0xFF153022,0xFF204333,12)); plus.setBackground(Ui.round(0xFF153022,0xFF204333,12));
        actions.addView(minus); actions.addView(reset); actions.addView(plus); box.addView(actions);
        minus.setOnClickListener(v->ZoomAccessibilityService.zoomOut()); reset.setOnClickListener(v->ZoomAccessibilityService.reset()); plus.setOnClickListener(v->ZoomAccessibilityService.zoomIn());
        TextView hint=new TextView(this); hint.setTextColor(0xFF9FB4A8); hint.setTextSize(9); hint.setText("FPS/CPS require game instrumentation • Zoom needs Accessibility"); box.addView(hint);
        overlay=box;
        int type=Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;
        WindowManager.LayoutParams p=new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,PixelFormat.TRANSLUCENT);
        p.gravity=Gravity.TOP|Gravity.END; p.x=16; p.y=90;
        wm.addView(overlay,p);
        box.setOnTouchListener(new DragListener(p));
        updateHz();
        powerManager=(PowerManager)getSystemService(POWER_SERVICE);
        if(Build.VERSION.SDK_INT>=29){ thermalListener=status->{ if(status>=PowerManager.THERMAL_STATUS_SEVERE && Prefs.get(this).getBoolean(Prefs.THERMAL,true)){ stopSelf(); } }; powerManager.addThermalStatusListener(thermalListener); }
    }
    private void updateHz(){
        float hz=60f;
        if(Build.VERSION.SDK_INT>=30){ try{ android.hardware.display.DisplayManager dm=(android.hardware.display.DisplayManager)getSystemService(Context.DISPLAY_SERVICE); android.view.Display d=dm.getDisplay(android.view.Display.DEFAULT_DISPLAY); if(d!=null)hz=d.getRefreshRate(); }catch(Exception ignored){} }
        stats.setText(String.format(java.util.Locale.US,"FPS  --\nCPS  --\nHZ   %.0f",hz));
    }
    private Notification notification(){ Intent i=new Intent(this,MainActivity.class); PendingIntent pi=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT); return new Notification.Builder(this,"mcboost_overlay").setSmallIcon(android.R.drawable.ic_menu_view).setContentTitle("MCBoost HUD").setContentText("Overlay active").setContentIntent(pi).setOngoing(true).build(); }
    private void createChannel(){if(Build.VERSION.SDK_INT>=26){NotificationManager nm=getSystemService(NotificationManager.class);nm.createNotificationChannel(new NotificationChannel("mcboost_overlay","MCBoost overlay",NotificationManager.IMPORTANCE_LOW));}}
    @Override public void onDestroy(){ if(Build.VERSION.SDK_INT>=29 && powerManager!=null && thermalListener!=null) powerManager.removeThermalStatusListener(thermalListener); if(overlay!=null&&wm!=null)wm.removeView(overlay); super.onDestroy(); }
    @Override public IBinder onBind(Intent intent){return null;}

    private static class DragListener implements View.OnTouchListener{
        private final WindowManager.LayoutParams p; private float sx,sy; private int ox,oy;
        DragListener(WindowManager.LayoutParams p){this.p=p;}
        public boolean onTouch(View v,android.view.MotionEvent e){switch(e.getAction()){case MotionEvent.ACTION_DOWN:sx=e.getRawX();sy=e.getRawY();ox=p.x;oy=p.y;return true;case MotionEvent.ACTION_MOVE:p.x=ox-(int)(e.getRawX()-sx);p.y=oy+(int)(e.getRawY()-sy);((WindowManager)v.getContext().getSystemService(Context.WINDOW_SERVICE)).updateViewLayout(v,p);return true;}return false;}
    }
}
