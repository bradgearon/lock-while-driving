package org.example.lockscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Main2Activity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("com.wds.untitled2", "on create");
    overridePendingTransition(0, 0);
  }

  @Override
  protected void onResume() {
    super.onResume();

    Log.d("com.wds.untitled2", "starting ranging service");
    Intent rangingIntent = new Intent(this, RangingService.class);
    startService(rangingIntent);

    Intent backToFullScreenActivity = new Intent(this, FullscreenActivity.class);
    backToFullScreenActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(backToFullScreenActivity);
    overridePendingTransition(0, 0);

    Handler handler = new Handler(getMainLooper());
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        finish();
      }
    }, 1);
  }


}
