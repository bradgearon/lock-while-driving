package org.example.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(final Context context, Intent intent) {
    Log.d("com.wds.untitled2", "startup received");

    Intent unlockedService = new Intent(context, UnlockedService.class);
    context.startService(unlockedService);
  }
}
