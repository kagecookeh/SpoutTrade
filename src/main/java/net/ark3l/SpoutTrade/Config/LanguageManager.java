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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * @author Oliver Brown (Arkel)
 *         Date: 08/09/11
 */
public class LanguageManager {

	public enum Strings {OPTION, ONLINE, BUSY, REQUESTED, TOACCEPT, TODECLINE, CANCELLED, CONFIRMED, NOTYOURS, NOROOM, FINISHED, SURE, SENT, TIMED, DECLINED, YOURSELF}

	private static List stringList = null;

	File configurationFile;
	YamlConfiguration config;

	public LanguageManager(Plugin plugin) {
		configurationFile = new File(plugin.getDataFolder(), "language.yml");
		config = YamlConfiguration.loadConfiguration(configurationFile);

		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource("language.yml");
		if(defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

			config.setDefaults(defConfig);
		}

		config.options().copyDefaults(true);
		save();

		stringList = config.getList("Language");
	}

	public void save() {
		try {
			config.save(configurationFile);
		} catch(IOException ex) {
			Log.severe("Could not save config to " + configurationFile + ex);
		}
	}

	public static String getString(Strings type) {
		return (String) stringList.get(type.ordinal());
	}
}
