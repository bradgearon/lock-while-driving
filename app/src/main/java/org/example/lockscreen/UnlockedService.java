package org.example.lockscreen;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class UnlockedService extends Service {

  BroadcastReceiver receiver;

  @Override
  public void onCreate() {
    Log.d("com.wds.untitled2", "on create unlocked service");

    IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_USER_PRESENT);

    //Set up a receiver to listen for the Intents in this Service
    receiver = new UnlockedReceiver();
    registerReceiver(receiver, filter);

    super.onCreate();
  }


  @Override
  public void onDestroy() {
    unregisterReceiver(receiver);
    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
