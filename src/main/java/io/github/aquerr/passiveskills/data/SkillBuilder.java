package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.entities.MiningSkill;
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
		if(!container.contains(SkillQueries.NAME_QUERY, SkillQueries.TYPE_QUERY, SkillQueries.LEVEL_QUERY, SkillQueries.EXPERIENCE_QUERY))
			return Optional.empty();

		final String name = container.getString(SkillQueries.NAME_QUERY).get();
		final int level = container.getInt(SkillQueries.LEVEL_QUERY).get();
		final int experience = container.getInt(SkillQueries.EXPERIENCE_QUERY).get();
		final SkillType skillType = SkillType.valueOf(container.getString(SkillQueries.TYPE_QUERY).get());

		if (skillType == SkillType.MINING)
			return Optional.of(new MiningSkill(level, experience));
//		else if (skillType == SkillType.FIGHTING)
//			return Optional.of(new FightingSkill(level, experience));
		return Optional.of(new Skill(name, SkillType.MINING, level, experience));
	}
}
