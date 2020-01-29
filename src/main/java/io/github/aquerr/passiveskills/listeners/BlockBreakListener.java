package io.github.aquerr.passiveskills.listeners;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.data.SkillDataBuilder;
import io.github.aquerr.passiveskills.data.SkillDataImpl;
import io.github.aquerr.passiveskills.data.SkillTranslator;
import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.entities.SkillType;
import io.github.aquerr.passiveskills.util.SkillBarUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.boss.*;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class BlockBreakListener
{
	@Listener
	public void onBlockBreak(final ChangeBlockEvent.Break event, final @Root Player player)
	{
		final EventContext eventContext = event.getContext();
		final Optional<ItemStackSnapshot> optionalUsedItem = eventContext.get(EventContextKeys.USED_ITEM);
		final Optional<BlockSnapshot> optionalHitBlock = eventContext.get(EventContextKeys.BLOCK_HIT);

		if(!optionalUsedItem.isPresent())
			return;

		final ItemStackSnapshot usedItem = optionalUsedItem.get();
		if(!usedItem.getType().getId().contains("pickaxe"))
			return;

		//At this point we know the player has used a pickaxe. Let's check what was the block that player has destroyed.

		if(!optionalHitBlock.isPresent())
			return;

		final BlockSnapshot hitBlock = optionalHitBlock.get();
		if(hitBlock.getState().getType() == BlockTypes.LOG || hitBlock.getState().getType() == BlockTypes.LOG2)
		{
			updateLumberjackSkill(player);
		}
		else
		{
			updateMiningSkill(player);
		}
	}

	private void updateMiningSkill(final Player player)
	{
		Optional<Skill> optionalSkill = player.get(PassiveSkillsPlugin.MINING_SKILL);
		if(!optionalSkill.isPresent())
		{
			Skill skill = new Skill("Mining", SkillType.MINING, 0, 1);
			player.offer(new SkillDataImpl(skill));
			return;
		}

		final Skill skill = optionalSkill.get();

		skill.addExperience(1);
		player.offer(PassiveSkillsPlugin.MINING_SKILL, skill);
		final ServerBossBar skillBar = SkillBarUtil.getSkillBar(player, Text.of("Mining Level"), skill.getExperience());
		skillBar.addPlayer(player);

		Task.builder().execute(() -> skillBar.removePlayer(player)).delay(3, TimeUnit.SECONDS).async().submit(PassiveSkillsPlugin.getPlugin());
	}

	private void updateLumberjackSkill(final Player player)
	{

	}
}
