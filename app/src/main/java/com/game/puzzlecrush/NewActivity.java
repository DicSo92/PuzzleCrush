package com.game.puzzlecrush;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NewActivity extends AppCompatActivity  {
    private ImageButton pauseBtn;
    private NewActivity activity;
    private GridLayout gridLayout;

    ArrayList<ImageView> gemList = new ArrayList<>();
    public static int cellWidth, screenWidth, gridColCount = 7, gridRowCount = 5;
    GemCell gemCellBeingDragged, gemCellBeingReplaced;
    public static int[] gems = {
            R.drawable.gem_blue,
            R.drawable.gem_green,
            R.drawable.gem_purple,
            R.drawable.gem_red,
            R.drawable.gem_yellow,
    };


    ArrayList<GemCell> gemCellList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.activity = this;

        // Get Screen dimensions for responsive
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        cellWidth = screenWidth / gridColCount;

        initGrid(); // Set grid col/row/size

        initBoardGems();

        initGemsSwipeListener();
    }

    private void initGrid () {
        this.gridLayout = findViewById(R.id.gemGrid); // Get grid
        gridLayout.setColumnCount(gridColCount);
        gridLayout.setRowCount(gridRowCount);
        gridLayout.getLayoutParams().width = screenWidth;
        gridLayout.getLayoutParams().height = cellWidth * gridRowCount;
    }

    private void initBoardGems() {
        for (int r=0; r < gridRowCount; r++) {
            for (int c=0; c < gridColCount; c++) {
                ImageView imageView = new ImageView(this);
                GemCell gemCell = new GemCell(r, c, imageView);

                gridLayout.addView(gemCell.getImageView());
                gemCellList.add(gemCell);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initGemsSwipeListener () {
        for (final GemCell gemCell : gemCellList) {
            ImageView imageView = gemCell.getImageView();

            imageView.setOnTouchListener(new OnGemSwipe(this)
            {
                @Override
                void swipeLeft() {
                    super.swipeLeft();
                    Toast.makeText(NewActivity.this, "Left", Toast.LENGTH_SHORT).show();
                    gemCellBeingDragged = gemCell;
                    for (GemCell gemCellR : gemCellList) {
                        if (gemCellR.getX() ==  gemCell.getX() && gemCellR.getY() == gemCell.getY() - 1) {
                            gemCellBeingReplaced = gemCellR;
                            gemInterchange();
                            break;
                        }
                    }
                }
                @Override
                void swipeRight() {
                    super.swipeRight();
                    Toast.makeText(NewActivity.this, "Right", Toast.LENGTH_SHORT).show();
                    gemCellBeingDragged = gemCell;
                    for (GemCell gemCellR : gemCellList) {
                        if (gemCellR.getX() ==  gemCell.getX() && gemCellR.getY() == gemCell.getY() + 1) {
                            gemCellBeingReplaced = gemCellR;
                            gemInterchange();
                            break;
                        }
                    }
                }
                @Override
                void swipeTop() {
                    super.swipeTop();
                    Toast.makeText(NewActivity.this, "Top", Toast.LENGTH_SHORT).show();
                    gemCellBeingDragged = gemCell;
                    for (GemCell gemCellR : gemCellList) {
                        if (gemCellR.getX() ==  gemCell.getX() - 1 && gemCellR.getY() == gemCell.getY()) {
                            gemCellBeingReplaced = gemCellR;
                            gemInterchange();
                            break;
                        }
                    }
                }
                @Override
                void swipeBottom() {
                    super.swipeBottom();
                    Toast.makeText(NewActivity.this, "Bottom", Toast.LENGTH_SHORT).show();
                    gemCellBeingDragged = gemCell;
                    for (GemCell gemCellR : gemCellList) {
                        if (gemCellR.getX() ==  gemCell.getX() + 1 && gemCellR.getY() == gemCell.getY()) {
                            gemCellBeingReplaced = gemCellR;
                            gemInterchange();
                            break;
                        }
                    }
                }
            });
        }
    }
    private void gemInterchange () {
//        ObjectAnimator animator = ObjectAnimator.ofFloat(gemList.get(gemCellBeingDragged), "x", cellWidth);
//        animator.setDuration(1000);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(animator);
//        animatorSet.start();

        int replacedGem = (int) gemCellBeingReplaced.getImageView().getTag();
        int draggedGem = (int) gemCellBeingDragged.getImageView().getTag();
        gemCellBeingDragged.updateImageView(replacedGem);
        gemCellBeingReplaced.updateImageView(draggedGem);
    }
}
