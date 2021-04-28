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
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import static java.lang.System.*;

public class GameActivity extends AppCompatActivity {
    private ImageButton pauseBtn;
    private GameActivity activity;
    private GridLayout gridLayout;
    private RelativeLayout relativeLayout;
    private LinearLayout heroLayout;

    GameChronometer gameChronometer;
    GameTimer gameTimer;

    public boolean isAnimationsRunning = false;
    public boolean isSwipeRunning = false;

    public static int cellWidth, screenWidth, screenHeight, gridColCount = 7, gridRowCount = 5;
    GemCell gemCellBeingDragged, gemCellBeingReplaced;
    public static int[] gems = {
            R.drawable.gem_blue,
            R.drawable.gem_green,
            R.drawable.gem_purple,
            R.drawable.gem_red,
            R.drawable.gem_yellow,
    };
    HashMap<List<Integer>, GemCell> gemCellList = new HashMap<>();


    LinearLayout hero1Btn;
    ImageView healthBar1;
    ImageView staminaBar1;
    public static int[] heros = {
            R.drawable.hero_purple,
            R.drawable.hero_yellow,
            R.drawable.hero_red,
            R.drawable.hero_green,
            R.drawable.hero_blue,
    };
    ArrayList<Hero> heroList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.activity = this;

        this.relativeLayout = (RelativeLayout) findViewById(R.id.mainAnimLayout);
        this.gridLayout = findViewById(R.id.gemGrid); // Get grid
        this.heroLayout = findViewById(R.id.heroLayout);

        // Get Screen dimensions for responsive
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        cellWidth = screenWidth / gridColCount;

        initGrid(); // Set grid col/row/size
        initBoardGems();
        initGemsSwipeListener();
        initPausePopupListeners();

        initHeros();
        initMonsters();

