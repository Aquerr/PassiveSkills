package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.entities.Skill;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class SkillDataBuilder extends AbstractDataBuilder<SkillData> implements DataManipulatorBuilder<SkillData, ImmutableSkillData>
{
	public static final int CONTENT_VERSION = 2;

	public SkillDataBuilder()
	{
		super(SkillData.class, CONTENT_VERSION);
	}

	@Override
	public SkillData create()
	{
		return new SkillDataImpl();
	}

	@Override
	public Optional<SkillData> createFrom(DataHolder dataHolder)
	{
		return create().fill(dataHolder);
	}

	@Override
	protected Optional<SkillData> buildContent(DataView container) throws InvalidDataException
	{
		SkillData skillData = new SkillDataImpl();

		container.getSerializable(PassiveSkillsPlugin.MINING_SKILL.getQuery(), Skill.class).ifPresent(skill ->
		{
			skillData.set(PassiveSkillsPlugin.MINING_SKILL, skill);
		});

		return Optional.of(skillData);
	}
}
