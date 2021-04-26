package com.game.puzzlecrush;

import android.widget.ImageView;

public class Hero {
    public static final int maxLife = 100;
    public static final int maxStamina = 100;
    int lifePoints = maxLife;
    int stamina = 0;
    int heroImage;
    int index;
    ImageView imageView;

    public Hero (int index, int heroImg) {
        this.index = index;
        this.heroImage = heroImg;
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
