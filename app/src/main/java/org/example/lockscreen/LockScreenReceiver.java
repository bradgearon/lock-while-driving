package org.example.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import static android.content.Context.POWER_SERVICE;
import static com.unity3d.ads.properties.ClientProperties.getApplicationContext;

public class LockScreenReceiver extends BroadcastReceiver {
  private final Thread thread;
  Handler handler;
  Context context;

  public LockScreenReceiver() {
    thread = new Thread(new Runnable() {
      public void run() {
        try {
          while (true) {
            if(FullscreenActivity.getContext() == null || FullscreenActivity.isActivityVisible()) {
              Thread.sleep(500);
              continue;
            }
            Thread.sleep(100);
            Log.d("com.wds.untitled2", "!hasWindowFocus");
            Intent backToFullScreenActivity = new Intent(FullscreenActivity.getContext(), Main2Activity.class);
            backToFullScreenActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            FullscreenActivity.getContext().startActivity(backToFullScreenActivity);
          }

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    thread.start();

  }

  @Override
  public void onReceive(final Context context, Intent intent) {
    String action = intent.getAction();
    this.context = context;
    if(handler == null) {
      handler = new Handler(context.getMainLooper());
    }

    if(action.equals(Intent.ACTION_SCREEN_OFF)) {
      thread.interrupt();
    } else if (action.equals(Intent.ACTION_SCREEN_ON) || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
      thread.start();
    }

    if (action.equals(Intent.ACTION_SCREEN_ON) || action.equals(Intent.ACTION_SCREEN_OFF)
      || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
      handler.post(new Runnable() {
        @Override
        public void run() {
          Log.d("com.wds.untitled2", "screen turned on");
          Intent i = new Intent(context, Main2Activity.class);
          i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          context.startActivity(i);
        }
      });
    }
  }
}
