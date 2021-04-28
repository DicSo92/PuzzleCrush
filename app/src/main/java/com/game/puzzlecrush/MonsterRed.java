package com.game.puzzlecrush;

public class MonsterRed extends Monster{
    public static int[] drawableSvgs = {
            R.drawable.monster_red_1,
            R.drawable.monster_red_2
    };
    private static int maxAttackDamage = 200;
    public int attackDamage = 20;

    public MonsterRed(int percentIncreasedDamages) {

    }
}
