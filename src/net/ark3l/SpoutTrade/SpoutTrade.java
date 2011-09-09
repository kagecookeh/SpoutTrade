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

package net.ark3l.SpoutTrade;

import couk.Adamki11s.AutoUpdater.AUCore;
import net.ark3l.SpoutTrade.Config.ConfigManager;
import net.ark3l.SpoutTrade.Config.LanguageManager;
import net.ark3l.SpoutTrade.Listeners.SpoutTradeInventoryListener;
import net.ark3l.SpoutTrade.Listeners.SpoutTradePlayerListener;
import net.ark3l.SpoutTrade.Listeners.SpoutTradeScreenListener;
import net.ark3l.SpoutTrade.Trade.TradeManager;
import net.ark3l.SpoutTrade.Trade.TradeRequest;
import net.ark3l.SpoutTrade.Util.Log;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author Oliver Brown
 */
public class SpoutTrade extends JavaPlugin {

    public final HashMap<SpoutPlayer, TradeRequest> requests = new HashMap<SpoutPlayer, TradeRequest>();
    public final HashMap<SpoutPlayer, TradeManager> trades = new HashMap<SpoutPlayer, TradeManager>();

    private ConfigManager config;
    private LanguageManager lang;

    private static SpoutTrade instance = null;

    public void onDisable() {
        terminateActiveTrades();

        PluginDescriptionFile pdf = getDescription();
        Log.info("Version " + pdf.getVersion() + " disabled");
    }


    private void terminateActiveTrades() {

        if (!requests.isEmpty()) {
            requests.clear();
        }

        if (!trades.isEmpty()) {
            Log.warning(
                    "SpoutTrade detected that players were still trading. Attempting to cancel trades...");
            Player[] players = getServer().getOnlinePlayers();
            for (Player player : players) {
                if (trades.get(player) != null) {
                    trades.get(player).abort();
                }
            }
            Log.info("Trades cancelled");
        }

    }

    public void onEnable() {

        AUCore core = new AUCore("http://arkel.github.com/update", Logger.getLogger("Minecraft"), "[SpoutTradeUpdater]");

        double currentVer = 1.4, currentSubVer = 0;

        if (!core.checkVersion(currentVer, currentSubVer, "SpoutTrade")) {
            core.forceDownload("https://github.com/downloads/arkel/SpoutTrade/SpoutTrade-1.3.1.jar", "SpoutTrade");
        }

        SpoutTradeInventoryListener invListener = new SpoutTradeInventoryListener(
                this);
        SpoutTradeScreenListener screenListener = new SpoutTradeScreenListener(
                this);
        SpoutTradePlayerListener playerListener = new SpoutTradePlayerListener(
                this);


        instance = this;

        PluginDescriptionFile pdf = getDescription();
        Log.info("Version " + pdf.getVersion() + " enabled");

        config = new ConfigManager(getDataFolder());
        lang = new LanguageManager(getDataFolder());

        PluginManager pm = getServer().getPluginManager();

        if (config.isRightClickTradeEnabled()) {
            pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener,
                    Priority.Normal, this);
        }

        pm.registerEvent(Type.CUSTOM_EVENT, invListener, Priority.High, this);
        pm.registerEvent(Type.CUSTOM_EVENT, screenListener, Priority.Normal,
                this);

    }

    public boolean onCommand(CommandSender sender, Command cmd,
                             String commandLabel, String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("You must be a player to do that");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("trade")) {
            Player player = ((Player) sender);
            return doCommand((SpoutPlayer) player, args);
        }

        return super.onCommand(sender, cmd, commandLabel, args);
    }

    /**
     * @param player the player who sent the command
     * @param args   the command arguments
     * @return wheter the command was successful
     */
    private boolean doCommand(SpoutPlayer player, String[] args) {

        if (args.length == 0) {
            // You must specify an option
            player.sendMessage(ChatColor.RED + lang.getString(LanguageManager.Strings.OPTION));
            return true;
        }

        if (requests.containsKey(player)) {

            if (args[0].equalsIgnoreCase("accept")) {
                requests.get(player).accept(player);
            } else if (args[0].equalsIgnoreCase("decline")) {
                requests.get(player).decline();
            }

        } else if (trades.containsKey(player)) {

            if (args[0].equalsIgnoreCase("accept")) {
                trades.get(player).confirm(player);
            } else if (args[0].equalsIgnoreCase("decline")) {
                trades.get(player).reject();
            }

        } else {

            Player target = this.getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED
                        // The player you specified is not online
                        + lang.getString(LanguageManager.Strings.ONLINE));
                return true;
            }

            if (!isBusy(player)
                    && getConfig().canTrade(player, target)) {
                beginTrade(player, target);
            }

        }
        return true;
    }

    private void beginTrade(final Player initiator, final Player target) {

        getServer().getScheduler().scheduleSyncDelayedTask(this,
                new Runnable() {

                    public void run() {
                        new TradeRequest(initiator, target);
                    }
                }, 15L);

    }

    /**
     * Checks if the player is currently involved in a trade or request
     *
     * @param player - the player to check
     * @return - if they are involved in a trade or request
     */
    public boolean isBusy(Player player) {
        SpoutPlayer sPlayer = (SpoutPlayer) player;

        return requests.containsKey(sPlayer) || trades.containsKey(sPlayer);
    }

    public static SpoutTrade getInstance() {
        return instance;
    }

    /**
     * @return the current config instance
     */
    public ConfigManager getConfig() {
        return config;
    }

    public LanguageManager getLang() {
        return lang;
    }
}
