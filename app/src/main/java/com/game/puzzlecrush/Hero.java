package com.game.puzzlecrush;

import android.widget.ImageView;
import android.widget.LinearLayout;

public class Hero {
    public static final int maxLife = 100;
    public static final int maxStamina = 100;
    int lifePoints = maxLife;
    int stamina = 0;
    int index;
    String type;

    LinearLayout heroArea;
    ImageView heroImage;
    ImageView healthBar;
    ImageView staminaBar;

    public Hero (int index, ImageView heroImg, LinearLayout heroArea, ImageView healthBar, ImageView staminaBar) {
        this.index = index;
        this.heroImage = heroImg;
        this.heroArea = heroArea;
        this.healthBar = healthBar;
        this.staminaBar = staminaBar;

        this.healthBar.getDrawable().setLevel(10000);
        this.staminaBar.getDrawable().setLevel(0);
    }

    public int getLifePoints() {
        return lifePoints;
    }
    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }
    public int getStamina() {
        return stamina;
    }
    public void setStamina(int stamina) {
        this.stamina = stamina;
    }
}