        this.gameChronometer = new GameChronometer(this);
        this.gameTimer = new GameTimer(this);
    }

    public String loadJSONFromAsset() {
        String json = null;
        InputStream is = null;
        try {
            is = getResources().openRawResource(R.raw.monster_template);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void initMonsters() {
        ArrayList<HashMap<String, Boolean[]>> monsterTemplate = new ArrayList<>();

        try {
            JSONArray jsonMonster = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonMonster.length() ; i++) {
                JSONObject el = jsonMonster.getJSONObject(i);
                HashMap<String, Boolean[]> monster = new HashMap<>();

                JSONArray indexesTop = el.getJSONArray("top");
                JSONArray indexesBot = el.getJSONArray("bottom");

                Boolean[] monsterTop = new Boolean[7];
                Boolean[] monsterBot = new Boolean[7];

                for (int iTop = 0; iTop < indexesTop.length(); iTop++) {
                    int cell = indexesTop.getInt(iTop);

                    monsterTop[iTop] = cell == 1;
                }
                for (int iBot = 0; iBot < indexesBot.length(); iBot++) {
                    int cell = indexesTop.getInt(iBot);

                    monsterBot[iBot] = cell == 1;
                }
                monster.put("top", monsterTop);
                monster.put("botttom", monsterBot);
                monsterTemplate.add(monster);
            }
            out.println(monsterTemplate.get(2).get("top")[5]);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        GridLayout monsterGridTop = findViewById(R.id.monsterGridTop);
        GridLayout monsterGridBottom = findViewById(R.id.monsterGridBottom);
        monsterGridTop.setColumnCount(gridColCount);
        monsterGridTop.setRowCount(2);
        gridLayout.getLayoutParams().width = screenWidth;

        int monsterGridHeight = findViewById(R.id.monsterGrid).getTop();
        out.println(monsterGridHeight);


        // Top Grid ---------------------------------------
        ImageView img1 = new ImageView(this);
        img1.setLayoutParams(new android.view.ViewGroup.LayoutParams(cellWidth*2, monsterGridHeight));
        img1.setImageResource(R.drawable.monster_red_1);
        img1.setBackgroundResource(R.drawable.cell_border);
        monsterGridTop.addView(img1);

        ImageView empty = new ImageView(this);
        empty.setLayoutParams(new android.view.ViewGroup.LayoutParams(cellWidth*3, cellWidth*2));
//        empty.setBackgroundResource(R.drawable.cell_border);
        monsterGridTop.addView(empty);

        ImageView img2 = new ImageView(this);
        img2.setLayoutParams(new android.view.ViewGroup.LayoutParams(cellWidth*2, cellWidth*2));
        img2.setImageResource(R.drawable.hero_green);
        img2.setBackgroundResource(R.drawable.cell_border);
        monsterGridTop.addView(img2);
        // ------------------------------------------------


        // Bottom Grid ------------------------------------
        ImageView emptyBottom = new ImageView(this);
        emptyBottom.setLayoutParams(new android.view.ViewGroup.LayoutParams(cellWidth*2, cellWidth*2));
//        empty.setBackgroundResource(R.drawable.cell_border);
        monsterGridBottom.addView(emptyBottom);

        ImageView imgMid = new ImageView(this);
        imgMid.setLayoutParams(new android.view.ViewGroup.LayoutParams(cellWidth*3, cellWidth*2));
        imgMid.setImageResource(R.drawable.hero_green);
        imgMid.setBackgroundResource(R.drawable.cell_border);
        monsterGridBottom.addView(imgMid);
        // ------------------------------------------------
    }

    private void initHeros() {
        for (int i = 0; i < heros.length; i++) {
            LinearLayout heroArea = findViewById(getResources().getIdentifier("heroArea" + (i+1), "id", getPackageName()));
            ImageView heroImg = findViewById(getResources().getIdentifier("heroImage" + (i+1), "id", getPackageName()));
            final ImageView healthBar = findViewById(getResources().getIdentifier("healthBar" + (i+1), "id", getPackageName()));
            final ImageView staminaBar = findViewById(getResources().getIdentifier("staminaBar" + (i+1), "id", getPackageName()));

            Hero hero = new Hero(i, heroImg, heroArea, healthBar, staminaBar);
            heroArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    out.println("testation");
                    int healthLevel = healthBar.getDrawable().getLevel();
                    int staminaLevel = staminaBar.getDrawable().getLevel();

                    healthBar.getDrawable().setLevel(healthLevel - 500);
                    staminaBar.getDrawable().setLevel(staminaLevel + 1000);
                }
            });

            heroList.add(hero);
        }

    }


    private void initGrid() {
        gridLayout.setColumnCount(gridColCount);
        gridLayout.setRowCount(gridRowCount);
        gridLayout.getLayoutParams().width = screenWidth;
        gridLayout.getLayoutParams().height = cellWidth * gridRowCount;
    }

    private void initBoardGems() {
        // Add random gems to empty board
        for (int r = 0; r < gridRowCount; r++) {
            for (int c = 0; c < gridColCount; c++) {
                ImageView imageView = new ImageView(this);
                imageView.setVisibility(View.VISIBLE);
                GemCell gemCell = new GemCell(r, c, imageView);

                gridLayout.addView(gemCell.getImageView());
                gemCellList.put(Collections.unmodifiableList(Arrays.asList(r, c)), gemCell);
            }
        }

        // Prevent matches on first board
        boolean hasMatches = true;
        while (hasMatches) {
            hasMatches = false;
            for (GemCell gem : gemCellList.values()) {
                Object gemTag = gem.getImageView().getTag();
                if (gem.getY() > 0 && gem.getY() < gridColCount - 1) {
                    GemCell leftGem = gemCellList.get(Arrays.asList(gem.getX(), gem.getY() - 1));
                    GemCell rightGem = gemCellList.get(Arrays.asList(gem.getX(), gem.getY() + 1));

                    if (leftGem.getImageView().getTag().equals(gemTag) && rightGem.getImageView().getTag().equals(gemTag)) {
                        gem.updateImageView(gems[(int) Math.floor(Math.random() * gems.length)]);
                        hasMatches = true;
                    }
                }
                if (gem.getX() > 0 && gem.getX() < gridRowCount - 1) {
                    GemCell topGem = gemCellList.get(Arrays.asList(gem.getX() - 1, gem.getY()));
                    GemCell bottomGem = gemCellList.get(Arrays.asList(gem.getX() + 1, gem.getY()));

                    if (topGem.getImageView().getTag().equals(gemTag) && bottomGem.getImageView().getTag().equals(gemTag)) {
                        gem.updateImageView(gems[(int) Math.floor(Math.random() * gems.length)]);
                        hasMatches = true;
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initGemsSwipeListener() {
        for (final GemCell gemCell : gemCellList.values()) {
            ImageView imageView = gemCell.getImageView();

            imageView.setOnTouchListener(new OnGemTouch(this) {
                @Override
                void swipeLeft() {
                    super.swipeLeft();
                    if (!isAnimationsRunning && !isSwipeRunning) {
                        initInterchange("Left", gemCell.getX(), gemCell.getY() - 1, "translationX", -1, gemCell);
                    }
                }
                @Override
                void swipeRight() {
                    super.swipeRight();
                    if (!isAnimationsRunning && !isSwipeRunning) {
                        initInterchange("Right", gemCell.getX(), gemCell.getY() + 1, "translationX", 1, gemCell);
                    }
                }
                @Override
                void swipeTop() {
                    super.swipeTop();
                    if (!isAnimationsRunning && !isSwipeRunning) {
                        initInterchange("Top", gemCell.getX() - 1, gemCell.getY(), "translationY", -1, gemCell);
                    }
                }
                @Override
                void swipeBottom() {
                    super.swipeBottom();
                    if (!isAnimationsRunning && !isSwipeRunning) {
                        initInterchange("Bottom", gemCell.getX() + 1, gemCell.getY(), "translationY", 1, gemCell);
                    }
                }
                @Override
                void clicked() {
                    super.clicked();
                    if (!isAnimationsRunning && !isSwipeRunning) {
                        gemCell.setSelected(!gemCell.isSelected());
                        for (GemCell currGem : gemCellList.values()) {
                            boolean isSameGem = (gemCell.getX() == currGem.getX()) && (currGem.getY() == gemCell.getY());

                            if (currGem.isSelected() && !isSameGem) {
                                if (gemCell.getX() == currGem.getX() && gemCell.getY() == currGem.getY() + 1)
                                    this.swipeLeft();
                                else if (gemCell.getX() == currGem.getX() && gemCell.getY() == currGem.getY() - 1)
                                    this.swipeRight();
                                else if (gemCell.getY() == currGem.getY() && gemCell.getX() == currGem.getX() + 1)
                                    this.swipeTop();
                                else if (gemCell.getY() == currGem.getY() && gemCell.getX() == currGem.getX() - 1)
                                    this.swipeBottom();
                                else {
                                    currGem.setSelected(false);
                                    break;
                                }
                                currGem.setSelected(false);
                                gemCell.setSelected(false);

                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    private void initInterchange(String direction, int x, int y, String translation, int positive, GemCell gemCell) {
        Toast.makeText(GameActivity.this, direction, Toast.LENGTH_SHORT).show();

        if (gemCellList.containsKey(Arrays.asList(x, y))) {
            gemCellBeingDragged = gemCell;
            gemCellBeingReplaced = gemCellList.get(Arrays.asList(x, y));
            gemInterchange(translation, positive, true);
        }
    }

    private void gemInterchange(final String translation, final int positive, final boolean userAction) {
        final ImageView animateGemDragged = createGemToAnimate(gemCellBeingDragged);
        final ImageView animateGemReplaced = createGemToAnimate(gemCellBeingReplaced);

        ObjectAnimator animatorDragged = ObjectAnimator.ofFloat(animateGemDragged, translation, positive * cellWidth);
        animatorDragged.setDuration(300);
        ObjectAnimator animatorReplaced = ObjectAnimator.ofFloat(animateGemReplaced, translation, -positive * cellWidth);
        animatorReplaced.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorDragged, animatorReplaced);
        animatorSet.start();

        gemCellBeingReplaced.getImageView().setVisibility(View.INVISIBLE);
        gemCellBeingDragged.getImageView().setVisibility(View.INVISIBLE);

        isSwipeRunning = true;
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isSwipeRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                relativeLayout.removeView(animateGemDragged);
                relativeLayout.removeView(animateGemReplaced);

                int replacedGem = (int) gemCellBeingReplaced.getImageView().getTag();
                int draggedGem = (int) gemCellBeingDragged.getImageView().getTag();
                gemCellBeingDragged.updateImageView(replacedGem);
                gemCellBeingReplaced.updateImageView(draggedGem);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSwipeRunning = false;

                        // Make gems return to initial position if noMatches
                        boolean hasMatches = findMatches();
                        if(!hasMatches && userAction) {
                            gemInterchange(translation, positive, false);
                        }
                    }
                }, 100);
            }
        });
    }

    private ImageView createGemToAnimate(GemCell gemCellToDuplicate) {
        final ImageView animateGem = new ImageView(this);
        animateGem.setPadding(10, 10, 10, 10);
        animateGem.setImageResource((int) gemCellToDuplicate.getImageView().getTag());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cellWidth, cellWidth);
        params.leftMargin = gemCellToDuplicate.getImageView().getLeft();

        int gemGridTop = screenHeight - (gridLayout.getMeasuredHeight() + heroLayout.getMeasuredHeight());
        params.topMargin = gemGridTop + gemCellToDuplicate.getImageView().getTop();

        relativeLayout.addView(animateGem, params);
        return animateGem;
    }


    private boolean findMatches() {
        boolean hasMatches = false;
        isAnimationsRunning = true;

        for (GemCell gem : gemCellList.values()) {
            Object gemTag = gem.getImageView().getTag();
            if (gem.getY() > 0 && gem.getY() < gridColCount - 1) {
                GemCell leftGem = gemCellList.get(Arrays.asList(gem.getX(), gem.getY() - 1));
                GemCell rightGem = gemCellList.get(Arrays.asList(gem.getX(), gem.getY() + 1));

                if (leftGem.getImageView().getTag().equals(gemTag) && rightGem.getImageView().getTag().equals(gemTag)) {
                    leftGem.setMatched(true);
                    gem.setMatched(true);
                    rightGem.setMatched(true);
                    hasMatches = true;
                }
            }
            if (gem.getX() > 0 && gem.getX() < gridRowCount - 1) {
                GemCell topGem = gemCellList.get(Arrays.asList(gem.getX() - 1, gem.getY()));
                GemCell bottomGem = gemCellList.get(Arrays.asList(gem.getX() + 1, gem.getY()));

                if (topGem.getImageView().getTag().equals(gemTag) && bottomGem.getImageView().getTag().equals(gemTag)) {
                    topGem.setMatched(true);
                    gem.setMatched(true);
                    bottomGem.setMatched(true);
                    hasMatches = true;
                }
            }
        }
        if (hasMatches) {
            removeMatches();
            return true;
        } else {
            isAnimationsRunning = false;
            return false;
        }
    }



    private void removeMatches () {
        final ArrayList<ObjectAnimator> animators = new ArrayList<>();
        final ArrayList<ImageView> animateGemsFalling = new ArrayList<>();
        final ArrayList<GemCell> gemsToDestroy = new ArrayList<>();
        final ArrayList<Integer> gemsFalling = new ArrayList<>();

        for (int r = 0; r < gridRowCount; r++) {
            for (int c = 0; c < gridColCount; c++) {
                GemCell gem = gemCellList.get(Arrays.asList(r, c));
                // Si isMatched ou empty
                if (gem.isMatched() || gem.isEmpty()) {
                    // regarder toute les row au dessus
                    for (int i = 1; i <= gridRowCount; i++) {
                        // Si la row n'existe pas (generer une nouvelle gemme)
                        if (!gemCellList.containsKey(Arrays.asList(gem.getX() + i, gem.getY()))) {
                            final ImageView animateGemFalling = new ImageView(this);
                            animateGemFalling.setPadding(10, 10, 10, 10);
                            final int randomGem = (int) Math.floor(Math.random() * gems.length);
                            animateGemFalling.setImageResource(gems[randomGem]);
                            animateGemFalling.setTag(gems[randomGem]);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cellWidth, cellWidth);
                            params.leftMargin = gem.getImageView().getLeft();

                            int gemGridTop = screenHeight - (gridLayout.getMeasuredHeight() + heroLayout.getMeasuredHeight());
                            params.topMargin = gemGridTop + (gridRowCount + 3) * cellWidth;

                            relativeLayout.addView(animateGemFalling, params);

                            ObjectAnimator animatorFalling = ObjectAnimator.ofFloat(animateGemFalling, "translationY", -((gridRowCount + 3) - gem.getX()) * cellWidth);
                            animatorFalling.setDuration(500);

                            gem.getImageView().setVisibility(View.INVISIBLE);
//                        gem.setEmpty(false);

                            animators.add(animatorFalling);
                            animateGemsFalling.add(animateGemFalling);
                            gemsToDestroy.add(gem);
                            gemsFalling.add((int) animateGemFalling.getTag());

                            break;
                        }
                        // Si la gem n'est pas match et pas empty (remplacer par celle-ci)
                        if (!gemCellList.get(Arrays.asList(gem.getX() + i, gem.getY())).isMatched()
                                && !gemCellList.get(Arrays.asList(gem.getX() + i, gem.getY())).isEmpty()) {
                            GemCell gemFalling = gemCellList.get(Arrays.asList(gem.getX() + i, gem.getY()));

                            gemFalling.setEmpty(true);

                            ImageView animateGemFalling = createGemToAnimate(gemFalling);
                            ObjectAnimator animatorFalling = ObjectAnimator.ofFloat(animateGemFalling, "translationY", -(gemFalling.getX() - gem.getX()) * cellWidth);
                            animatorFalling.setDuration(500);
                            gemFalling.getImageView().setVisibility(View.INVISIBLE);
                            gem.getImageView().setVisibility(View.INVISIBLE);
//                        gem.setEmpty(false);

                            animators.add(animatorFalling);
                            animateGemsFalling.add(animateGemFalling);
                            gemsToDestroy.add(gem);
                            gemsFalling.add((int) gemFalling.getImageView().getTag());

                            break;
                        }
                    }
                }
            }
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators.toArray(new ObjectAnimator[1]));
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimationsRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                for (int i = 0; i < gemsToDestroy.size(); i++) {
                    relativeLayout.removeView(animateGemsFalling.get(i));
                    gemsToDestroy.get(i).setMatched(false);
                    gemsToDestroy.get(i).setEmpty(false);
//                    gemsToDestroy.get(i).getImageView().setBackgroundResource(0);
                    gemsToDestroy.get(i).updateImageView(gemsFalling.get(i));
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isAnimationsRunning = false;
                        findMatches();
                    }
                }, 500);
            }
        });
    }

    private void initPausePopupListeners() {
        this.pauseBtn = findViewById(R.id.pauseBtn); // Get pauseBtn
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Init new Popup (PausePopup Class)
                final PausePopup pausePopup = new PausePopup(activity);

                // Pause Timer/Chronometer
                gameTimer.pauseTimer();
                gameChronometer.pauseChronometer();
                // Cancel popup listener to restart timer/chronometer
                pausePopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        gameTimer.startTimer(); // restart timer
                        gameChronometer.startChronometer(); // restart chronometer
                    }
                });

                // On click continue Button
                pausePopup.getBtn_continue().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gameTimer.startTimer(); // restart timer
                        gameChronometer.startChronometer(); // restart chronometer
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
