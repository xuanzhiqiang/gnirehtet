package com.genymobile.gnirehtet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class USBReceiver extends BroadcastReceiver {

    private Callback callback;
    private int mPlugType;

    interface Callback
    {
        void usbChange(boolean state);
    }

    public USBReceiver(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {// 判断USB插入拔出
            final int oldPlugType = mPlugType;
            mPlugType = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 1);
            final boolean plugged = mPlugType != 0;
            final boolean oldPlugged = oldPlugType != 0;
            if ((!plugged) && (oldPlugged != plugged)) {
                //USB被拔出
                Log.i("USBReceiver", "拔出USB设备");
                callback.usbChange(false);
            } else if (plugged) {
                if (oldPlugged != plugged ) {
                    //USB插入
                    Log.i("USBReceiver", "插入USB设备");
                    callback.usbChange(true);
                }
            }
        }
    }


    public static USBReceiver register(Context context, Callback callback) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        USBReceiver usbReceiver = new USBReceiver(callback);
        context.registerReceiver(usbReceiver, filter);
        return usbReceiver;
    }

    public static void unRegister(Context context, USBReceiver usbReceiver) {
        context.unregisterReceiver(usbReceiver);
    }

}
