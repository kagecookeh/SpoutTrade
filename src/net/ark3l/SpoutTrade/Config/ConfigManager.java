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

package net.ark3l.SpoutTrade.Config;

import net.ark3l.SpoutTrade.Util.Log;
import org.bukkit.entity.Player;

import java.io.File;

public final class ConfigManager extends ConfigClass {

	public ConfigManager(File dataFolder) {
		super(dataFolder, new File(dataFolder, "config.yml"));

		// TODO - update this with each change to the config
		if(config.getAll().size() != 5) {
			Log.warning("Configuration is outdated! Delete it to generate a new one");
		}
	}

	/**
	 * Check if the right click to trade feature is enabled, defaults to false
	 *
	 * @return - whether right click trade is enabled
	 */
	public boolean isRightClickTradeEnabled() {
		return config.getBoolean("RightClickTrade", false);
	}

	/**
	 * Check if the range checking feature is enabled, defaults to false
	 *
	 * @return - whether range checking is enabled
	 */
	boolean isRangeCheckEnabled() {
		return config.getBoolean("RangeCheck.Enabled", false);
	}

	/**
	 * Get the configured range check distance, defaults to 30
	 *
	 * @return - the distance, as an integer
	 */
	int getRangeCheckDistance() {
		return config.getInt("RangeCheck.MaxDistance", 30);
	}

	/**
	 * Check if the update checking is enabled
	 *
	 * @return - whether update checking is enabled
	 */
	public boolean isUpdateCheckEnabled() {
		return config.getBoolean("CheckForUpdates", true);
	}

	/**
	 * Determines whether or not the two given players can trade
	 *
	 * @param player - the first player
	 * @param target - the second player
	 * @return - whether or not the two players can trade
	 */
	public boolean canTrade(Player player, Player target) {

		if(!player.hasPermission("spouttrade.trade")) {
			return false;
		}

		if(isRangeCheckEnabled()) {
			if(player.getLocation().distance(target.getLocation()) > getRangeCheckDistance()) {
				return false;
			}
		}

		return true;
	}

}
