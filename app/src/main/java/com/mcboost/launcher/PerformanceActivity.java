package com.mcboost.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class PerformanceActivity extends Activity {
    private LinearLayout page;
    private TextView note;
    @Override protected void onCreate(Bundle b){ super.onCreate(b); build(); }

    private void build(){
        ScrollView scroll = new ScrollView(this); page = Ui.page(this); scroll.addView(page); setContentView(scroll);
        TextView back=Ui.action(this,"‹  Back"); back.setOnClickListener(v->finish()); page.addView(back);
        page.addView(Ui.title(this,"Performance"));
        page.addView(Ui.subtitle(this,"System-side optimization plus honest Bedrock engine status."));

        LinearLayout profileCard=Ui.card(this); TextView h=new TextView(this); h.setText("Performance profile"); h.setTextColor(0xFFF1FFF6); h.setTextSize(16); h.setTypeface(null,1); profileCard.addView(h);
        RadioGroup rg=new RadioGroup(this); rg.setOrientation(RadioGroup.VERTICAL);
        String[] names={"Balanced","Low latency","Battery saver"};
        String current=Prefs.get(this).getString(Prefs.PERF,"Balanced");
        for(String name:names){ RadioButton rb=new RadioButton(this); rb.setText(name); rb.setTextColor(0xFFF1FFF6); rb.setTextSize(15); rb.setChecked(current.equals(name)); rb.setOnClickListener(v->{ Prefs.get(this).edit().putString(Prefs.PERF,((RadioButton)v).getText().toString()).apply(); }); rg.addView(rb); }
        profileCard.addView(rg); page.addView(profileCard);

        page.addView(section("Real Android optimizations"));
        addToggle("Thermal Guard","Monitor device thermal state and reduce MCBoost overhead when hot.",Prefs.get(this).getBoolean(Prefs.THERMAL,true),Prefs.THERMAL);
        addToggle("Overlay efficiency","Keep HUD lightweight; frame pacing is measured by the overlay itself.",true,"_overlay_eff");
        addToggle("Battery optimization exception","Allow MCBoost service to stay active during play. Android may show a system confirmation.",false,"_battery");

        page.addView(section("Bedrock engine hooks"));
        page.addEngineInfo("Render Culling","Not directly controllable from a companion launcher. Requires an in-game render module.",false);
        page.addEngineInfo("Entity / Particle Culling","Same limitation: Minecraft's renderer owns these decisions.",false);
        page.addEngineInfo("Occlusion Culling","Requires access to Bedrock's render pipeline; launcher-only mode cannot inject it.",false);
        page.addEngineInfo("Chunk / World Render Optimization","Can be approximated by lowering render/simulation distance, but true culling is engine-side.",false);

        page.addView(section("FPS control"));
        LinearLayout fps=Ui.card(this);
        TextView fh=new TextView(this); fh.setText("Uncap FPS"); fh.setTextColor(0xFFF1FFF6); fh.setTextSize(16); fh.setTypeface(null,1); fps.addView(fh);
        TextView fd=new TextView(this); fd.setText("MCBoost records your preference, but a normal companion app cannot override Minecraft Bedrock's internal frame-rate cap. Use Minecraft's Video settings for the actual cap."); fd.setTextColor(0xFF9FB4A8); fd.setTextSize(13); fd.setPadding(0,Ui.dp(this,5),0,Ui.dp(this,8)); fps.addView(fd);
        android.widget.Switch uncap=Ui.toggle(this,"Request uncapped","Show Uncap as your selected target.",Prefs.get(this).getBoolean(Prefs.UNCAP,false),(b,checked)->Prefs.get(this).edit().putBoolean(Prefs.UNCAP,checked).apply()); fps.addView(uncap);
        TextView open=Ui.action(this,"Open Minecraft Video settings"); open.setOnClickListener(v->launchMinecraft()); fps.addView(open);
        page.addView(fps);

        note=new TextView(this); note.setTextColor(0xFF8BFFC0); note.setTextSize(13); note.setPadding(0,Ui.dp(this,14),0,0); page.addView(note); updateThermalNote();
    }

    private TextView section(String s){ TextView t=new TextView(this); t.setText(s); t.setTextColor(0xFFF1FFF6); t.setTextSize(18); t.setTypeface(null,1); t.setPadding(0,Ui.dp(this,18),0,Ui.dp(this,3)); return t; }
    private void addToggle(String title,String desc,boolean value,String key){ LinearLayout c=Ui.card(this); android.widget.Switch sw=Ui.toggle(this,title,desc,value,(b,checked)->{if(!"_overlay_eff".equals(key))Prefs.get(this).edit().putBoolean(key,checked).apply(); if("_battery".equals(key)&&checked)requestBatteryExemption();}); c.addView(sw); page.addView(c); }
    private void addEngineInfo(String title,String desc,boolean enabled){ LinearLayout c=Ui.card(this); android.widget.Switch sw=Ui.toggle(this,title,desc,false,(b,checked)->{}); sw.setEnabled(enabled); c.addView(sw); page.addView(c); }
    private void requestBatteryExemption(){ PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE); if(!pm.isIgnoringBatteryOptimizations(getPackageName())){ try{ startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, android.net.Uri.parse("package:"+getPackageName()))); }catch(Exception e){ startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)); } } }
    private void launchMinecraft(){ Intent i=getPackageManager().getLaunchIntentForPackage(MinecraftInfo.PACKAGE); if(i!=null) startActivity(i); else android.widget.Toast.makeText(this,"Install official Minecraft first",android.widget.Toast.LENGTH_LONG).show(); }
    private void updateThermalNote(){ PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE); String state="Thermal state: unavailable"; if(android.os.Build.VERSION.SDK_INT>=29){ int s=pm.getCurrentThermalStatus(); state="Thermal state: "+thermalName(s); } note.setText(state); }
    private String thermalName(int s){ switch(s){case PowerManager.THERMAL_STATUS_NONE:return "NORMAL";case PowerManager.THERMAL_STATUS_LIGHT:return "LIGHT";case PowerManager.THERMAL_STATUS_MODERATE:return "MODERATE";case PowerManager.THERMAL_STATUS_SEVERE:return "SEVERE";case PowerManager.THERMAL_STATUS_CRITICAL:return "CRITICAL";default:return "EMERGENCY";} }
}
