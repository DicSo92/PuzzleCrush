package com.game.puzzlecrush;

import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class GameTimer {
    public Activity activity;

    private CountDownTimer countDownTimer;
    private TextView textViewCountDown;
    private static final long START_TIME = 60 * 1000;
    private long TIME_LEFT = START_TIME;
    private boolean countDownRunning;

    public GameTimer (Activity _activity) {
        this.activity = _activity;

        this.textViewCountDown = this.activity.findViewById(R.id.countdown);
        startTimer();
    }

    public void startTimer () {
        if (!countDownRunning) {
            countDownTimer = new CountDownTimer(TIME_LEFT, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    TIME_LEFT = millisUntilFinished;
                    updateCountDownText();
                }
                @Override
                public void onFinish() {
                    countDownRunning = false;
                    resetTimer();
                    startTimer();
                }
            }.start();
            countDownRunning = true;
        }
    }
    public void pauseTimer () {
        if (countDownRunning) {
            countDownTimer.cancel();
            countDownRunning = false;
        }
    }
    public void resetTimer () {
        TIME_LEFT = START_TIME;
        updateCountDownText();
        Toast.makeText(this.activity, "Reset Timer !", Toast.LENGTH_SHORT).show();
    }
    private void updateCountDownText () {
        int minutes = (int) (TIME_LEFT / 1000) / 60;
        int seconds = (int) (TIME_LEFT / 1000) % 60;

        String formatedTimeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textViewCountDown.setText(formatedTimeLeft);
    }
}
