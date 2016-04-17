package com.badlogic.masaki.splashdemo.splash;

import android.util.Log;

/**
 * Class extends {@link SplashRunnable} and executes a simple splash task
 * Created by shojimasaki on 2016/04/17.
 */
public class SimpleSplashTask implements SplashRunnable {

    public static final String TAG = SimpleSplashTask.class.getSimpleName();

    /**
     * default splash seconds
     */
    public static final float DEFAULT_SPLASH_SECONDS = 5.0f;

    /**
     * default sleep milliseconds per loop
     */
    public static final long DEFAULT_SLEEP_SECONDS_PER_LOOP = 10;

    /**
     * constant of ten to the power of nine used to calculate elapsed time
     */
    public static final float ONE_GIGA_SECOND = 1000000000.0f;


    /**
     * seconds during which splash screen is displayed
     */
    private float mSplashSeconds = DEFAULT_SPLASH_SECONDS;

    /**
     * flag that indicates whether a splash task completed
     */
    private boolean mSplashCompleted = false;

    /**
     * used to calculate delta time of looping
     */
    private long mLastTime = System.nanoTime();

    /**
     * elapsed time from the launch to current
     */
    private float mElapsedTime;

    /**
     * current status of the task
     */
    private volatile Status mCurrentStatus = Status.PAUSED;

    /**
     * lock for monitoring {@link SimpleSplashTask#mCurrentStatus}
     */
    private final Object mStateLock = new Object();

    /**
     * lock for monitoring {@link SimpleSplashTask#mSplashCompleted}
     */
    private final Object mCompletionLock = new Object();

    /**
     * callback to notify the completion of the task
     */
    private SplashRunnable.Callback mCallback;

    /**
     * sleep milliseconds per loop
     */
    private long mSleepMilliseconds = DEFAULT_SLEEP_SECONDS_PER_LOOP;

    /**
     * flag whether to sleep while looping
     */
    private boolean mSleepPerLoop = true;

    /**
     * enum representing the current status of the task
     */
    public enum Status {
        /**
         * represents the state of running
         */
        RUNNING,

        /**
         * represents the state of pause
         */
        PAUSED,

        /**
         * represents the state of destroy
         */
        DESTROYED,
    }

    /**
     * Constructor
     */
    public SimpleSplashTask() { }

    /**
     * Constructor
     * @param callback Callback
     */
    public SimpleSplashTask(SplashRunnable.Callback callback) {
        mCallback = callback;
    }

    /**
     * Constructor
     * @param callback Callback
     * @param splashSeconds seconds during which splash screen is displayed
     */
    public SimpleSplashTask(SplashRunnable.Callback callback, float splashSeconds) {
        this(callback);
        mSplashSeconds = splashSeconds;
    }

    /**
     * sets splash seconds to {@link SimpleSplashTask#mSplashSeconds}
     * @param splashSeconds splash seconds
     */
    public void setSplashSeconds(float splashSeconds) {
        mSplashSeconds = splashSeconds;
    }

    /**
     * sets callback to {@link SimpleSplashTask#mCallback}
     * @param callback Callback
     */
    public void setCallback(SplashRunnable.Callback callback) {
        mCallback = callback;
    }

    /**
     * sets sleepPerLoop to {@link SimpleSplashTask#mSleepPerLoop}
     * @param sleepPerLoop true if the task sleeps per loop
     */
    public void setSleepPerLoop(boolean sleepPerLoop) {
        mSleepPerLoop = sleepPerLoop;
    }

    /**
     * sets sleepMilliseconds to {@link SimpleSplashTask#mSleepMilliseconds}
     * @param sleepMilliseconds sleep milliseconds per loop
     */
    public void setSleepSeconds(long sleepMilliseconds) {
        mSleepMilliseconds = sleepMilliseconds;
    }

    @Override
    public void setElapsedTime(float elapsedTime) {
        mElapsedTime = elapsedTime;
    }

    @Override
    public float getElapsedTime() {
        return mElapsedTime;
    }

    @Override
    public void run() {
        mLastTime = System.nanoTime();

        while(!mSplashCompleted) {
            /*
            if the task is destroyed, just finish looping
             */
            if(mCurrentStatus == Status.DESTROYED) {
                return;
            }

            /*
             * in pause, retakes current time to adjust the elapsed time
             */
            if(mCurrentStatus == Status.PAUSED) {
                mLastTime = System.nanoTime();
                continue;
            }

            float deltaTime = (System.nanoTime() - mLastTime) / ONE_GIGA_SECOND;
            mElapsedTime += deltaTime;
            mLastTime = System.nanoTime();

            /*
            sleeps the task if needed to adjust the elapsed time
             */
            sleep(mSleepMilliseconds);

            if(mElapsedTime < mSplashSeconds) {
                continue;
            }

            /*
            splash completes
             */
            finish();
        }

    }

    @Override
    public void pause() {
        /*
        sets the current status to PAUSED
         */
        synchronized (mStateLock) {
            mCurrentStatus = Status.PAUSED;
        }
    }

    @Override
    public void resume() {
        /*
        sets the current status to RUNNING
         */
        synchronized (mStateLock) {
            mCurrentStatus = Status.RUNNING;
        }
    }

    @Override
    public void destroy() {
        /*
        sets the current status to DESTROYED
         */
        synchronized (mStateLock) {
            mCurrentStatus = Status.DESTROYED;
        }
    }

    @Override
    public void finish() {
        synchronized (mCompletionLock) {
            if(mSplashCompleted) {
                return;
            }

            if(mCallback != null) {
                mCallback.onSplashCompleted();
            }

            mSplashCompleted = true;
        }
    }

    /**
     * sleeps the task
     * @param millis milliseconds during which the task sleeps
     */
    private void sleep(long millis) {
        if(!mSleepPerLoop) {
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
