package io.github.aquerr.passiveskills.data;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import io.github.aquerr.passiveskills.entities.FightingSkill;
import io.github.aquerr.passiveskills.entities.MiningSkill;
import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.entities.SkillType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SkillDataImpl extends AbstractData<SkillData, ImmutableSkillData> implements SkillData
{
	private List<Skill> skills = new ArrayList<>();

	public SkillDataImpl()
	{
		this(Arrays.asList(new MiningSkill(), new FightingSkill()));
	}

	public SkillDataImpl(final List<Skill> skills)
	{
		this.skills.addAll(skills);
		this.registerGettersAndSetters();
	}

	@Override
	public ListValue<Skill> skills()
	{
		return Sponge.getRegistry().getValueFactory().createListValue(PassiveSkillsPlugin.SKILLS, this.skills);
	}

	@Override
	protected void registerGettersAndSetters()
	{
		registerKeyValue(PassiveSkillsPlugin.SKILLS, this::skills);
		registerFieldSetter(PassiveSkillsPlugin.SKILLS, this::setSkills);
		registerFieldGetter(PassiveSkillsPlugin.SKILLS, this::getSkills);
	}

	@Override
	public Optional<SkillData> fill(DataHolder dataHolder, MergeFunction overlap)
	{
		SkillData merged = overlap.merge(this, dataHolder.get(SkillData.class).orElse(null));
		this.skills = merged.skills().get();
		return Optional.of(this);
	}

	@Override
	public Optional<SkillData> from(DataContainer container)
	{
		if(!container.contains(PassiveSkillsPlugin.SKILLS))
			return Optional.empty();

		this.skills = container.getSerializableList(PassiveSkillsPlugin.SKILLS.getQuery(), Skill.class).get();
		return Optional.of(this);
	}

	@Override
	public SkillData copy()
	{
		return new SkillDataImpl(this.skills);
	}

	@Override
	public ImmutableSkillData asImmutable()
	{
		return new ImmutableSkillDataImpl(this.skills);
	}

	@Override
	public int getContentVersion()
	{
		return SkillDataBuilder.CONTENT_VERSION;
	}

	private List<Skill> getSkills()
	{
		return this.skills;
	}

	private void setSkills(final @Nullable List<Skill> skills)
	{
		this.skills = skills;
	}

	@Override
	public DataContainer toContainer()
	{
		DataContainer container = super.toContainer();
		if(this.skills != null)
		{
			container.set(PassiveSkillsPlugin.SKILLS, this.skills);
		}
		return container;
	}
}
