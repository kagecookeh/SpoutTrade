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
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Oliver Brown (Arkel)
 *         Date: 08/09/11
 */
public abstract class ConfigClass {

	protected Configuration config;

	public ConfigClass(File folder, File file) {
		if(!folder.exists()) {
			folder.mkdirs();
		}

		if(!file.exists()) {
			Log.warning("File " + file.getName() + " not found. Writing default");
			writeDefault(file);
		}

		config = new Configuration(file);
		config.load();

	}

	private void writeDefault(File outputFile) {
		try {

			File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
			if(jarloc.isFile()) {
				JarFile jar = new JarFile(jarloc);
				JarEntry entry = jar.getJarEntry(outputFile.getName());

				if(entry != null && !entry.isDirectory()) {

					InputStream in = jar.getInputStream(entry);
					FileOutputStream out = new FileOutputStream(outputFile);

					byte[] tempbytes = new byte[512];
					int readbytes = in.read(tempbytes, 0, 512);

					while(readbytes > -1) {
						out.write(tempbytes, 0, readbytes);
						readbytes = in.read(tempbytes, 0, 512);
					}

					out.close();
					in.close();

				}
			}

		} catch(Exception ex) {
			Log.severe("Error copying " + outputFile.getName() + " from Jar");
		}
	}
}
