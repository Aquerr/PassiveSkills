package io.github.aquerr.passiveskills.entities;

public class FightingSkill extends Skill
{
    public FightingSkill()
    {
        super("Fighting", SkillType.FIGHTING, 0, 0);
    }

    public FightingSkill(int level, int experience)
    {
        super("Fighting", SkillType.FIGHTING, level, experience);
    }

    public float getExtraDamageChance()
    {
        return (float) getLevel() / 100;
    }

    public float getExtraDamage()
    {
        final int level = getLevel();
        float extraDamage = 0.0f;

        boolean shouldDealExtraDamage = false;
        double randomNumber = Skill.RANDOM.nextDouble();
        if (randomNumber <= getExtraDamageChance())
        {
            shouldDealExtraDamage = true;
        }

        if (shouldDealExtraDamage)
        {
            extraDamage = Skill.RANDOM.nextFloat() + Skill.RANDOM.nextInt(level);
        }
        return extraDamage;
    }
}
