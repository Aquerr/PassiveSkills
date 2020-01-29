package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.entities.Skill;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableSkillDataImpl extends AbstractImmutableData<ImmutableSkillData, SkillData> implements ImmutableSkillData
{
	private final Skill skill;

	private final ImmutableValue<Skill> skillValue;

	public ImmutableSkillDataImpl()
	{
		this(null);
	}

	public ImmutableSkillDataImpl(final Skill skill)
	{
		this.skill = skill;

		this.skillValue = Sponge.getRegistry().getValueFactory().createValue(PassiveSkillsPlugin.MINING_SKILL, skill).asImmutable();

		this.registerGetters();
	}

	@Override
	public ImmutableValue<Skill> skill()
	{
		return this.skillValue;
	}

	@Override
	protected void registerGetters()
	{
		registerKeyValue(PassiveSkillsPlugin.MINING_SKILL, this::skill);
		registerFieldGetter(PassiveSkillsPlugin.MINING_SKILL, this::getSkill);
	}

	@Override
	public SkillData asMutable()
	{
		return new SkillDataImpl(this.skill);
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

	@Override
	public DataContainer toContainer()
	{
		final DataContainer container = super.toContainer();
		if(this.skill != null)
		{
			container.set(PassiveSkillsPlugin.MINING_SKILL, this.skill);
		}
		return container;
	}
}
