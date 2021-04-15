package com.game.puzzlecrush;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {
    private ImageButton pauseBtn;
    private GameActivity activity;
    private GridLayout gridLayout;

    private Chronometer chronometer;
    private boolean chronoRunning;
    private long pauseOffset;

    private CountDownTimer countDownTimer;
    private TextView textViewCountDown;
    private static final long START_TIME = 60 * 1000;
    private long TIME_LEFT = START_TIME;
    private boolean countDownRunning;

    public static int cellWidth, screenWidth, gridColCount = 7, gridRowCount = 5;
    GemCell gemCellBeingDragged, gemCellBeingReplaced;
    public static int[] gems = {
            R.drawable.gem_blue,
            R.drawable.gem_green,
            R.drawable.gem_purple,
            R.drawable.gem_red,
            R.drawable.gem_yellow,
    };
    private RelativeLayout relativeLayout;
    HashMap<List<Integer>, GemCell> gemCellList = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.activity = this;

        // Get Screen dimensions for responsive
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        cellWidth = screenWidth / gridColCount;

        initGrid(); // Set grid col/row/size
        initBoardGems(); // For each cell, create image view with random gem img
        initGemsSwipeListener();
        initPausePopupListeners();

        // Init Chronometer
        this.chronometer = findViewById(R.id.chronometer);
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

        this.textViewCountDown = findViewById(R.id.countdown);
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
        Toast.makeText(GameActivity.this, "Reset Timer !", Toast.LENGTH_SHORT).show();
    }
    private void updateCountDownText () {
        int minutes = (int) (TIME_LEFT / 1000) / 60;
        int seconds = (int) (TIME_LEFT / 1000) % 60;

        String formatedTimeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textViewCountDown.setText(formatedTimeLeft);
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
        Toast.makeText(GameActivity.this, "Reset Chronometer !", Toast.LENGTH_SHORT).show();
    }

    private void initGrid () {
        this.relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        this.gridLayout = findViewById(R.id.gemGrid); // Get grid
        gridLayout.setColumnCount(gridColCount);
        gridLayout.setRowCount(gridRowCount);
        gridLayout.getLayoutParams().width = screenWidth;
        gridLayout.getLayoutParams().height = cellWidth * gridRowCount;
    }
    private void initBoardGems () {
        for (int r=0; r < gridRowCount; r++) {
            for (int c=0; c < gridColCount; c++) {
                ImageView imageView = new ImageView(this);
                imageView.setVisibility(View.VISIBLE);
                GemCell gemCell = new GemCell(r, c, imageView);

                gridLayout.addView(gemCell.getImageView());

                gemCellList.put(Collections.unmodifiableList(Arrays.asList(r, c)), gemCell);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initGemsSwipeListener () {
        for (final GemCell gemCell : gemCellList.values()) {
            ImageView imageView = gemCell.getImageView();

            imageView.setOnTouchListener(new OnGemSwipe(this)
            {
                @Override
                void swipeLeft() {
                    super.swipeLeft();
                    initInterchange("Left", gemCell.getX(), gemCell.getY() - 1, "translationX", -1, gemCell);
                }

                @Override
                void swipeRight() {
                    super.swipeRight();
                    initInterchange("Right", gemCell.getX(), gemCell.getY() + 1, "translationX", 1, gemCell);
                }
                @Override
                void swipeTop() {
                    super.swipeTop();
                    initInterchange("Top", gemCell.getX() - 1, gemCell.getY(), "translationY", -1, gemCell);
                }
                @Override
                void swipeBottom() {
                    super.swipeBottom();
                    initInterchange("Bottom", gemCell.getX() + 1, gemCell.getY(), "translationY", 1, gemCell);
                }
            });
        }
    }
    private void initInterchange(String direction, int x, int y, String translationX, int positive, GemCell gemCell) {
        Toast.makeText(GameActivity.this, direction, Toast.LENGTH_SHORT).show();

        if (gemCellList.containsKey(Arrays.asList(x, y))) {
            gemCellBeingDragged = gemCell;
            gemCellBeingReplaced = gemCellList.get(Arrays.asList(x, y));
            gemInterchange(translationX, positive);
        }
    }
    private void gemInterchange (String direction, int positive) {
        final ImageView animateGemDragged = createGemToAnimate(gemCellBeingDragged);
        final ImageView animateGemReplaced = createGemToAnimate(gemCellBeingReplaced);

        ObjectAnimator animatorDragged;
        animatorDragged = ObjectAnimator.ofFloat(animateGemDragged, direction, positive * cellWidth);
        animatorDragged.setDuration(500);

        ObjectAnimator animatorReplaced;
        animatorReplaced = ObjectAnimator.ofFloat(animateGemReplaced, direction, -positive * cellWidth);
        animatorReplaced.setDuration(500);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorDragged, animatorReplaced);
        animatorSet.start();

        gemCellBeingReplaced.getImageView().setVisibility(View.INVISIBLE);
        gemCellBeingDragged.getImageView().setVisibility(View.INVISIBLE);


        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                relativeLayout.removeView(animateGemDragged);
                relativeLayout.removeView(animateGemReplaced);


                int replacedGem = (int) gemCellBeingReplaced.getImageView().getTag();
                int draggedGem = (int) gemCellBeingDragged.getImageView().getTag();
                gemCellBeingDragged.updateImageView(replacedGem);
                gemCellBeingReplaced.updateImageView(draggedGem);
            }
        });
    }

    private ImageView createGemToAnimate(GemCell gemCellToDuplicate) {
        final ImageView animateGem = new ImageView(this);
        animateGem.setPadding(10, 10, 10, 10);
        animateGem.setImageResource((int) gemCellToDuplicate.getImageView().getTag());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cellWidth, cellWidth);
        params.leftMargin = gemCellToDuplicate.getImageView().getLeft();
        params.topMargin = gemCellToDuplicate.getImageView().getTop();
        relativeLayout.addView(animateGem, params);
        return animateGem;
    }

    private void initPausePopupListeners() {
        this.pauseBtn = findViewById(R.id.pauseBtn); // Get pauseBtn
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Init new Popup (PausePopup Class)
                final PausePopup pausePopup = new PausePopup(activity);

                // Pause Timer/Chronometer
                pauseTimer();
                pauseChronometer();
                // Cancel popup listener to restart timer/chronometer
                pausePopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        startTimer(); // restart timer
                        startChronometer(); // restart chronometer
                    }
                });

                // On click continue Button
                pausePopup.getBtn_continue().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTimer(); // restart timer
                        startChronometer(); // restart chronometer
                        pausePopup.dismiss();
                    }
                });
                // On click BackMenu Button
                pausePopup.getBtn_backMenu().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent newMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(newMainActivity);
                        finish();
                        pausePopup.dismiss();
                    }
                });
                // Build/Create Popup
                pausePopup.Build();
            }
        });
    }
}
