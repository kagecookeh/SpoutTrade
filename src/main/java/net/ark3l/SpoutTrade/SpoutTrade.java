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

import net.ark3l.SpoutTrade.Config.ConfigManager;
import net.ark3l.SpoutTrade.Config.LanguageManager;
import net.ark3l.SpoutTrade.Listeners.SpoutTradeInventoryListener;
import net.ark3l.SpoutTrade.Listeners.SpoutTradePlayerListener;
import net.ark3l.SpoutTrade.Listeners.SpoutTradeScreenListener;
import net.ark3l.SpoutTrade.Trade.TradeManager;
import net.ark3l.SpoutTrade.Trade.TradePlayer;
import net.ark3l.SpoutTrade.Updater.UpdateChecker;
import net.ark3l.SpoutTrade.Util.Log;
import net.ark3l.SpoutTrade.Util.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Brown (Arkel)
 */
public class SpoutTrade extends JavaPlugin {

    private ConfigManager config;
    private LanguageManager languageManager;

    private TradeManager manager;

    private List<String> playersIgnoring = new ArrayList<String>();

    private Metrics.Graph requestGraph;
    private int tradeRequests = 0;

    public void onDisable() {
        manager.terminateActiveTrades();
        Log.info(this + " disabled");
    }

    public void onEnable() {
        config = new ConfigManager(this);
        languageManager = new LanguageManager(this);
        manager = new TradeManager(this);

        if (config.isUpdateCheckEnabled()) {
            UpdateChecker.checkForUpdates(this);
        }

        // Call listeners to register events in constructors
        new SpoutTradeInventoryListener(this);
        new SpoutTradeScreenListener(this);
        new SpoutTradePlayerListener(this);

        Log.verbose = config.isVerboseLoggingEnabled();

        try {
            Metrics metrics = new Metrics();

            requestGraph = metrics.createGraph(this, Metrics.Graph.Type.Line, "Number of Trade Requests");
            requestGraph.addPlotter(new Metrics.Plotter("Trade Request") {
                @Override
                public int getValue() {
                    int i = tradeRequests;
                    tradeRequests = 0;
                    return i;
                }
            });

            metrics.beginMeasuringPlugin(this);
        } catch (IOException e) {
            Log.warning("Failed to submit usage stats");
        }

        Log.info(this + " enabled");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

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
     * @return whether the command was successful
     */
    private boolean doCommand(SpoutPlayer player, String[] args) {

        if (args.length == 0) {
            // You must specify an option
            player.sendMessage(ChatColor.RED + languageManager.getString(LanguageManager.Strings.OPTION));

        } else if ("accept".equalsIgnoreCase(args[0]) || "decline".equalsIgnoreCase(args[0])) {

            manager.handleCommand(args[0], player);

        } else if ("ignore".equalsIgnoreCase(args[0])) {

            if (playersIgnoring.contains(player.getName())) {
                playersIgnoring.remove(player.getName());
                player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.NOTIGNORING));
            } else {
                playersIgnoring.add(player.getName());
                player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.IGNORING));
            }

        } else {

            Player target;

            if ((target = getServer().getPlayer(args[0])) == null) {
                player.sendMessage(ChatColor.RED
                        // The player you specified is not online
                        + languageManager.getString(LanguageManager.Strings.ONLINE));
                return true;
            } else if (player.equals(target)) {
                player.sendMessage(ChatColor.RED
                        // You can't trade with yourself!
                        + languageManager.getString(LanguageManager.Strings.YOURSELF));
                return true;
            }


            if (!isBusy(player)) {
                requestTrade(player, (SpoutPlayer) target);
            } else {
                // Unable to trade with <target name>
                player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.UNABLE) + " " + target.getName());
            }

        }
        return true;
    }

    /**
     * Attempts to begin a trade for the two given players
     *
     * @param initiator The player who initiated the trade
     * @param target    The target of the initiator
     */
    public void requestTrade(SpoutPlayer initiator, SpoutPlayer target) {
        tradeRequests++;

        if (playersIgnoring.contains(target.getName())) {
            initiator.sendMessage(ChatColor.RED + target.getName() + " " + LanguageManager.getString(LanguageManager.Strings.PLAYERIGNORING));
        } else if (config.canTrade(initiator, target)) {
            manager.begin(new TradePlayer(initiator), new TradePlayer(target));
        }
    }

    /**
     * Get the current instance of the TradeManager
     *
     * @return The current instance of the TradeManager
     */
    public TradeManager getTradeManager() {
        return manager;
    }

    /**
     * Checks if the player is currently involved in a trade or request
     *
     * @param player - the player to check
     * @return - if they are involved in a trade or request
     */
    public boolean isBusy(Player player) {
        return manager.isBusy((SpoutPlayer) player);
    }

    public ConfigManager getConfigManager() {
        return config;
    }

}
