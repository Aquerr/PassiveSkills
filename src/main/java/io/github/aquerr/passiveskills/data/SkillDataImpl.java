package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.entities.MiningSkill;
import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.entities.SkillType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nullable;
import java.util.Optional;

public class SkillDataImpl extends AbstractData<SkillData, ImmutableSkillData> implements SkillData
{
	private Skill skill;

	public SkillDataImpl()
	{
		this(new MiningSkill());
	}

	public SkillDataImpl(final Skill skill)
	{
		this.skill = skill;
		this.registerGettersAndSetters();
	}

	@Override
	public Value<Skill> skill()
	{
		if (this.skill.getType() == SkillType.MINING)
			return Sponge.getRegistry().getValueFactory().createValue(PassiveSkillsPlugin.MINING_SKILL, this.skill, new MiningSkill());
		else
		{
			return Sponge.getRegistry().getValueFactory().createValue(PassiveSkillsPlugin.MINING_SKILL, this.skill, new Skill("Mining", SkillType.MINING, 0, 0));
		}
	}

	@Override
	protected void registerGettersAndSetters()
	{
		if (this.skill.getType() == SkillType.MINING)
		{
			registerKeyValue(PassiveSkillsPlugin.MINING_SKILL, this::skill);
			registerFieldSetter(PassiveSkillsPlugin.MINING_SKILL, this::setSkill);
			registerFieldGetter(PassiveSkillsPlugin.MINING_SKILL, this::getSkill);
		}
		else if (this.skill.getType() == SkillType.FIGHTING)
		{
			registerKeyValue(PassiveSkillsPlugin.FIGHTING_SKILL, this::skill);
			registerFieldSetter(PassiveSkillsPlugin.FIGHTING_SKILL, this::setSkill);
			registerFieldGetter(PassiveSkillsPlugin.FIGHTING_SKILL, this::getSkill);
		}
	}

	@Override
	public Optional<SkillData> fill(DataHolder dataHolder, MergeFunction overlap)
	{
		SkillData merged = overlap.merge(this, dataHolder.get(SkillData.class).orElse(null));
		this.skill = merged.skill().get();
		return Optional.of(this);
	}

	@Override
	public Optional<SkillData> from(DataContainer container)
	{
		if(!container.contains(PassiveSkillsPlugin.MINING_SKILL) && !container.contains(PassiveSkillsPlugin.FIGHTING_SKILL))
			return Optional.empty();

		this.skill = container.getSerializable(PassiveSkillsPlugin.MINING_SKILL.getQuery(), Skill.class).orElse(container.getSerializable(PassiveSkillsPlugin.FIGHTING_SKILL.getQuery(), Skill.class).get());
		return Optional.of(this);
	}

	@Override
	public SkillData copy()
	{
		return new SkillDataImpl(this.skill);
	}

	@Override
	public ImmutableSkillData asImmutable()
	{
		return new ImmutableSkillDataImpl(this.skill);
	}

	@Override
	public int getContentVersion()
	{
		return SkillDataBuilder.CONTENT_VERSION;
	}

	private Skill getSkill()
	{
		return this.skill;
	}

	private void setSkill(final @Nullable Skill skill)
	{
		this.skill = skill;
	}

	@Override
	public DataContainer toContainer()
	{
		DataContainer container = super.toContainer();
		if(this.skill != null)
		{
			if (this.skill.getType() == SkillType.MINING)
				container.set(PassiveSkillsPlugin.MINING_SKILL, this.skill);
			else if (this.skill.getType() == SkillType.FIGHTING)
				container.set(PassiveSkillsPlugin.FIGHTING_SKILL, this.skill);
		}
		return container;
	}
}
