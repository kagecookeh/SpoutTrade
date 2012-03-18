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

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class ConfigManager {

    private final FileConfiguration config;
    private final Plugin plugin;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();
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
     * Check if the right click to trade feature is enabled, defaults to false
     *
     * @return - whether right click trade is enabled
     */
    public boolean isVerboseLoggingEnabled() {
        return config.getBoolean("Verbose", true);
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

        if (!player.hasPermission("spouttrade.trade")) {
            return false;
        }

        if (player.getGameMode().equals(GameMode.CREATIVE) || target.getGameMode().equals(GameMode.CREATIVE)) {
            player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.UNABLE));
            player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.CREATIVE));
            return false;
        }

        if (isRangeCheckEnabled()) {
            if (player.getWorld() != target.getWorld()) {
                player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.TOOFAR));
                return false;
            }
            if (player.getLocation().distance(target.getLocation()) > getRangeCheckDistance()) {
                player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.TOOFAR));
                return false;
            }
        }

        return true;
    }

}
