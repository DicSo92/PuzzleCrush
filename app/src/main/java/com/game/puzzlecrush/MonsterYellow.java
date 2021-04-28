package com.game.puzzlecrush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonsterYellow extends Monster {
    public static int[] drawableSvgs = {
            R.drawable.monster_yellow_1,
            R.drawable.monster_yellow_2
    };
    private static int maxAttackDamage = 200;
    public int attackDamage = 20;

    public MonsterYellow(int percentIncreasedDamages) {

    }
}
