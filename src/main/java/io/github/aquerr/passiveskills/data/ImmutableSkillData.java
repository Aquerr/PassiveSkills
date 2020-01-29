package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.entities.Skill;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public interface ImmutableSkillData extends ImmutableDataManipulator<ImmutableSkillData, SkillData>
{
	ImmutableValue<Skill> skill();
}
