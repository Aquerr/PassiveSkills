package io.github.aquerr.passiveskills.listeners;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.data.SkillDataBuilder;
import io.github.aquerr.passiveskills.data.SkillDataImpl;
import io.github.aquerr.passiveskills.data.SkillTranslator;
import io.github.aquerr.passiveskills.entities.MiningSkill;
import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.entities.SkillType;
import io.github.aquerr.passiveskills.util.SkillBarUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.boss.*;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BlockBreakListener
{
	@Listener(order = Order.LAST)
	public void onBlockBreak(final ChangeBlockEvent.Break event, final @Root Player player)
	{
		final EventContext eventContext = event.getContext();
		final Optional<ItemStackSnapshot> optionalUsedItem = eventContext.get(EventContextKeys.USED_ITEM);
		final Optional<BlockSnapshot> optionalHitBlock = eventContext.get(EventContextKeys.BLOCK_HIT);

		if(!optionalUsedItem.isPresent())
			return;

		final ItemStackSnapshot usedItem = optionalUsedItem.get();
		if(!usedItem.getType().getId().contains("pickaxe")) //Find a better way for determining if tool is pickaxe.
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
			MiningSkill skill = new MiningSkill(0, 1);
			player.offer(new SkillDataImpl(skill));
			return;
		}

		final MiningSkill skill = (MiningSkill) optionalSkill.get();

		final int level = skill.getLevel();
		skill.addExperience(1);
		if (level != skill.getLevel())
			player.sendMessage(Text.of(TextColors.BLUE, "Your " + skill.getName() + " has leveled up to level " + skill.getLevel()));
		player.offer(PassiveSkillsPlugin.MINING_SKILL, skill);
		final ServerBossBar skillBar = SkillBarUtil.getSkillBar(player, Text.of("Mining Level"), skill.getLevelPercentage());
		skillBar.addPlayer(player);

		Task.builder().execute(() -> skillBar.removePlayer(player)).delay(3, TimeUnit.SECONDS).async().submit(PassiveSkillsPlugin.getPlugin());
	}

	private void updateLumberjackSkill(final Player player)
	{

	}

	@Listener(beforeModifications = true)
	public void onBlockDrop(final SpawnEntityEvent event)
	{
		final List<Entity> entities = new ArrayList<>(event.getEntities());
		final EventContext eventContext = event.getContext();
		final Optional<Player> optionalPlayer = event.getCause().first(Player.class);
		final Optional<ItemStackSnapshot> optionalItemStackSnapshot = eventContext.get(EventContextKeys.USED_ITEM);

		if (!optionalPlayer.isPresent())
			return;

		final Object source = event.getSource();
		if (!(source instanceof BlockSnapshot))
			return;

		//Find better way for determining if used item is pickaxe.
		if (!optionalItemStackSnapshot.isPresent() || !optionalItemStackSnapshot.get().getType().getId().contains("pickaxe"))
			return;

		final Player player = optionalPlayer.get();
		final Optional<Skill> optionalSkill = player.get(PassiveSkillsPlugin.MINING_SKILL);
		if (!optionalSkill.isPresent())
			return;

		final MiningSkill skill = (MiningSkill) optionalSkill.get();
		final int extraDropCount = skill.getExtraDropCount();
		for (final Entity entity : entities)
		{
			final Optional<ItemStackSnapshot> entityItemStackSnapshot = entity.get(Keys.REPRESENTED_ITEM);
			if (!entityItemStackSnapshot.isPresent())
				continue;
			if (entityItemStackSnapshot.get().getType().getBlock().isPresent())
			{
				final BlockState blockState = BlockState.builder().blockType(entityItemStackSnapshot.get().getType().getBlock().get()).build();
				final BlockSnapshot blockSnapshot = BlockSnapshot.builder().position(entity.getLocation().getPosition().toInt()).blockState(blockState).world(player.getWorld().getProperties()).build();

				for (int j = 0; j < extraDropCount; j++)
				{
					final Entity newEntity = player.getWorld().createEntity(EntityTypes.ITEM, entity.getLocation().getPosition());
					newEntity.offer(Keys.REPRESENTED_ITEM, ItemStack.builder().fromBlockState(blockSnapshot.getState()).build().createSnapshot());
					event.getEntities().add(newEntity);
				}
			}
			else
			{

				for (int j = 0; j < extraDropCount; j++)
				{
					final Entity newEntity = player.getWorld().createEntity(EntityTypes.ITEM, entity.getLocation().getPosition());
					newEntity.offer(Keys.REPRESENTED_ITEM, ItemStack.builder().fromSnapshot(entityItemStackSnapshot.get()).build().createSnapshot());
					event.getEntities().add(newEntity);
				}
			}
		}
	}
}
