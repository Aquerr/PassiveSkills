package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.entities.Skill;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;

public interface SkillData extends DataManipulator<SkillData, ImmutableSkillData>
{
	ListValue<Skill> skills();
//	Value<Skill> skill();
}
