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

/**
 * @author Oliver Brown (Arkel)
 *         Date: 11/09/11
 */

import net.ark3l.SpoutTrade.Util.Log;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class UpdateChecker {

	private static BukkitDevDownload getBukkitDevDownload(String plugin) throws Exception {

		URL yahoo = new URL("http://dev.bukkit.org/server-mods/" + plugin + "/files/");
		BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream()));

		String inputLine;
		while((inputLine = in.readLine()) != null) {

			inputLine = inputLine.trim();
			if(inputLine.startsWith("<td") && inputLine.contains("<a href=\"/server-mods/" + "spouttrade" + "/files")) {

				String result = "";
				try {
					Pattern regex = Pattern.compile("/[^\"]*" + "spouttrade" + "[^\"]*", Pattern.MULTILINE);
					Matcher regexMatcher = regex.matcher(inputLine);
					if(regexMatcher.find()) {
						result = "http://dev.bukkit.org" + regexMatcher.group();
					}
				} catch(PatternSyntaxException ex) {
					// Syntax error in the regular expression
				}
				String version = "";
				try {
					Pattern regex = Pattern.compile("(?<=\">)[^<]*(?=</a>)", Pattern.MULTILINE);
					Matcher regexMatcher = regex.matcher(inputLine);
					if(regexMatcher.find()) {
						version = regexMatcher.group();
					}
				} catch(PatternSyntaxException ex) {
					ex.printStackTrace();
				}
				in.close();
				return new BukkitDevDownload(version, result);
			}
		}
		in.close();
		return null;
	}

	public static void checkForUpdates(Plugin plugin) {

		Log.info("Checking for updates...");
		try {
			BukkitDevDownload bdd = getBukkitDevDownload("spouttrade");
			String[] version = bdd.getVersion().split(" ");
			String[] versionNumbers = version[0].split(".");

			int count = 0;
			for(int i = 0; i < versionNumbers.length; i++) {
				count += Integer.parseInt(versionNumbers[i]) * (4 - i);
			}

			if(!plugin.getDescription().getVersion().equalsIgnoreCase("v" + version[0])) {
				Log.warning("This version is out of date!");

				URL google = new URL(bdd.getLink());
				ReadableByteChannel rbc = Channels.newChannel(google.openStream());

				File directory = new File(plugin.getServer().getUpdateFolder());
				if(!directory.exists()) {
					directory.mkdirs();
				}

				File file = new File(directory.getPath(), "SpoutTrade.jar");
				if(file.exists()) {
					Log.info("Jar already exists in update folder.");
				} else {
					Log.info("Downloading latest version.");

					FileOutputStream fos = new FileOutputStream(file);
					fos.getChannel().transferFrom(rbc, 0, 1 << 24);
				}
			}

		} catch(Exception e) {
			Log.severe("Error while checking for updates!");
		}
	}
}