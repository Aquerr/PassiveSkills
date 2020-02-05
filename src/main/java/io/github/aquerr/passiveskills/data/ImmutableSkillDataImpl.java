package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.entities.Skill;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.List;

public class ImmutableSkillDataImpl extends AbstractImmutableData<ImmutableSkillData, SkillData> implements ImmutableSkillData
{
	private final List<Skill> skills;
//	private final Skill skill;

	private final ImmutableListValue<Skill> skillsValue;

	public ImmutableSkillDataImpl()
	{
		this(null);
	}

	public ImmutableSkillDataImpl(final List<Skill> skills)
	{
		this.skills = skills;

		this.skillsValue = Sponge.getRegistry().getValueFactory().createListValue(PassiveSkillsPlugin.SKILLS, skills).asImmutable();

		this.registerGetters();
	}

	@Override
	public ImmutableListValue<Skill> skills()
	{
		return this.skillsValue;
	}

	@Override
	protected void registerGetters()
	{
		registerKeyValue(PassiveSkillsPlugin.SKILLS, this::skills);
		registerFieldGetter(PassiveSkillsPlugin.SKILLS, this::getSkills);
	}

	@Override
	public SkillData asMutable()
	{
		return new SkillDataImpl(this.skills);
	}

	@Override
	public int getContentVersion()
	{
		return SkillDataBuilder.CONTENT_VERSION;
	}

	private List<Skill> getSkills()
	{
		return this.skills;
	}

	@Override
	public DataContainer toContainer()
	{
		final DataContainer container = super.toContainer();
		if(this.skills != null)
		{
			container.set(PassiveSkillsPlugin.SKILLS, this.skills);
		}
		return container;
	}
}
