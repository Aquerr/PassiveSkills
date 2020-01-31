package io.github.aquerr.passiveskills.entities;

import java.util.Random;

public class MiningSkill extends Skill
{
    private static final Random random = new Random();
    private static final String NAME = "Mining";

    public MiningSkill()
    {
        super(NAME, SkillType.MINING, 0, 0);
    }

    public MiningSkill(final int level, final int experience)
    {
        super(NAME, SkillType.MINING, level, experience);
    }

    public double getExtraDropChance()
    {
        return (double) getLevel() / 100;
    }

    public int getExtraDropCount()
    {
        final int level = getLevel();
        int extraDropCount = 0;
        if (level <= 5)
        {
            //One Extra Block
            extraDropCount += getExtraBlocksCount(1);
        }
        else if(level <= 10)
        {
            //Two Extra Blocks
            extraDropCount += getExtraBlocksCount(2);
        }
        else if(level <= 15)
        {
            //Three Extra Blocks
            extraDropCount += getExtraBlocksCount(3);
        }
        return extraDropCount;
    }

    private int getExtraBlocksCount(int wishedCount)
    {
        final double extraDropChance = getExtraDropChance();
        int extraBlocksCount = 0;
        for (int i = 0; i < wishedCount; i++)
        {
            double randomNumber = random.nextDouble();
            if (randomNumber <= extraDropChance)
            {
                extraBlocksCount++;
            }
        }
        return extraBlocksCount;
    }
}
