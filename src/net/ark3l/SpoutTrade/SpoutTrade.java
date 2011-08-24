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
import net.ark3l.SpoutTrade.Util.LogLevel;
import net.ark3l.SpoutTrade.Util.Util;

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

/**
 * @author Oliver Brown
 * 
 */
public class SpoutTrade extends JavaPlugin {

	public static HashMap<Player, TradeRequest> requests = new HashMap<Player, TradeRequest>();
	public static HashMap<Player, Trade> trades = new HashMap<Player, Trade>();

	private Util util = Util.getInstance();

	public ConfigManager config;

	private PluginManager pm;

	private SpoutTradePlayerListener playerListener = new SpoutTradePlayerListener(
			this);
	private SpoutTradeInventoryListener invListener = new SpoutTradeInventoryListener(
			this);
	private SpoutTradeScreenListener screenListener = new SpoutTradeScreenListener(
			this);

	@Override
	public void onDisable() {
		terminateActiveTrades();

		PluginDescriptionFile pdf = getDescription();
		util.log(LogLevel.INFO, "Version " + pdf.getVersion() + " disabled");
	}

	private void terminateActiveTrades() {

		if (!requests.isEmpty()) {
			requests.clear();
		}

		if (!trades.isEmpty()) {
			util.log(
					LogLevel.WARNING,
					"SpoutTrade detected that players were still trading. Attempting to cancel trades...");
			Player[] players = getServer().getOnlinePlayers();
			for (int i = 0; i < players.length; i++) {
				if (trades.get(players[i]) != null) {
					trades.get(players[i]).abort();
				}
			}
			util.log(LogLevel.INFO, "Trades cancelled");
		}

	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdf = getDescription();
		util.log(LogLevel.INFO, "Version " + pdf.getVersion() + " enabled");

		initialize();

		pm = this.getServer().getPluginManager();

		if (config.isRightClickTradeEnabled())
			pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener,
					Priority.Normal, this);

		pm.registerEvent(Type.CUSTOM_EVENT, invListener, Priority.Normal, this);
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
			return doCommand(player, args);
		}

		return super.onCommand(sender, cmd, commandLabel, args);
	}

	/**
	 * @param player
	 * @param args
	 */
	private boolean doCommand(Player player, String[] args) {

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "You must specify an option");
			return true;
		}

		if (requests.containsKey(player)) {

			if (args[0].equalsIgnoreCase("accept"))
				requests.get(player).accept(player);

			else if (args[0].equalsIgnoreCase("decline"))
				requests.get(player).decline(player);

		} else if (trades.containsKey(player)) {

			if (args[0].equalsIgnoreCase("confirm"))
				trades.get(player).confirm(player);

			else if (args[0].equalsIgnoreCase("reject"))
				trades.get(player).abort();

		} else {

			Player target = this.getServer().getPlayer(args[0]);

			if (target == null) {
				player.sendMessage(ChatColor.RED
						+ "The player you specified is not online");
				return true;
			}

			if (!requests.containsKey(player) && !trades.containsKey(player)
					&& config.canTrade(player, target))
				beginTrade(player, target);

		}
		return true;
	}

	/**
	 * @param player
	 * @param target
	 */
	private void beginTrade(final Player initiator, final Player target) {

		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {
					@Override
					public void run() {
						new TradeRequest(initiator, target);
					}
				}, 15L);

	}

	private void initialize() {

		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();

		config = new ConfigManager(this);

	}

}
