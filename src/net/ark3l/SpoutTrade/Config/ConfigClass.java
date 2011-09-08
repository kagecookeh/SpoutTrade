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
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!file.exists()) {
            Log.warning("File " + file.getName() + " not found. Writing default");
            writeDefault(file);
        }

        config = new Configuration(file);
        config.load();

    }

    private void writeDefault(File outputFile) {
        try {

            File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
            if (jarloc.isFile()) {
                JarFile jar = new JarFile(jarloc);
                JarEntry entry = jar.getJarEntry(outputFile.getName());

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
            Log.severe("Error copying " + outputFile.getName() + " from Jar");
        }
    }
}
