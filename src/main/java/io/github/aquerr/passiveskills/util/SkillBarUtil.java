package io.github.aquerr.passiveskills.util;

import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class SkillBarUtil
{
	private static final Map<UUID, ServerBossBar> playersSkillBars = new HashMap<>();

	public static ServerBossBar getSkillBar(final Player player, final Text name, final float percentage)
	{
		if(playersSkillBars.containsKey(player.getUniqueId()))
			return playersSkillBars.get(player.getUniqueId()).setPercent(percentage);


		ServerBossBar bossBar = ServerBossBar.builder()
				.name(name)
				.color(BossBarColors.BLUE)
				.overlay(BossBarOverlays.NOTCHED_6)
				.createFog(false)
				.darkenSky(false)
				.visible(true)
				.percent(percentage)
				.build();

		playersSkillBars.put(player.getUniqueId(), bossBar);
		return bossBar;
	}
}
