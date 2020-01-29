package io.github.aquerr.passiveskills.entities;

import io.github.aquerr.passiveskills.data.SkillBuilder;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.Queries;

public class Skill implements DataSerializable
{
	public static final DataQuery LEVEL_QUERY = DataQuery.of("Level");
	public static final DataQuery EXPERIENCE_QUERY = DataQuery.of("Experience");
	public static final DataQuery NAME_QUERY = DataQuery.of("Name");
	public static final DataQuery TYPE_QUERY = DataQuery.of("Type");

	private final String name;
	private final SkillType skillType;
	private int level;
	private int requiredToNextLevel;
	private int experience;

	public Skill(final String name, final SkillType skillType)
	{
		this(name, skillType, 0, 0);
	}

	public Skill(final String name, final SkillType skillType, final int level, final int experience)
	{
		this.name = name;
		this.skillType = skillType;
		this.level = level;
		this.experience = experience;
		calculateRequiredExpToNextLevel();
	}

	public SkillType getSkillType()
	{
		return this.skillType;
	}

	public String getName()
	{
		return this.name;
	}

	public int getExperience()
	{
		return this.experience;
	}

	public int getLevel()
	{
		return this.level;
	}

	public void addExperience(int experience)
	{
		if(this.experience + experience > this.requiredToNextLevel)
		{
			int rest = this.requiredToNextLevel - this.experience;
			this.experience = experience - rest;
			levelUp();
		}

		this.experience = this.experience + experience;
	}

	private void levelUp()
	{
		this.level++;
		calculateRequiredExpToNextLevel();
	}

	private void calculateRequiredExpToNextLevel()
	{
		if(this.level == 0)
			this.requiredToNextLevel = 100;

		int result = 100;
		for(int i = 1; i <= this.level; i++)
		{
			result = (int)(result * 1.5);
		}
		this.requiredToNextLevel = result;
	}

	@Override
	public int getContentVersion()
	{
		return SkillBuilder.CONTENT_VERSION;
	}

	@Override
	public DataContainer toContainer()
	{
		return DataContainer.createNew()
				.set(NAME_QUERY, this.name)
				.set(LEVEL_QUERY, this.level)
				.set(EXPERIENCE_QUERY, this.experience)
				.set(TYPE_QUERY, this.skillType)
				.set(Queries.CONTENT_VERSION, SkillBuilder.CONTENT_VERSION);
	}
}
