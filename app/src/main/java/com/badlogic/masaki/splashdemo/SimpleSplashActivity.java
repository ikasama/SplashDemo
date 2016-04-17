package com.badlogic.masaki.splashdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.masaki.splashdemo.splash.SimpleSplashTask;
import com.badlogic.masaki.splashdemo.splash.SplashRunnable;

/**
 * An activity that executes a splash task and receives its callback
 * Created by shojimasaki on 2016/04/16.
 */
public class SimpleSplashActivity extends AppCompatActivity implements SplashRunnable.Callback {

    public static final String TAG = SimpleSplashActivity.class.getSimpleName();

    /**
     * key string for saving and getting mElapsedTime's value
     */
    protected static final String KEY_SPLASH_ELAPSED_TIME = "elapsed_time";

    /**
     * flag whether to skip splash when the screen is touched
     */
    protected boolean mSkipSplashAfterTouch = true;

    /**
     * time elapsing from the launch to current
     */
    protected float mElapsedTime;

    /**
     * looper that runs a runnable interface
     */
    protected Thread mLooper;

    /**
     * class that executes a splash task
     */
    protected SplashRunnable mSplashTask;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        hides the title of the window
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null) {
            mElapsedTime = savedInstanceState.getFloat(KEY_SPLASH_ELAPSED_TIME);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * if the splash task is null, initializes and starts it ,
         */
        if(mLooper == null) {
            mSplashTask = new SimpleSplashTask(this);
            mLooper = new Thread(mSplashTask);
            mLooper.start();
        }

        /*
        resets the elapsed time and resumes the task
         */
        mSplashTask.setElapsedTime(mElapsedTime);
        mSplashTask.resume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * gets the elapsed time from the task and saves it
         */
        mElapsedTime = mSplashTask.getElapsedTime();
        outState.putFloat(KEY_SPLASH_ELAPSED_TIME, mElapsedTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        pauses the task not to run in the background
         */
        mSplashTask.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*
        calls SplashRunnable#destroy() to ensure that the splash task certainly terminates
         */
        mSplashTask.destroy();
        mLooper = null;
        mSplashTask = null;
    }

    @Override
    public void onSplashCompleted() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mSkipSplashAfterTouch) {
            mSplashTask.finish();
        }
        return super.onTouchEvent(event);
    }

    /**
     * sets skipSplashAfterTouch to {@link SimpleSplashActivity#mSkipSplashAfterTouch}
     * @param skipSplashAfterTouch true if the splash skips when the device's screen is touched
     */
    public void setSkipSplashAfterTouch(boolean skipSplashAfterTouch) {
        mSkipSplashAfterTouch = skipSplashAfterTouch;
    }
}
