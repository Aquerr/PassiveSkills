package io.github.aquerr.passiveskills.entities;

import io.github.aquerr.passiveskills.data.SkillBuilder;
import io.github.aquerr.passiveskills.data.SkillQueries;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.Queries;

public class Skill implements DataSerializable
{
	private String name;
	private int level;
	private int requiredToNextLevel;
	private int experience;
	private SkillType skillType;

	public Skill(final String name)
	{
		this(name, SkillType.MINING, 0, 0);
	}

	public Skill(final String name, SkillType skillType, final int level, final int experience)
	{
		this.name = name;
		this.level = level;
		this.experience = experience;
		this.skillType = skillType;
		calculateRequiredExpToNextLevel();
	}

	public SkillType getType()
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

	public float getLevelPercentage()
	{
		return (float) this.experience / this.requiredToNextLevel;
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
				.set(SkillQueries.NAME_QUERY, this.name)
				.set(SkillQueries.LEVEL_QUERY, this.level)
				.set(SkillQueries.EXPERIENCE_QUERY, this.experience)
				.set(Queries.CONTENT_VERSION, SkillBuilder.CONTENT_VERSION);
	}
}
