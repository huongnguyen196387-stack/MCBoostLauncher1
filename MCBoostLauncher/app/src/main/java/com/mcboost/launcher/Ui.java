package com.mcboost.launcher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Switch;

final class Ui {
    static LinearLayout page(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(c, 18), dp(c, 14), dp(c, 18), dp(c, 24));
        l.setBackgroundColor(Color.rgb(7,18,14));
        return l;
    }
    static TextView title(Context c, String s) {
        TextView t = new TextView(c); t.setText(s); t.setTextColor(Color.rgb(241,255,246)); t.setTextSize(25); t.setTypeface(null, 1); t.setPadding(0,0,0,dp(c,8)); return t;
    }
    static TextView subtitle(Context c, String s) {
        TextView t = new TextView(c); t.setText(s); t.setTextColor(Color.rgb(159,180,168)); t.setTextSize(13); t.setPadding(0,0,0,dp(c,12)); return t;
    }
    static LinearLayout card(Context c) {
        LinearLayout l = new LinearLayout(c); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(dp(c,15), dp(c,13), dp(c,15), dp(c,13)); l.setBackground(round(0xFF0D1B15,0xFF204333,18));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); p.setMargins(0,dp(c,8),0,0); l.setLayoutParams(p); return l;
    }
    static Switch toggle(Context c, String title, String desc, boolean value, CompoundButton.OnCheckedChangeListener listener) {
        Switch sw = new Switch(c); sw.setText(title + "\n" + desc); sw.setTextColor(Color.rgb(241,255,246)); sw.setTextSize(15); sw.setPadding(0,0,0,0); sw.setChecked(value); sw.setOnCheckedChangeListener(listener); return sw;
    }
    static TextView action(Context c, String text) {
        TextView t = new TextView(c); t.setText(text); t.setTextColor(Color.rgb(241,255,246)); t.setTextSize(15); t.setPadding(dp(c,15),dp(c,15),dp(c,15),dp(c,15)); t.setBackground(round(0xFF0D1B15,0xFF204333,16)); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); p.setMargins(0,dp(c,8),0,0); t.setLayoutParams(p); return t;
    }
    static GradientDrawable round(int fill, int stroke, int radius) { GradientDrawable g=new GradientDrawable(); g.setColor(fill); g.setStroke(1,stroke); g.setCornerRadius(radius); return g; }
    static int dp(Context c,int v){ return (int)(v*c.getResources().getDisplayMetrics().density+0.5f); }
}
