package io.github.aquerr.passiveskills.data;

import com.google.common.reflect.TypeToken;
import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.entities.SkillType;
import org.spongepowered.api.data.*;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class SkillTranslator implements DataTranslator<Skill>
{
	public static final int CONTENT_VERSION = 2;

	@Override
	public TypeToken<Skill> getToken()
	{
		return TypeToken.of(Skill.class);
	}

	@Override
	public Skill translate(DataView view) throws InvalidDataException
	{
		view.getInt(Queries.CONTENT_VERSION).ifPresent(version ->
		{
			if(version != CONTENT_VERSION)
			{
				throw new InvalidDataException("Version incompatible: " + version);
			}
		});

		if(!view.contains(Skill.NAME_QUERY, Skill.TYPE_QUERY, Skill.LEVEL_QUERY, Skill.EXPERIENCE_QUERY))
			throw new InvalidDataException("Incomplete data");

		final String name = view.getString(Skill.NAME_QUERY).get();
		final SkillType skillType = SkillType.valueOf(view.getString(Skill.TYPE_QUERY).get());
		final int level = view.getInt(Skill.LEVEL_QUERY).get();
		final int experience = view.getInt(Skill.EXPERIENCE_QUERY).get();
		return new Skill(name, skillType, level, experience);
	}

	@Override
	public DataContainer translate(Skill obj) throws InvalidDataException
	{
		return new MemoryDataContainer()
				.set(Skill.NAME_QUERY, obj.getName())
				.set(Skill.LEVEL_QUERY, obj.getLevel())
				.set(Skill.EXPERIENCE_QUERY, obj.getExperience())
				.set(Skill.TYPE_QUERY, obj.getSkillType().getName())
				.set(Queries.CONTENT_VERSION, SkillBuilder.CONTENT_VERSION);
	}

	@Override
	public String getId()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return null;
	}
}
