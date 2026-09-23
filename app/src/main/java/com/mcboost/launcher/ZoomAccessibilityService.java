package com.mcboost.launcher;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityService.MagnificationController;
import android.os.Build;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class ZoomAccessibilityService extends AccessibilityService {
    private static ZoomAccessibilityService instance;
    private MagnificationController controller;
    private float scale = 1f;

    @Override protected void onServiceConnected(){
        super.onServiceConnected();
        instance=this;
        if(Build.VERSION.SDK_INT>=24) controller=getMagnificationController();
    }
    @Override public void onDestroy(){ if(instance==this) instance=null; super.onDestroy(); }
    @Override public void onAccessibilityEvent(AccessibilityEvent event){}
    @Override public void onInterrupt(){}

    @Override public boolean onKeyEvent(KeyEvent event){
        if(event.getAction()!=KeyEvent.ACTION_DOWN) return false;
        if(event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_UP){ zoomIn(); return true; }
        if(event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN){ zoomOut(); return true; }
        return false;
    }

    static boolean zoomIn(){ return instance!=null && instance.setZoom(instance.scale+0.5f); }
    static boolean zoomOut(){ return instance!=null && instance.setZoom(instance.scale-0.5f); }
    static boolean reset(){ return instance!=null && instance.resetZoom(); }

    private boolean setZoom(float value){
        if(Build.VERSION.SDK_INT<24 || controller==null) return false;
        scale=Math.max(1f,Math.min(value,4f));
        try{ return controller.setScale(scale, true); }catch(Exception e){return false;}
    }
    private boolean resetZoom(){
        if(Build.VERSION.SDK_INT<24 || controller==null) return false;
        scale=1f;
        try{ return controller.reset(true); }catch(Exception e){return false;}
    }
}
