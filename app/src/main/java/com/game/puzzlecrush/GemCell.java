package com.game.puzzlecrush;

import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

public class GemCell extends NewActivity{
    private int x, y, id;
    private ImageView imageView;

    public GemCell(int row, int col, ImageView gemImage) {
        this.x = row;
        this.y = col;
        this.imageView = gemImage;

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
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void updateImageView(int gem) {
        this.imageView.setImageResource(gem);
        this.imageView.setTag(gem);
        this.imageView.setVisibility(View.VISIBLE);
    }
}
