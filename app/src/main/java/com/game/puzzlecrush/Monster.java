package com.game.puzzlecrush;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Monster {
    int lifePoints;
    int countDown = 3;

    String type;

    LinearLayout monsterArea;
    ImageView monsterImage;
    ImageView healthBar;
    TextView countDownText;

//    public Monster(LinearLayout monsterArea, ImageView healthBar, TextView countDownText) {
//        this.monsterArea = monsterArea;
//        this.healthBar = healthBar;
//        this.countDownText = countDownText;
//    }
}
