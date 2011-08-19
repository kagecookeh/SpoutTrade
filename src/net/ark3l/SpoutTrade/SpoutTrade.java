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

	Logger log = Logger.getLogger("Minecraft");
	PluginManager pm;

	private static HashMap<Player, SpoutTradeTrade> traders = new HashMap<Player, SpoutTradeTrade>();

	@Override
	public void onDisable() {
		log.info("SpoutTrade V" + this.getDescription().getVersion()
				+ " Disabled");
	}

	@Override
	public void onEnable() {
		InvListener inventoryListener = new InvListener(this);
		pm = getServer().getPluginManager();
		pm.registerEvent(Type.CUSTOM_EVENT, inventoryListener, Priority.Normal,
				this);

		log.info("SpoutTrade V" + this.getDescription().getVersion()
				+ " Enabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {

		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("You must be a player to do that!");
			return true;
		}

		if (cmd.getName().equals("trade")
				&& args[0].equalsIgnoreCase("confirm")) {
			if (traders.get(sender) == null) {
				sender.sendMessage("Not trading!");
				return true;
			}

			traders.get(sender).confirm((Player) sender);
			return true;
		}

		if (cmd.getName().equals("trade") && args[0].equalsIgnoreCase("reject")) {
			if (traders.get(sender) == null) {
				sender.sendMessage("Not trading!");
				return true;
			}

			traders.get(sender).reject((Player) sender);
			return true;
		}

		if (cmd.getName().equals("trade")) {
			return trade(sender, cmd, args);
		}

		return super.onCommand(sender, cmd, commandLabel, args);
	}

	private boolean trade(CommandSender sender, Command cmd, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("You must specify a player to trade with");
			return true;
		}

		final Player initiator = (Player) sender;
		final Player target = getServer().getPlayer(args[0]);

		if (target == null) {
			initiator.sendMessage("Invalid player: " + args[0]
					+ ". Are they definitely online?");
			return true;
		}

		if (target == initiator) {
			initiator.sendMessage("You can't trade with yourself!");
			return true;
		}

		if (traders.containsKey(initiator) || traders.containsKey(target))
			sender.sendMessage("Already trading!");

		log.info(initiator.getName() + " is trading with " + args[0]);

		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {
					@Override
					public void run() {
						new SpoutTradeTrade(initiator, target);
					}
				}, 15L);

		return true;

	}

	public static HashMap<Player, SpoutTradeTrade> getTraders() {
		return traders;
	}

}
