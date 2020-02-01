package io.github.aquerr.passiveskills.data;

import com.google.common.reflect.TypeToken;
import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.entities.FightingSkill;
import io.github.aquerr.passiveskills.entities.MiningSkill;
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

		if(!view.contains(SkillQueries.NAME_QUERY, SkillQueries.TYPE_QUERY, SkillQueries.LEVEL_QUERY, SkillQueries.EXPERIENCE_QUERY))
			throw new InvalidDataException("Incomplete data");

		final String name = view.getString(SkillQueries.NAME_QUERY).get();
		final SkillType skillType = SkillType.valueOf(view.getString(SkillQueries.TYPE_QUERY).get());
		final int level = view.getInt(SkillQueries.LEVEL_QUERY).get();
		final int experience = view.getInt(SkillQueries.EXPERIENCE_QUERY).get();
		if (skillType == SkillType.MINING)
			return new MiningSkill(level, experience);
		else if (skillType == SkillType.FIGHTING)
			return new FightingSkill(level, experience);
		return new Skill(name, SkillType.MINING, level, experience);
	}

	@Override
	public DataContainer translate(Skill obj) throws InvalidDataException
	{
		return new MemoryDataContainer()
				.set(SkillQueries.NAME_QUERY, obj.getName())
				.set(SkillQueries.LEVEL_QUERY, obj.getLevel())
				.set(SkillQueries.EXPERIENCE_QUERY, obj.getExperience())
				.set(SkillQueries.TYPE_QUERY, obj.getType().getName())
				.set(Queries.CONTENT_VERSION, SkillBuilder.CONTENT_VERSION);
	}

	@Override
	public String getId()
	{
		return "passiveskills:skill_translator";
	}

	@Override
	public String getName()
	{
		return "Skill Translator";
	}
}
