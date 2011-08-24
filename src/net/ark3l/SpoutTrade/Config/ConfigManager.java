package net.ark3l.SpoutTrade.Config;

/*   SpoutTrade - In game GUI trading for Bukkit Minecraft servers with Spout
 Copyright (C) 2011  Oliver Brown (Arkel)

 TileEntityVirtualChest and VirtualChest classes are attributed to Balor and
 Timberjaw, the authors of GiftPost

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Util.LogLevel;
import net.ark3l.SpoutTrade.Util.Util;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class ConfigManager {

	private SpoutTrade plugin;
	private Configuration config;
	private File configFile;
	private Util util = Util.getInstance();

	public ConfigManager(SpoutTrade instance) {
		plugin = instance;

		File df = plugin.getDataFolder();

		// if the plugin data folder doesn't exist, make it
		if (!df.exists())
			df.mkdirs();

		configFile = new File(df, "config.yml");

		// if the config file doesn't exist, write the default one from the jar
		if (!configFile.exists()) {
			util.log(LogLevel.WARNING,
					"No configuration file found. Writing default.");
			util.writeDefault("config.yml", configFile);
		}

		// create and load the instance on the configuration class
		config = new Configuration(configFile);
		config.load();
	}

	public boolean isRightClickTradeEnabled() {
		return config.getBoolean("RightClickTrade", false);
	}

	public boolean isRangeCheckEnabled() {
		return config.getBoolean("RangeCheck.Enabled", false);
	}

	public int getRangeCheckDistance() {
		return config.getInt("RangeCheck.MaxDistance", 30);
	}

	public void close() {
		config.save();
	}

	/**
	 * @param player
	 * @param target
	 * @return
	 */
	public boolean canTrade(Player player, Player target) {

		if (!player.hasPermission("spouttrade.trade"))
			return false;

		if (isRangeCheckEnabled()) {
			if (player.getLocation().distance(target.getLocation()) > getRangeCheckDistance())
				return false;
		}

		return true;
	}

}
