package com.game.puzzlecrush;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private ImageButton pauseBtn;
    private GameActivity activity;
    private GridLayout gridLayout;

    private Chronometer chronometer;
    private boolean runningChrono;
    private long pauseOffset;

    ArrayList<ImageView> gemList = new ArrayList<>();
    private int cellWidth, screenWidth, gridColCount = 7, gridRowCount = 5;
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
        startChronometer(chronometer);
        // Reset timer after 30 seconds (feature test)
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 30 * 1000) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(GameActivity.this, "Reset Timer !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startChronometer (View v) {
        if (!runningChrono) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            runningChrono = true;
        }
    }
    public void pauseChronometer (View v) {
        if (runningChrono) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            runningChrono = false;
        }
    }
    public void resetChronometer (View v) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
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
            gemList.add(imageView);
            gridLayout.addView(imageView);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initGemsSwipeListener () {
        for (ImageView imageView : gemList) {
            imageView.setOnTouchListener(new OnGemSwipe(this)
            {
                @Override
                void swipeLeft() {
                    super.swipeLeft();
                    Toast.makeText(GameActivity.this, "Left", Toast.LENGTH_SHORT).show();
                }
                @Override
                void swipeRight() {
                    super.swipeRight();
                    Toast.makeText(GameActivity.this, "Right", Toast.LENGTH_SHORT).show();
                }
                @Override
                void swipeTop() {
                    super.swipeTop();
                    Toast.makeText(GameActivity.this, "Top", Toast.LENGTH_SHORT).show();
                }
                @Override
                void swipeBottom() {
                    super.swipeBottom();
                    Toast.makeText(GameActivity.this, "Bottom", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initPausePopupListeners() {
        this.pauseBtn = findViewById(R.id.pauseBtn); // Get pauseBtn
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Init new Popup (PausePopup Class)
                final PausePopup pausePopup = new PausePopup(activity);

                // Pause Chronometer
                pauseChronometer(chronometer);
                // Cancel popup listener to restart chronometer
                pausePopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        startChronometer(chronometer); // restart chronometer
                    }
                });

                // On click continue Button
                pausePopup.getBtn_continue().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startChronometer(chronometer); // restart chronometer
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
