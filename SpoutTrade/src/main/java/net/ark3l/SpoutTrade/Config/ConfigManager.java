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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Util.Log;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public final class ConfigManager {

    private SpoutTrade plugin;
    private Configuration config;
    private File configFile;
    private List<String> stringCache;

    public ConfigManager(SpoutTrade instance) {
        plugin = instance;

        File df = plugin.getDataFolder();

        // if the plugin data folder doesn't exist, make it
        if (!df.exists()) {
            df.mkdirs();
        }

        configFile = new File(df, "config.yml");

        // if the config file doesn't exist, write the default one from the jar
        if (!configFile.exists()) {
            Log.warning("No configuration file found. Writing default.");
            writeDefault("config.yml", configFile);
        }

        // create and load

        config = new Configuration(configFile);
        config.load();

        stringCache = config.getStringList("Localisation.Strings", null);
    }

    /**
     * Check if the right click to trade feature is enabled, defaults to false
     * @return - whether right click trade is enabled
     */
    public boolean isRightClickTradeEnabled() {
        return config.getBoolean("RightClickTrade", false);
    }

    /**
     * Check if the range checking feature is enabled, defaults to false
     * @return - whether range checking is enabled
     */
    public boolean isRangeCheckEnabled() {
        return config.getBoolean("RangeCheck.Enabled", false);
    }

    /**
     * Get the configured range check distance, defaults to 30
     * @return - the distance, as an integer
     */
    public int getRangeCheckDistance() {
        return config.getInt("RangeCheck.MaxDistance", 30);
    }

    /**
     * Saves the configuration
     */
    public void save() {
        config.save();
    }

    /**
     * Returns the string that corresponds to the given ID, used for localization
     * @param ID - the strings ID
     * @return - the string, taken from the local cache
     */
    public String getString(int ID) {
        return stringCache.get(ID);
    }


    /**
     * Determines whether or not the two given players can trade
     * @param player - the first player
     * @param target - the second player
     * @return - whether or not the two players can trade
     */
    public boolean canTrade(Player player, Player target) {

        if (!player.hasPermission("spouttrade.trade")) {
            return false;
        }

        if (isRangeCheckEnabled()) {
            if (player.getLocation().distance(target.getLocation()) > getRangeCheckDistance()) {
                return false;
            }
        }

        return true;
    }

    private void writeDefault(String location, File outputFile) {
        try {

            File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
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
            Log.severe("Error copying default config from Jar");
        }
    }
}
