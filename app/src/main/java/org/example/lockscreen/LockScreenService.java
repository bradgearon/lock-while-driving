package org.example.lockscreen;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockScreenService extends Service {

  BroadcastReceiver receiver;

  @Override
  public void onCreate() {
    Log.d("com.wds.untitled2", "on create lock screen service");

    Intent backToFullScreenActivity = new Intent(this, Main2Activity.class);
    backToFullScreenActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(backToFullScreenActivity);

    KeyguardManager.KeyguardLock key;
    KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

    //This is deprecated, but it is a simple way to disable the lock screen in code
    key = km.newKeyguardLock("IN");

    key.disableKeyguard();

    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    filter.addAction(Intent.ACTION_SCREEN_OFF);

    //Set up a receiver to listen for the Intents in this Service
    receiver = new LockScreenReceiver();
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
