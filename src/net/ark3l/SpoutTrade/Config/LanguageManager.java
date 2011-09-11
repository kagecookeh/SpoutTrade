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

import java.io.File;
import java.util.List;

/**
 * @author Oliver Brown (Arkel)
 *         Date: 08/09/11
 */
public class LanguageManager extends ConfigClass {

	public enum Strings {OPTION, ONLINE, BUSY, REQUESTED, TOACCEPT, TODECLINE, CANCELLED, CONFIRMED, NOTYOURS, NOROOM, FINISHED, SURE, SENT, TIMED, DECLINED}

	private List<Object> stringList;

	public LanguageManager(File dataFolder) {
		super(dataFolder, new File(dataFolder, "language.yml"));
		stringList = config.getList("Language");

		// TODO - update this with each change to the language file
		if(stringList.size() != 15) {
			Log.warning("Language is outdated! Delete it to generate a new one");
		}
	}

	public String getString(Strings type) {
		return (String) stringList.get(type.ordinal());
	}
}
