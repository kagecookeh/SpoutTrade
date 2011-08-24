/**
 * 
 */
package net.ark3l.SpoutTrade.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * @author Oliver
 * 
 */
public class Util {

	private static Util instance;
	private Logger log = Logger.getLogger("Minecraft");

	private Util() {
		instance = this;
	}

	public static Util getInstance() {
		if (instance == null)
			return new Util();
		else
			return instance;

	}

	public void log(LogLevel level, String msg) {

		switch (level) {
		case INFO:
			log.info("[SpoutTrade] " + msg);
			break;
		case WARNING:
			log.warning("[SpoutTrade] " + msg);
			break;
		case SEVERE:
			log.severe("[SpoutTrade] " + msg);
			break;
		}
	}

	/**
	 * @param string
	 * @param configFile
	 */
	public void writeDefault(String location, File outputFile) {
		try {

			File jarloc = new File(getClass().getProtectionDomain()
					.getCodeSource().getLocation().toURI()).getCanonicalFile();
			if (jarloc.isFile()) {
				JarFile jar = new JarFile(jarloc);
				JarEntry entry = jar.getJarEntry(location);

				if (entry != null && !entry.isDirectory()) {

					InputStream in = jar.getInputStream(entry);
					FileOutputStream out = new FileOutputStream(outputFile);

					byte[] tempbytes = new byte[512];
					int readbytes = in.read(tempbytes, 0, 512);

					while (readbytes > -1) {
						out.write(tempbytes, 0, readbytes);
						readbytes = in.read(tempbytes, 0, 512);
					}

					out.close();
					in.close();

				}
			}

		} catch (Exception ex) {
			log(LogLevel.SEVERE, "Error copying default config from Jar");
			ex.printStackTrace();
		}
	}

}
