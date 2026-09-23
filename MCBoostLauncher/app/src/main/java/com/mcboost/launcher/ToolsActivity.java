package com.mcboost.launcher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ToolsActivity extends Activity {
    private LinearLayout page;
    @Override protected void onCreate(Bundle b){super.onCreate(b); build();}
    private void build(){
        ScrollView s=new ScrollView(this); page=Ui.page(this); s.addView(page); setContentView(s);
        TextView back=Ui.action(this,"‹  Back"); back.setOnClickListener(v->finish()); page.addView(back);
        page.addView(Ui.title(this,"Permissions & tools"));
        page.addView(Ui.subtitle(this,"Everything is optional and handled through Android system settings."));
        add("Display over other apps","Required for the floating HUD.",v->{if(Build.VERSION.SDK_INT>=23)startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())));});
        add("Accessibility → MCBoost Zoom","Required only for zoom control.",v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        add("Battery optimization","Allows longer-lived overlay service; Android may decline this request.",v->{PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);if(!pm.isIgnoringBatteryOptimizations(getPackageName())&&Build.VERSION.SDK_INT>=23){try{startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,Uri.parse("package:"+getPackageName())));}catch(Exception e){startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));}}else Toast.makeText(this,"Battery optimization is already ignored.",Toast.LENGTH_SHORT).show();});
        add("Open Minecraft app info","Useful for checking official installation/version.",v->startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+MinecraftInfo.PACKAGE))));
    }
    private void add(String title,String desc,android.view.View.OnClickListener click){LinearLayout c=Ui.card(this); TextView t=Ui.action(this,title+"\n"+desc); t.setOnClickListener(click); c.addView(t); page.addView(c);}
}
