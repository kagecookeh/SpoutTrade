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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Brown
 */
public class SpoutTrade extends JavaPlugin {

    private ConfigManager config;
    private LanguageManager lang;

    private TradeManager manager;

    private List<String> playersIgnoring = new ArrayList<String>();

    public void onDisable() {
        manager.terminateActiveTrades();
        PluginDescriptionFile pdf = getDescription();

        lang.save();
        config.save();

        Log.info("Version " + pdf.getVersion() + " disabled");
    }

    public void onEnable() {
        config = new ConfigManager(this);
        lang = new LanguageManager(this);
        manager = new TradeManager(this);

        if (config.isUpdateCheckEnabled()) {
            UpdateChecker.checkForUpdates(this);
        }

        SpoutTradeInventoryListener invListener = new SpoutTradeInventoryListener(this);
        SpoutTradeScreenListener screenListener = new SpoutTradeScreenListener(this);
        SpoutTradePlayerListener playerListener = new SpoutTradePlayerListener(this);

        PluginManager pm = getServer().getPluginManager();

        if (config.isRightClickTradeEnabled()) {
            pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Normal, this);
        }

        pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Low, this);
        pm.registerEvent(Type.PLAYER_DROP_ITEM, playerListener, Priority.High, this);
        pm.registerEvent(Type.CUSTOM_EVENT, invListener, Priority.Highest, this);
        pm.registerEvent(Type.CUSTOM_EVENT, screenListener, Priority.Normal, this);

        Log.verbose = config.isVerboseLoggingEnabled();

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
     * @return wheter the command was successful
     */
    private boolean doCommand(SpoutPlayer player, String[] args) {

        if (args.length == 0) {
            // You must specify an option
            player.sendMessage(ChatColor.RED + lang.getString(LanguageManager.Strings.OPTION));
            return true;
        }
        if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("decline")) {

            manager.handleCommand(args[0], player);

        } else if (args[0].equalsIgnoreCase("ignore")) {
            if (playersIgnoring.contains(player.getName())) {
                playersIgnoring.remove(player.getName());
                player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.NOTIGNORING));
            } else {
                playersIgnoring.add(player.getName());
                player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.IGNORING));
            }
        } else {

            Player target = this.getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED
                        // The player you specified is not online
                        + lang.getString(LanguageManager.Strings.ONLINE));
                return true;
            }

            if (player.equals(target)) {
                player.sendMessage(ChatColor.RED
                        // You can't trade with yourself!
                        + lang.getString(LanguageManager.Strings.YOURSELF));
                return true;
            }

            if (!isBusy(player)) {
                beginTrade(player, (SpoutPlayer) target);
            } else {
                player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.UNABLE) + " " + target.getName());
            }

        }
        return true;
    }

    public void beginTrade(SpoutPlayer initiator, SpoutPlayer target) {
        if (playersIgnoring.contains(target.getName())) {
            initiator.sendMessage(ChatColor.RED + target.getName() + " " + LanguageManager.getString(LanguageManager.Strings.PLAYERIGNORING));
        } else if(config.canTrade(initiator, target)) {
            manager.begin(new TradePlayer(initiator), new TradePlayer(target));
        }
    }

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

}
