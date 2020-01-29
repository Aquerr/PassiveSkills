package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
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
		this(null);
	}

	public SkillDataImpl(final Skill skill)
	{
		this.skill = skill;

		this.registerGettersAndSetters();
	}

	@Override
	public Value<Skill> skill()
	{
		return Sponge.getRegistry().getValueFactory().createValue(PassiveSkillsPlugin.MINING_SKILL, this.skill, new Skill("Mining", SkillType.MINING));
	}

	@Override
	protected void registerGettersAndSetters()
	{
		registerKeyValue(PassiveSkillsPlugin.MINING_SKILL, this::skill);

		registerFieldGetter(PassiveSkillsPlugin.MINING_SKILL, this::getSkill);

		registerFieldSetter(PassiveSkillsPlugin.MINING_SKILL, this::setSkill);
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
		if(!container.contains(PassiveSkillsPlugin.MINING_SKILL))
			return Optional.empty();

		this.skill = container.getSerializable(PassiveSkillsPlugin.MINING_SKILL.getQuery(), Skill.class).get();
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
			container.set(PassiveSkillsPlugin.MINING_SKILL, this.skill);
		}
		return container;
	}
}
