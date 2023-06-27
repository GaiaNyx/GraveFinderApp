package com.example.gravefinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (isAirplaneModeOn(context.getApplicationContext())) {
            Toast.makeText(context, "Attention! You are on flight mode so you can't see the graves information!", Toast.LENGTH_LONG).show();
            Log.d("MyBroadcastReceiver", intent.getAction());
        }
    }

    private static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON
                , 0) != 0;
    }
}
