package com.game.puzzlecrush;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.GridLayout;
import android.widget.ImageView;

public class GameActivity extends AppCompatActivity {

    int[] gems = {
            R.drawable.gem_blue,
            R.drawable.gem_green,
            R.drawable.gem_purple,
            R.drawable.gem_red,
            R.drawable.gem_yellow,
    };

    int cellWidth, screenWidth, gridColCount = 7, gridRowCount = 5;

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
            // Random int
            int randomGem = (int) Math.floor(Math.random() * gems.length);
            // display gem
            imageView.setImageResource(gems[randomGem]);
            gridLayout.addView(imageView);
        }
    }
}
