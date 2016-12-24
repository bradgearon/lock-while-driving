package org.example.lockscreen;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import static android.content.Context.KEYGUARD_SERVICE;

public class LockScreenReceiver extends BroadcastReceiver {
  Handler handler;
  Context context;
  private Thread thread;

  public LockScreenReceiver() {
    thread = new Thread(new Runnable() {

      public void run() {
        try {
          while (true) {
            if (FullscreenActivity.getContext() == null
              || FullscreenActivity.isActivityVisible()
              || !RangingService.isInRange()) {
              Thread.sleep(500);
              continue;
            }

            Log.d("com.wds.untitled2", "!hasWindowFocus");
            Intent backToFullScreenActivity = new Intent(FullscreenActivity.getContext(), Main2Activity.class);
            backToFullScreenActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            FullscreenActivity.getContext().startActivity(backToFullScreenActivity);

            Thread.sleep(500);
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

    if (handler == null) {
      handler = new Handler(context.getMainLooper());
    }

    Log.d("com.wds.untitled2", "screen turned on");
    Intent i = new Intent(context, Main2Activity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    context.startActivity(i);

    handler.post(new Runnable() {
      @Override
      public void run() {
        Log.d("com.wds.untitled2", "screen turned on");
        Intent i = new Intent(context, Main2Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
      }
    });


    if (action.equals(Intent.ACTION_SCREEN_OFF)) {
      try {
        thread.interrupt();
      } catch (Exception ignored) {
      }
    }

    if (action.equals(Intent.ACTION_SCREEN_ON)) {
      if (!thread.isAlive()) {
        thread.start();
      }
    }
  }
}
