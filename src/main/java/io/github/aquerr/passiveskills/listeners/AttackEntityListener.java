package io.github.aquerr.passiveskills.listeners;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.data.SkillDataImpl;
import io.github.aquerr.passiveskills.entities.FightingSkill;
import io.github.aquerr.passiveskills.entities.Skill;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.HashSet;
import java.util.Optional;

public class AttackEntityListener
{
    @Listener
    public void onEntityAttack(final AttackEntityEvent event, final @Root Player player)
    {
        final Optional<Skill> optionalSkill = player.get(PassiveSkillsPlugin.FIGHTING_SKILL);
        if (!optionalSkill.isPresent())
        {
            player.offer(new SkillDataImpl(new FightingSkill()));
            return;
        }

        final FightingSkill fightingSkill = (FightingSkill)optionalSkill.get();
        final float extraDamage = fightingSkill.getExtraDamage();
        final DamageModifier damageModifier = DamageModifier.builder().type(DamageModifierTypes.SWEEPING).cause(Cause.builder().append(PassiveSkillsPlugin.getPlugin()).build(event.getContext())).build();
        event.addDamageModifierAfter(damageModifier, operand -> extraDamage, new HashSet<>());
    }
}
