package io.github.aquerr.passiveskills.listeners;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.data.SkillDataImpl;
import io.github.aquerr.passiveskills.entities.MiningSkill;
import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.entities.SkillType;
import io.github.aquerr.passiveskills.entities.WoodcuttingSkill;
import io.github.aquerr.passiveskills.util.SkillBarUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class BlockBreakListener
{
	@Listener(order = Order.POST)
	@IsCancelled(Tristate.FALSE)
	public void onBlockBreak(final ChangeBlockEvent.Break event, final @Root Player player)
	{
		final EventContext eventContext = event.getContext();
		final Optional<ItemStackSnapshot> optionalUsedItem = eventContext.get(EventContextKeys.USED_ITEM);
		final Optional<BlockSnapshot> optionalHitBlock = eventContext.get(EventContextKeys.BLOCK_HIT);

		if(!optionalUsedItem.isPresent())
			return;

		final ItemStackSnapshot usedItem = optionalUsedItem.get();

		if(!optionalHitBlock.isPresent())
			return;

		final BlockSnapshot hitBlock = optionalHitBlock.get();
		if(hitBlock.getState().getType() == BlockTypes.LOG || hitBlock.getState().getType() == BlockTypes.LOG2)
		{
			updateWoodcuttingSkill(player);
		}
		else
		{
			if(!usedItem.getType().getId().contains("pickaxe")) //Find a better way for determining if tool is pickaxe.
				return;

			updateMiningSkill(player);
		}
	}

	private void updateMiningSkill(final Player player)
	{
		List<Skill> skills = player.getOrElse(PassiveSkillsPlugin.SKILLS, new SkillDataImpl().skills().getAll());
		final MiningSkill skill = (MiningSkill) skills.stream().filter(x->x.getType() == SkillType.MINING).findFirst().get();

		final int level = skill.getLevel();
		skill.addExperience(1);
		if (level != skill.getLevel())
			player.sendMessage(Text.of(TextColors.BLUE, "Your " + skill.getName() + " has leveled up to level " + skill.getLevel()));
		player.offer(PassiveSkillsPlugin.SKILLS, skills);
		final ServerBossBar skillBar = SkillBarUtil.getSkillBar(Text.of("Mining Level"), BossBarColors.BLUE, player, skill.getLevelPercentage());
		SkillBarUtil.showSkillBar(player, skillBar);

		Task.builder().execute(() -> skillBar.removePlayer(player)).delay(3, TimeUnit.SECONDS).async().submit(PassiveSkillsPlugin.getPlugin());
	}

	private void updateWoodcuttingSkill(final Player player)
	{
		List<Skill> skills = player.getOrElse(PassiveSkillsPlugin.SKILLS, new SkillDataImpl().skills().getAll());
		final WoodcuttingSkill skill = (WoodcuttingSkill) skills.stream().filter(x->x.getType() == SkillType.WOODCUTTING).findFirst().get();

		final int level = skill.getLevel();
		skill.addExperience(1);
		if (level != skill.getLevel())
			player.sendMessage(Text.of(TextColors.BLUE, "Your " + skill.getName() + " has leveled up to level " + skill.getLevel()));
		player.offer(PassiveSkillsPlugin.SKILLS, skills);
		final ServerBossBar skillBar = SkillBarUtil.getSkillBar(Text.of(skill.getName()), BossBarColors.BLUE, player, skill.getLevelPercentage());
		SkillBarUtil.showSkillBar(player, skillBar);

		Task.builder().execute(() -> skillBar.removePlayer(player)).delay(3, TimeUnit.SECONDS).async().submit(PassiveSkillsPlugin.getPlugin());
	}

	@Listener(beforeModifications = true)
	@IsCancelled(Tristate.FALSE)
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

		final BlockSnapshot sourceBlock = (BlockSnapshot)source;

		if ((sourceBlock.getState().getType() != BlockTypes.LOG && sourceBlock.getState().getType() != BlockTypes.LOG2)
				|| !optionalItemStackSnapshot.isPresent()
				|| !optionalItemStackSnapshot.get().getType().getId().contains("pickaxe"))
			return;

		final Player player = optionalPlayer.get();
		final Optional<List<Skill>> optionalSkill = player.get(PassiveSkillsPlugin.SKILLS);
		if (!optionalSkill.isPresent())
			return;


		//Mining
		final MiningSkill skill = (MiningSkill) optionalSkill.get().stream().filter(x->x.getType() == SkillType.MINING).findFirst().get();
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
