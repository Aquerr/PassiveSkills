package io.github.aquerr.passiveskills;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.aquerr.passiveskills.data.*;
import io.github.aquerr.passiveskills.entities.FightingSkill;
import io.github.aquerr.passiveskills.entities.MiningSkill;
import io.github.aquerr.passiveskills.entities.Skill;
import io.github.aquerr.passiveskills.listeners.AttackEntityListener;
import io.github.aquerr.passiveskills.listeners.BlockBreakListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

@Plugin(id = "passiveskills",
		name = "Passive Skills",
		version = "%VERSION%",
		description = "Adds passive skills which gives players bonuses while mining, woodcutting and fighting.",
		url = "https://github.com/Aquerr/PassiveSkills",
		authors = {"Aquerr"})
public class PassiveSkillsPlugin
{
	private static PassiveSkillsPlugin INSTANCE = null;

	//PassiveSkills Data
	public static Key<ListValue<Skill>> SKILLS = DummyObjectProvider.createExtendedFor(Key.class, "PASSIVE_SKILLS");

	//PassiveSkills Data Keys
	public static Key<Value<MiningSkill>> MINING_SKILL = DummyObjectProvider.createExtendedFor(Key.class, "MINING_SKILL");
	public static Key<Value<Skill>> LUMBER_SKILL = DummyObjectProvider.createExtendedFor(Key.class, "LUMBER_SKILL");
	public static Key<Value<FightingSkill>> FIGHTING_SKILL = DummyObjectProvider.createExtendedFor(Key.class, "FIGHTING_SKILL");
	public static Key<Value<Skill>> FISHING_SKILL = DummyObjectProvider.createExtendedFor(Key.class, "FISHING_SKILL");


	//PassiveSkills Managers

	@Inject
	private PluginContainer pluginContainer;

	private DataRegistration<SkillData, ImmutableSkillData> SKILL_DATA_REGISTRATION;

	//Sponge Managers
	private final EventManager eventManager;
	private final CommandManager commandManager;

	@Inject
	public PassiveSkillsPlugin(final EventManager eventManager, final CommandManager commandManager)
	{
		this.eventManager = eventManager;
		this.commandManager = commandManager;
	}

	public static PassiveSkillsPlugin getPlugin()
	{
		return INSTANCE;
	}

	@Listener
	public void onGameInit(final GameInitializationEvent event)
	{
		INSTANCE = this;
		registerCommands();
		registerListeners();
	}

	@Listener
	public void onKeyRegistration(final GameRegistryEvent.Register<Key<?>> event)
	{
		registerKeys();
	}

	@Listener
	public void onDataRegistration(final GameRegistryEvent.Register<DataRegistration<?, ?>> event)
	{
		final DataManager dataManager = Sponge.getDataManager();

		dataManager.registerBuilder(Skill.class, new SkillBuilder());
//		dataManager.registerContentUpdater(Skill.class, new SkillBuilder.SkillUp());
//		dataManager.registerContentUpdater(SkillData.class, new SkillDataBuilder.SkillUpdater());

		this.SKILL_DATA_REGISTRATION = DataRegistration.builder()
				.dataClass(SkillData.class)
				.immutableClass(ImmutableSkillData.class)
				.builder(new SkillDataBuilder())
				.dataImplementation(SkillDataImpl.class)
				.immutableImplementation(ImmutableSkillDataImpl.class)
				.dataName("Skill Data")
				.manipulatorId("skill-data")
				.buildAndRegister(this.pluginContainer);
	}

	private void registerKeys()
	{
		SKILLS = Key.builder()
				.type(new TypeToken<ListValue<Skill>>(){})
				.id("passive_skills")
				.name("Passive Skills")
				.query(DataQuery.of("PassiveSkills"))
				.build();

		MINING_SKILL = Key.builder()
				.type(new TypeToken<Value<MiningSkill>>(){})
				.id("mining_skill")
				.name("Mining Skill")
				.query(DataQuery.of("PassiveSkills", "MiningSkill"))
				.build();

		FIGHTING_SKILL = Key.builder()
				.type(new TypeToken<Value<FightingSkill>>(){})
				.id("fighting_skill")
				.name("Fighting Skill")
				.query(DataQuery.of("PassiveSkills", "FightingSkill"))
				.build();

		//Register other keys here...
	}

	private void registerCommands()
	{
		//Register Commands Here...
	}

	private void registerListeners()
	{
		this.eventManager.registerListeners(this, new BlockBreakListener());
		this.eventManager.registerListeners(this, new AttackEntityListener());
		//Register Commands here...
	}
}
