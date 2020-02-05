package io.github.aquerr.passiveskills.listeners;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.data.SkillDataImpl;
import io.github.aquerr.passiveskills.entities.FightingSkill;
import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.entities.SkillType;
import io.github.aquerr.passiveskills.util.SkillBarUtil;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageFunction;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AttackEntityListener
{
    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onEntityAttack(final AttackEntityEvent event, final @Root EntityDamageSource entityDamageSource)
    {
        if (!(entityDamageSource.getSource() instanceof Player))
            return;

        final Player player = (Player) entityDamageSource.getSource();
        final Optional<List<Skill>> optionalSkill = player.get(PassiveSkillsPlugin.SKILLS);
        if (!optionalSkill.isPresent())
        {
            player.offer(new SkillDataImpl());
            return;
        }

        final FightingSkill fightingSkill = (FightingSkill)optionalSkill.get().stream().filter(x->x.getType() == SkillType.FIGHTING).findFirst().get();
        final float extraDamage = fightingSkill.getExtraDamage();
        final DamageModifier damageModifier = DamageModifier.builder().type(DamageModifierTypes.SWEEPING).cause(Cause.builder().append(PassiveSkillsPlugin.getPlugin()).build(event.getContext())).build();
        event.addDamageModifierAfter(damageModifier, operand -> extraDamage, new HashSet<>());
        player.sendMessage(Text.of("Final Damage:" + event.getFinalOutputDamage()));
        final List<DamageFunction> damageModifierList = event.getModifiers();
        final boolean isCriticalHit = damageModifierList.stream().anyMatch(x->x.getModifier().getType() == DamageModifierTypes.CRITICAL_HIT);

        updateFightingSkill(player, isCriticalHit);
    }

    private void updateFightingSkill(final Player player, final boolean isCriticalHit)
    {
        Optional<Skill> optionalSkill = player.get(PassiveSkillsPlugin.SKILLS).get().stream().filter(x->x.getType() == SkillType.FIGHTING).findFirst();
        final FightingSkill skill = (FightingSkill) optionalSkill.get();

        final int level = skill.getLevel();
        skill.addExperience(isCriticalHit ? 2 : 1);
        if (level != skill.getLevel())
            player.sendMessage(Text.of(TextColors.BLUE, "Your " + skill.getName() + " has leveled up to level " + skill.getLevel()));
        player.offer(PassiveSkillsPlugin.FIGHTING_SKILL, skill);
        final ServerBossBar skillBar = SkillBarUtil.getSkillBar(Text.of("Fighting Level"), BossBarColors.RED, player, skill.getLevelPercentage());
        SkillBarUtil.showSkillBar(player, skillBar);

        Task.builder().execute(() -> skillBar.removePlayer(player)).delay(3, TimeUnit.SECONDS).async().submit(PassiveSkillsPlugin.getPlugin());
    }
}
