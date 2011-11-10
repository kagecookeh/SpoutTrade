/*
 *  SpoutTrade - In game GUI trading for Bukkit Minecraft servers with Spout
 * Copyright (C) 2011 Oliver Brown (Arkel)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * /
 */

package net.ark3l.SpoutTrade.Listeners;

import net.ark3l.SpoutTrade.Config.LanguageManager;
import net.ark3l.SpoutTrade.SpoutTrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutTradePlayerListener extends PlayerListener {

	private final SpoutTrade plugin;

	public SpoutTradePlayerListener(SpoutTrade instance) {
		plugin = instance;
	}

	/**
	 * Handles a player interact entity event
	 *
	 * @param event the event
	 */
	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(!(event.getRightClicked() instanceof Player)) {
			return;
		}

		Player target = (Player) event.getRightClicked();

		// prevent trading with citizens NPCs
		if(plugin.getServer().getPluginManager().isPluginEnabled("Citizens")) {
			if(plugin.getServer().getPlayer(((Player) event.getRightClicked()).getName()) == null) {
				return;
			}
		}

		SpoutPlayer player = (SpoutPlayer) event.getPlayer();

		// prevent trading with a busy player
		if(plugin.isBusy(target)) {
			// that player is already trading
			player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.BUSY));
			return;
		}

		if(player.getItemInHand().getType() == Material.BOW) {
			return;
		}

		plugin.beginTrade(player, (SpoutPlayer) target);
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.getTradeManager().onPlayerQuit((SpoutPlayer) event.getPlayer());
	}

}
