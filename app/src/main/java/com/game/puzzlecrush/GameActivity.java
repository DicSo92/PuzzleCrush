package com.game.puzzlecrush;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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

    ArrayList<ImageView> gemList = new ArrayList<>();
    private int cellWidth, screenWidth, gridColCount = 7, gridRowCount = 5;
    int gemBeingDragged, gemBeingReplaced;
    private int[] gems = {
            R.drawable.gem_blue,
            R.drawable.gem_green,
            R.drawable.gem_purple,
            R.drawable.gem_red,
            R.drawable.gem_yellow,
    };

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
        this.gridLayout = findViewById(R.id.gemGrid); // Get grid
        gridLayout.setColumnCount(gridColCount);
        gridLayout.setRowCount(gridRowCount);
        gridLayout.getLayoutParams().width = screenWidth;
        gridLayout.getLayoutParams().height = cellWidth * gridRowCount;
    }
    private void initBoardGems () {
        for (int i = 0; i < gridColCount * gridRowCount; i++)
        {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            // Set imageView Size
            imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(cellWidth, cellWidth));
            imageView.setMaxHeight(cellWidth);
            imageView.setMaxWidth(cellWidth);
            // Set ImageView padding
            imageView.setPadding(10,10,10,10);
            // Random int
            int randomGem = (int) Math.floor(Math.random() * gems.length);
            // display gem
            imageView.setImageResource(gems[randomGem]);
            imageView.setTag(gems[randomGem]);
            gemList.add(imageView);
            gridLayout.addView(imageView);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initGemsSwipeListener () {
        for (final ImageView imageView : gemList) {
            imageView.setOnTouchListener(new OnGemSwipe(this)
            {
                @Override
                void swipeLeft() {
                    super.swipeLeft();
                    Toast.makeText(GameActivity.this, "Left", Toast.LENGTH_SHORT).show();
                    gemBeingDragged = imageView.getId();
                    gemBeingReplaced = gemBeingDragged - 1;
                    gemInterchange();

                }
                @Override
                void swipeRight() {
                    super.swipeRight();
                    Toast.makeText(GameActivity.this, "Right", Toast.LENGTH_SHORT).show();
                    gemBeingDragged = imageView.getId();
                    gemBeingReplaced = gemBeingDragged + 1;
                    gemInterchange();
                }
                @Override
                void swipeTop() {
                    super.swipeTop();
                    Toast.makeText(GameActivity.this, "Top", Toast.LENGTH_SHORT).show();
                    gemBeingDragged = imageView.getId();
                    gemBeingReplaced = gemBeingDragged - gridColCount;
                    gemInterchange();
                }
                @Override
                void swipeBottom() {
                    super.swipeBottom();
                    Toast.makeText(GameActivity.this, "Bottom"+imageView.getId(), Toast.LENGTH_SHORT).show();
                    gemBeingDragged = imageView.getId();
                    gemBeingReplaced = gemBeingDragged + gridColCount;
                    gemInterchange();
                }
            });
        }
    }
    private void gemInterchange () {
//        ObjectAnimator animator = ObjectAnimator.ofFloat(gemList.get(gemBeingDragged), "x", cellWidth);
//        animator.setDuration(1000);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(animator);
//        animatorSet.start();


        int replacedGem = (int) gemList.get(gemBeingReplaced).getTag();
        int draggedGem = (int) gemList.get(gemBeingDragged).getTag();
        gemList.get(gemBeingDragged).setImageResource(replacedGem);
        gemList.get(gemBeingReplaced).setImageResource(draggedGem);
        gemList.get(gemBeingDragged).setTag(replacedGem);
        gemList.get(gemBeingReplaced).setTag(draggedGem);
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
