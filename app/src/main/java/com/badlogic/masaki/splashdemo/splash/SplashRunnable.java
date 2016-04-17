package com.badlogic.masaki.splashdemo.splash;

/**
 * An interface that extends {@link Runnable} and executes a splash task
 * Created by shojimasaki on 2016/04/17.
 */
public interface SplashRunnable extends Runnable {
    /**
     * should be called when the task is paused
     */
    void pause();

    /**
     * should be called when the task is resumed
     */
    void resume();

    /**
     * should be called when the task is destroyed
     */
    void destroy();

    /**
     * called when the task is completed
     */
    void finish();

    /**
     * used to adjust the elapsed time when the device is going to background, changing configuration, etc
     * @param elapsedTime elapsed time
     */
    void setElapsedTime(float elapsedTime);

    /**
     * gets the elapsed time from the launch to current
     * @return elapsed time
     */
    float getElapsedTime();

    /**
     * callback interface to notify an Activity of the completion of its task
     */
    interface Callback {
        void onSplashCompleted();
    }
}
