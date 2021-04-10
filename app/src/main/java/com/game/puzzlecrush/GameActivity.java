package com.game.puzzlecrush;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private ImageButton pauseBtn;
    private GameActivity activity;
    private GridLayout gridLayout;

    private int[] gems = {
            R.drawable.gem_blue,
            R.drawable.gem_green,
            R.drawable.gem_purple,
            R.drawable.gem_red,
            R.drawable.gem_yellow,
    };

    private int cellWidth, screenWidth, gridColCount = 7, gridRowCount = 5;
    ArrayList<ImageView> gemList = new ArrayList<>();

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
                final PausePopup pausePopup = new PausePopup(activity);

                // On click
                pausePopup.getBtn_continue().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pausePopup.dismiss();
                    }
                });
                pausePopup.getBtn_backMenu().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent newMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(newMainActivity);
                        finish();
                        pausePopup.dismiss();
                    }
                });
                pausePopup.Build();
            }
        });
    }
}
