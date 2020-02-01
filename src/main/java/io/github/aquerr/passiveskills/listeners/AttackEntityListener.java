package io.github.aquerr.passiveskills.listeners;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.data.SkillDataImpl;
import io.github.aquerr.passiveskills.entities.FightingSkill;
import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.util.SkillBarUtil;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AttackEntityListener
{
    @Listener
    public void onEntityAttack(final AttackEntityEvent event, final @Root EntityDamageSource entityDamageSource)
    {
        if (!(entityDamageSource.getSource() instanceof Player))
            return;

        final Player player = (Player) entityDamageSource.getSource();
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
        player.sendMessage(Text.of("Final Damage:" + event.getFinalOutputDamage()));
        updateFightingSkill(player);
    }

    private void updateFightingSkill(final Player player)
    {
        Optional<Skill> optionalSkill = player.get(PassiveSkillsPlugin.FIGHTING_SKILL);
        final FightingSkill skill = (FightingSkill) optionalSkill.get();

        final int level = skill.getLevel();
        skill.addExperience(1);
        if (level != skill.getLevel())
            player.sendMessage(Text.of(TextColors.BLUE, "Your " + skill.getName() + " has leveled up to level " + skill.getLevel()));
        player.offer(PassiveSkillsPlugin.FIGHTING_SKILL, skill);
        final ServerBossBar skillBar = SkillBarUtil.getSkillBar(player, Text.of("Fighting Level"), skill.getLevelPercentage());
        skillBar.addPlayer(player);

        Task.builder().execute(() -> skillBar.removePlayer(player)).delay(3, TimeUnit.SECONDS).async().submit(PassiveSkillsPlugin.getPlugin());
    }
}
