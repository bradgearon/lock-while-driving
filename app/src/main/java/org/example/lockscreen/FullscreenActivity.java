package org.example.lockscreen;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.adunit.AdUnitActivity;
import com.unity3d.ads.api.AdUnit;
import com.unity3d.ads.properties.ClientProperties;

import java.util.List;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends Activity {
  private static final boolean AUTO_HIDE = true;
  private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

  /**
   * Some older devices needs a small delay between UI widget updates
   * and a change of the status and navigation bar.
   */
  private static final int UI_ANIMATION_DELAY = 300;
  private final Handler mHideHandler = new Handler();
  private View mContentView;
  private final Runnable mHidePart2Runnable = new Runnable() {
    @SuppressLint("InlinedApi")
    @Override
    public void run() {
      mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


      moveToFront();
    }
  };
  private View mControlsView;
  private final Runnable mShowPart2Runnable = new Runnable() {
    @Override
    public void run() {
      // Delayed display of UI elements
      ActionBar actionBar = getActionBar();
      if (actionBar != null) {
        actionBar.show();
      }
      mControlsView.setVisibility(View.VISIBLE);
    }
  };
  private boolean mVisible;
  private final Runnable mHideRunnable = new Runnable() {
    @Override
    public void run() {
      hide();
    }
  };
  private GestureDetectorCompat mDetector;
  private Random rand = new Random();
  private Thread thread;
  private static boolean showing;

  public static boolean isActivityVisible() {
    return activityVisible;
  }

  public static void activityResumed() {
    activityVisible = true;
  }

  public static void activityPaused() {
    activityVisible = false;
  }

  private static boolean activityVisible = false;
  private static Context thisContext;

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    activityVisible = hasFocus;
  }

  public static Context getContext() {
    return thisContext;
  }

  private void moveToFront() {
    ActivityManager activityManager = (ActivityManager) getApplicationContext()
      .getSystemService(Context.ACTIVITY_SERVICE);
    activityManager.moveTaskToFront(getTaskId(), 0);
    overridePendingTransition(0, 0);
  }

  public void makeFullScreen() {
    this.getWindow()
      .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    if (Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
      this.getWindow()
        .getDecorView()
        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    } else {
      this.getWindow()
        .getDecorView()
        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    thisContext = this;
    super.onCreate(savedInstanceState);

    makeFullScreen();
    setContentView(R.layout.activity_fullscreen);

    mVisible = true;
    mControlsView = findViewById(R.id.fullscreen_content_controls);
    mContentView = findViewById(R.id.fullscreen_content);

    mDetector = new GestureDetectorCompat(this, new MyGestureListener());

    startService(new Intent(this, LockScreenService.class));
    activityResumed();
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide(100);
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    activityResumed();
  }

  @Override
  protected void onPause() {
    super.onPause();
    moveToFront();
    activityPaused();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return !(keyCode == KeyEvent.KEYCODE_BACK)
      && super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    mDetector.onTouchEvent(event);
    return false;
  }

  @Override
  public boolean onGenericMotionEvent(MotionEvent event) {
    Log.d("com.wds.untitled2", "motion");
    return false;
  }

  private void hide() {
    // Hide UI first
    ActionBar actionBar = getActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }
    mControlsView.setVisibility(View.GONE);
    mVisible = false;

    // Schedule a runnable to remove the status and navigation bar after a delay
    mHideHandler.removeCallbacks(mShowPart2Runnable);
    mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
  }

  /**
   * Schedules a call to hide() in [delay] milliseconds, canceling any
   * previously scheduled calls.
   */
  private void delayedHide(int delayMillis) {
    mHideHandler.removeCallbacks(mHideRunnable);
    mHideHandler.postDelayed(mHideRunnable, delayMillis);
  }

  private class UnityAdsListener implements IUnityAdsListener {

    @Override
    public void onUnityAdsReady(final String zoneId) {

    }

    @Override
    public void onUnityAdsStart(String zoneId) {
      FullscreenActivity.activityResumed();
    }

    @Override
    public void onUnityAdsFinish(String zoneId, UnityAds.FinishState result) {

    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {

    }

    private void toast(String callback, String msg) {

    }
  }

  class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String DEBUG_TAG = "com.wds.untitled2";
    private boolean fromOutSide;

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
      if (!fromOutSide) {
        return true;
      }

      Intent newIntent = new Intent(getContext(), Main2Activity.class);
      newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(newIntent);
      overridePendingTransition(0, 0);

      Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
      return true;
    }

    @Override
    public boolean onDown(MotionEvent event) {
      if (event.getY() < 70 || event.getY() > getWindow().getDecorView()
        .getHeight() - 70) {
        fromOutSide = true;
      }

      Log.d("com.wds.untitled2", event.getY() + " ");
      return true;
    }
  }
}
