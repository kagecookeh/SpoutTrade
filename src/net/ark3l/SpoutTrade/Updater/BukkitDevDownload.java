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

package net.ark3l.SpoutTrade.Updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BukkitDevDownload {

	private final String version;
	private final String link;

	public BukkitDevDownload(String version, String link) throws Exception {
		this.version = version;

		URL yahoo = new URL(link);
		BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream()));

		String inputLine;
		while((inputLine = in.readLine()) != null) {
			inputLine = inputLine.trim();
			if(inputLine.contains("Download")) {


				Pattern regex = Pattern.compile("http[^>]*jar", Pattern.MULTILINE);
				Matcher regexMatcher = regex.matcher(inputLine);
				if(regexMatcher.find()) {
					link = regexMatcher.group();
				}
			}
		}
		this.link = link;
	}

	public String getVersion() {
		return version;
	}

	public String getLink() {
		return link;
	}

	public String toString() {
		return version + "=" + link;
	}

}