package io.github.aquerr.passiveskills.entities;

public enum SkillType
{
	MINING("Mining"),
	WOODCUTTING("Woodcutting"),
	FARMING("Farming"),
	FISHING("Fishing"),
	FIGHTING("Fighting");

	private final String name;

	SkillType(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}
