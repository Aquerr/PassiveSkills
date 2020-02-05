package io.github.aquerr.passiveskills.util;

import io.github.aquerr.passiveskills.PassiveSkillsPlugin;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SkillBarUtil
{
	private static final Map<UUID, ServerBossBar> PLAYERS_SKILL_BARS = new HashMap<>();
	private static final Map<UUID, Integer> PLAYER_SKILL_BAR_SHOW_TIME = new HashMap<>();

	public static ServerBossBar getSkillBar(final Text name, BossBarColor color, final Player player, final float percentage)
	{
		if(PLAYERS_SKILL_BARS.containsKey(player.getUniqueId()))
		{
			return PLAYERS_SKILL_BARS.get(player.getUniqueId())
					.setPercent(percentage)
					.setColor(color)
					.setName(name);
		}


		final ServerBossBar bossBar = ServerBossBar.builder()
				.name(name)
				.color(color)
				.overlay(BossBarOverlays.NOTCHED_6)
				.createFog(false)
				.darkenSky(false)
				.visible(true)
				.percent(percentage)
				.build();

		return bossBar;
	}

	public static void showSkillBar(final Player player, final ServerBossBar skillBar)
    {
        skillBar.addPlayer(player);
        PLAYERS_SKILL_BARS.put(player.getUniqueId(), skillBar);
        PLAYER_SKILL_BAR_SHOW_TIME.put(player.getUniqueId(), 3);
        runSkillBarHideTask(player, skillBar);
    }

    private static void runSkillBarHideTask(final Player player, final ServerBossBar skillBar)
    {
        Task.Builder taskBuilder = Task.builder();
        taskBuilder.execute(task ->
        {
            boolean didHide = hideOrUpdateSkillBarTime(player, skillBar);
            if (didHide)
                task.cancel();
        })
                .interval(1, TimeUnit.SECONDS)
                .async()
                .submit(PassiveSkillsPlugin.getPlugin());
    }

    private static boolean hideOrUpdateSkillBarTime(final Player player, final ServerBossBar skillBar)
    {
        int secondsToHide = PLAYER_SKILL_BAR_SHOW_TIME.get(player.getUniqueId());
        if (secondsToHide == 0)
        {
            PLAYER_SKILL_BAR_SHOW_TIME.remove(player.getUniqueId());
            PLAYERS_SKILL_BARS.get(player.getUniqueId()).removePlayer(player);
            PLAYERS_SKILL_BARS.remove(player.getUniqueId());
            return true;
        }
        else
        {
            PLAYERS_SKILL_BARS.put(player.getUniqueId(), skillBar);
            PLAYER_SKILL_BAR_SHOW_TIME.put(player.getUniqueId(), secondsToHide - 1);
            return false;
        }
    }
}
