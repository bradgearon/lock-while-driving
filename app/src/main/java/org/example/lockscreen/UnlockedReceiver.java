package org.example.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class UnlockedReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(final Context context, Intent intent) {
    Log.d("com.wds.untitled2", "unlocked receiver on receive");

    Intent backToFullScreenActivity = new Intent(context, Main2Activity.class);
    backToFullScreenActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    context.startActivity(backToFullScreenActivity);
  }
}
