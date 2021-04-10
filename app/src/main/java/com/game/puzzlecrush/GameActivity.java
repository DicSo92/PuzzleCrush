package com.game.puzzlecrush;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

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

        // Get Screen dimensions for responsive
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        cellWidth = screenWidth / gridColCount;

        GridLayout gridLayout = findViewById(R.id.gemGrid); // Get grid

        setGridParams(gridLayout); // Set grid col/row/size

        initBoardGems(gridLayout); // For each cell, create image view with random gem img

        for (ImageView imageView : gemList) {
            imageView.setOnTouchListener(new OnGemSwipe(this)
            {
                @Override
                void swipeLeft() {
                    super.swipeLeft();
                    Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();
                }

                @Override
                void swipeRight() {
                    super.swipeRight();
                    Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
                }

                @Override
                void swipeTop() {
                    super.swipeTop();
                    Toast.makeText(MainActivity.this, "Top", Toast.LENGTH_SHORT).show();
                }

                @Override
                void swipeBottom() {
                    super.swipeBottom();
                    Toast.makeText(MainActivity.this, "Bottom", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setGridParams (GridLayout gridLayout) {
        gridLayout.setColumnCount(gridColCount);
        gridLayout.setRowCount(gridRowCount);
        gridLayout.getLayoutParams().width = screenWidth;
        gridLayout.getLayoutParams().height = cellWidth * gridRowCount;
    }

    private void initBoardGems (GridLayout gridLayout) {
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
}
