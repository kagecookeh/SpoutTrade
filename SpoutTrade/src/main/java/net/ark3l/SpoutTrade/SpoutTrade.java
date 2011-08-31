package net.ark3l.SpoutTrade;

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
import java.util.HashMap;

import net.ark3l.SpoutTrade.Config.ConfigManager;
import net.ark3l.SpoutTrade.Listeners.SpoutTradeInventoryListener;
import net.ark3l.SpoutTrade.Listeners.SpoutTradePlayerListener;
import net.ark3l.SpoutTrade.Listeners.SpoutTradeScreenListener;
import net.ark3l.SpoutTrade.Trade.Trade;
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

/**
 * @author Oliver Brown
 * 
 */
public class SpoutTrade extends JavaPlugin {

    public HashMap<SpoutPlayer, TradeRequest> requests = new HashMap<SpoutPlayer, TradeRequest>();
    public HashMap<SpoutPlayer, Trade> trades = new HashMap<SpoutPlayer, Trade>();
    private ConfigManager config;
    private SpoutTradePlayerListener playerListener;
    private SpoutTradeInventoryListener invListener;
    private SpoutTradeScreenListener screenListener;
    private static SpoutTrade instance = null;

    @Override
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
            for (int i = 0; i < players.length; i++) {
                if (trades.get((SpoutPlayer) players[i]) != null) {
                    trades.get((SpoutPlayer) players[i]).abort();
                }
            }
            Log.info("Trades cancelled");
        }

    }

    @Override
    public void onEnable() {
        invListener = new SpoutTradeInventoryListener(
                this);
        screenListener = new SpoutTradeScreenListener(
                this);
        playerListener = new SpoutTradePlayerListener(
                this);


        instance = this;

        PluginDescriptionFile pdf = getDescription();
        Log.info("Version " + pdf.getVersion() + " enabled");

        config = new ConfigManager(this);

        PluginManager pm = this.getServer().getPluginManager();

        if (getConfig().isRightClickTradeEnabled()) {
            pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener,
                    Priority.Normal, this);
        }

        pm.registerEvent(Type.CUSTOM_EVENT, invListener, Priority.Normal, this);
        pm.registerEvent(Type.CUSTOM_EVENT, screenListener, Priority.Normal,
                this);

    }

    @Override
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
     * @param player
     * @param args
     */
    private boolean doCommand(SpoutPlayer player, String[] args) {

        if (args.length == 0) {
            // You must specify an option
            player.sendMessage(ChatColor.RED + getConfig().getString(0));
            return true;
        }

        if (requests.containsKey(player)) {

            if (args[0].equalsIgnoreCase("accept")) {
                requests.get(player).accept(player);
            } else if (args[0].equalsIgnoreCase("decline")) {
                requests.get(player).decline();
            }

        } else if (trades.containsKey(player)) {

            if (args[0].equalsIgnoreCase("confirm")) {
                trades.get(player).confirm(player);
            } else if (args[0].equalsIgnoreCase("reject")) {
                trades.get(player).reject();
            }

        } else {

            Player target = this.getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED
                        // The player you specified is not online
                        + getConfig().getString(1));
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

                    @Override
                    public void run() {
                        new TradeRequest(initiator, target);
                    }
                }, 15L);

    }

    /**
     * Checks if the player is currently involved in a trade or request
     * @param player - the player to check
     * @return - if they are involved in a trade or request
     */
    public boolean isBusy(Player player) {
        SpoutPlayer sPlayer = (SpoutPlayer) player;

        if (requests.containsKey(sPlayer) || trades.containsKey(sPlayer)) {
            return true;
        }

        return false;
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
}
