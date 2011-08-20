/*   SpoutTrade - In game GUI trading for Bukkit with Spout
    Copyright (C) 2011  Oliver Brown
    
    TileEntityVirtualChest and VirtualChest classes attributed to the authors
    of GiftPost

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
package net.ark3l.SpoutTrade;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpoutTrade extends JavaPlugin {

	private static HashMap<Player, Trade> traders = new HashMap<Player, Trade>();

	public static HashMap<Player, Trade> getTraders() {
		return traders;
	}

	public static Logger log = Logger.getLogger("Minecraft");

	PluginManager pm;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {

		// if the command is sent from the console, reject it
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("You must be a player to do that!");
			return true;
		}

		// if the player confirms a trade and they're trading, notify his/her
		// trade instance
		if (cmd.getName().equals("trade")
				&& args[0].equalsIgnoreCase("confirm")) {
			if (traders.get(sender) == null) {
				sender.sendMessage("Not trading!");
				return true;
			}

			traders.get(sender).confirm((Player) sender);
			return true;
		}

		if (cmd.getName().equals("trade") && args[0].equalsIgnoreCase("accept")) {
			if (traders.get(sender) == null) {
				sender.sendMessage("Not trading!");
				return true;
			}

			traders.get(sender).acceptTrade((Player) sender);
			return true;
		}

		// if the player rejects a trade and they're trading, notify his/her
		// trade instance
		if (cmd.getName().equals("trade") && args[0].equalsIgnoreCase("reject")) {
			if (traders.get(sender) == null) {
				sender.sendMessage("Not trading!");
				return true;
			}

			traders.get(sender).cancelTrade();
			return true;
		}

		if (cmd.getName().equals("trade") && args[0].equalsIgnoreCase("deny")) {
			if (traders.get(sender) == null) {
				sender.sendMessage("Not trading!");
				return true;
			}

			traders.get(sender).cancelTrade();
			return true;
		}

		if (cmd.getName().equals("trade"))
			return startTrade(sender, cmd, args);

		return super.onCommand(sender, cmd, commandLabel, args);
	}

	@Override
	public void onDisable() {

		if (!traders.isEmpty()) {
			log.info("SpoutTrade detected that players were still trading. Attempting to cancel trades...");
			Player[] players = getServer().getOnlinePlayers();
			for (int i = 0; i < players.length; i++) {
				if (traders.get(players[i]) != null) {
					traders.get(players[i]).cancelTrade();
				}
			}
			log.info("Trades cancelled");
		}

		log.info("SpoutTrade V" + getDescription().getVersion() + " Disabled");

	}

	@Override
	public void onEnable() {
		final InvListener inventoryListener = new InvListener(this);

		pm = getServer().getPluginManager();

		pm.registerEvent(Type.CUSTOM_EVENT, inventoryListener, Priority.Normal,
				this);

		log.info("SpoutTrade V" + getDescription().getVersion() + " Enabled");
	}

	private boolean startTrade(CommandSender sender, Command cmd, String[] args) {
		// if no player is specified, stop before a NPE shows up
		if (args.length == 0) {
			sender.sendMessage("You must specify a player to trade with");
			return true;
		}

		// make players final so they are correctly fed into the scheduler
		final Player initiator = (Player) sender;
		final Player target = getServer().getPlayer(args[0]);

		// if the target player isn't online, stop the trade
		if (target == null) {
			initiator.sendMessage("Invalid player: " + args[0]
					+ ". Are they definitely online?");
			return true;
		}

		// if the player tries to trade with his or her self, prevent the trade
		if (target == initiator) {
			initiator.sendMessage("You can't trade with yourself!");
			return true;
		}

		// if either player is already trading, prevent the trade
		if (traders.containsKey(initiator) || traders.containsKey(target)) {
			initiator.sendMessage(target.getName() + " is already trading!");
			return true;
		}

		// log the start of the trade
		log.info(initiator.getName() + " requested to trade with "
				+ target.getName());

		// schedule the trade task with a short delay for dramatic effect
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {
					@Override
					public void run() {
						new Trade(initiator, target);
					}
				}, 15L);

		return true;

	}

}
