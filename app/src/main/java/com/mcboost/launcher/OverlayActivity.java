package com.mcboost.launcher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayActivity extends Activity {
    private LinearLayout page;
    @Override protected void onCreate(Bundle b){ super.onCreate(b); build(); }
    @Override protected void onResume(){ super.onResume(); }
    private void build(){
        ScrollView s=new ScrollView(this); page=Ui.page(this); s.addView(page); setContentView(s);
        TextView back=Ui.action(this,"‹  Back"); back.setOnClickListener(v->finish()); page.addView(back);
        page.addView(Ui.title(this,"Overlay"));
        page.addView(Ui.subtitle(this,"Lightweight HUD for Minecraft. FPS/CPS remain unavailable unless an in-game instrumentation module supplies them."));
        LinearLayout svc=Ui.card(this);
        android.widget.Switch overlay=Ui.toggle(this,"Overlay service","Floating HUD requires Display over other apps.",Prefs.get(this).getBoolean(Prefs.OVERLAY,false),(b,checked)->{ Prefs.get(this).edit().putBoolean(Prefs.OVERLAY,checked).apply(); if(checked){ if(!Settings.canDrawOverlays(this)) openOverlayPermission(); else startOverlay(); } else stopOverlay(); });
        svc.addView(overlay); page.addView(svc);
        page.addView(Ui.action(this,"Grant Display over other apps permission ↗")); page.getChildAt(page.getChildCount()-1).setOnClickListener(v->openOverlayPermission());
        page.addView(section("HUD modules"));
        addToggle("FPS","Shows game FPS only when supplied by a compatible in-game module.",Prefs.get(this).getBoolean(Prefs.FPS,true),Prefs.FPS);
        addToggle("1% Low FPS","Shows frame-time percentiles only when instrumentation is available.",false,"_low");
        addToggle("CPS","Clicks per second from Minecraft input requires instrumentation; launcher cannot intercept it safely.",Prefs.get(this).getBoolean(Prefs.CPS,true),Prefs.CPS);
        addToggle("Zoom","Use Android Accessibility Magnification.",Prefs.get(this).getBoolean(Prefs.ZOOM,true),Prefs.ZOOM);
        addToggle("Refresh rate","Show the device display mode / Hz (not Minecraft FPS).",true,"_hz");
        page.addView(Ui.action(this,"Open Accessibility settings for Zoom ↗")); page.getChildAt(page.getChildCount()-1).setOnClickListener(v->{ startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)); });
    }
    private TextView section(String s){ TextView t=new TextView(this); t.setText(s); t.setTextColor(0xFFF1FFF6); t.setTextSize(18); t.setTypeface(null,1); t.setPadding(0,Ui.dp(this,18),0,Ui.dp(this,3)); return t; }
    private void addToggle(String title,String desc,boolean value,String key){ LinearLayout c=Ui.card(this); android.widget.Switch sw=Ui.toggle(this,title,desc,value,(b,checked)->{ if(!key.startsWith("_")) Prefs.get(this).edit().putBoolean(key,checked).apply(); }); c.addView(sw); page.addView(c); }
    private void openOverlayPermission(){ if(Build.VERSION.SDK_INT>=23){ try{ startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()))); }catch(Exception e){ startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)); } } }
    private void startOverlay(){ try{ Intent i=new Intent(this,OverlayService.class); if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);}catch(Exception e){Toast.makeText(this,"Overlay could not start: "+e.getMessage(),Toast.LENGTH_LONG).show();} }
    private void stopOverlay(){ stopService(new Intent(this,OverlayService.class)); }
}
