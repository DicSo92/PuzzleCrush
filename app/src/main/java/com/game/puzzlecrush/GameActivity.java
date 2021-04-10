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


        // Set grid col/raw/size
        GridLayout gridLayout = findViewById(R.id.gemGrid);
        gridLayout.setColumnCount(gridColCount);
        gridLayout.setRowCount(gridRowCount);
        gridLayout.getLayoutParams().width = screenWidth;
        gridLayout.getLayoutParams().height = cellWidth * gridRowCount;

        // For each cell, create image view with random gem img
        for (int i = 0; i < gridColCount * gridRowCount; i++)
        {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(cellWidth, cellWidth));
            imageView.setMaxHeight(cellWidth);
            imageView.setMaxWidth(cellWidth);
            int randomGem = (int) Math.floor(Math.random() * gems.length);
            imageView.setImageResource(gems[randomGem]);
            gridLayout.addView(imageView);
        }
    }
}
