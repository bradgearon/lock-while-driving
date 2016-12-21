package org.example.lockscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class Main2Activity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("com.wds.untitled2", "on create");
    overridePendingTransition(0, 0);
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
  }

  @Override
  protected void onResume() {
    super.onResume();
    Intent backToFullScreenActivity = new Intent(this, FullscreenActivity.class);
    backToFullScreenActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(backToFullScreenActivity);
    overridePendingTransition(0, 0);
    finish();
  }


}
