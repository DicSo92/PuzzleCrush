package com.game.puzzlecrush;

import android.app.Activity;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.Toast;

public class GameChronometer {
    public Activity activity;
    private Chronometer chronometer;
    private boolean chronoRunning;
    private long pauseOffset;

    public GameChronometer (Activity _activity) {
        this.activity = _activity;
        this.chronometer = this.activity.findViewById(R.id.chronometer);
        startChronometer();
        // Reset timer after 30 seconds (feature test)
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 30 * 1000) {
                    resetChronometer();
                }
            }
        });
    }

    public void startChronometer () {
        if (!chronoRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            chronoRunning = true;
        }
    }
    public void pauseChronometer () {
        if (chronoRunning) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronoRunning = false;
        }
    }
    public void resetChronometer () {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        Toast.makeText(this.activity, "Reset Chronometer !", Toast.LENGTH_SHORT).show();
    }
}
