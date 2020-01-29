package io.github.aquerr.passiveskills;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "passiveskills",
		name = "Passive Skills",
		version = "%VERSION%",
		description = "Adds passive skills which gives players bonuses while mining, woodcutting and fighting.",
		url = "https://github.com/Aquerr/PassiveSkills",
		authors = {"Aquerr"})
public class PassiveSkillsPlugin
{
	//PassiveSkills Managers

	//Sponge Managers
	private final EventManager eventManager;
	private final CommandManager commandManager;

	@Inject
	public PassiveSkillsPlugin(final EventManager eventManager, final CommandManager commandManager)
	{
		this.eventManager = eventManager;
		this.commandManager = commandManager;
	}

	@Listener
	public void onGameInit(final GameInitializationEvent event)
	{
		registerCommands();
		registerListeners();
	}

	private void registerCommands()
	{
		//Register Commands Here...
	}

	private void registerListeners()
	{
		//Register Commands here...
	}
}
