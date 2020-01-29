package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.entities.SkillType;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class SkillBuilder extends AbstractDataBuilder<Skill>
{
	public static final int CONTENT_VERSION = 2;

	public SkillBuilder()
	{
		super(Skill.class, CONTENT_VERSION);
	}

	@Override
	protected Optional<Skill> buildContent(final DataView container) throws InvalidDataException
	{
		if(!container.contains(Skill.NAME_QUERY, Skill.TYPE_QUERY, Skill.LEVEL_QUERY, Skill.EXPERIENCE_QUERY))
			return Optional.empty();

		final String name = container.getString(Skill.NAME_QUERY).get();
		final int level = container.getInt(Skill.LEVEL_QUERY).get();
		final int experience = container.getInt(Skill.EXPERIENCE_QUERY).get();
		final SkillType type = SkillType.valueOf(container.getString(Skill.TYPE_QUERY).get());

		return Optional.of(new Skill(name, type, level, experience));
	}
}
