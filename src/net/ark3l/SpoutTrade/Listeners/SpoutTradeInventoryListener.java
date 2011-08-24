package net.ark3l.SpoutTrade.Listeners;

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

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Trade.Trade;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;

public class SpoutTradeInventoryListener extends InventoryListener {

	public static SpoutTrade plugin;

	public SpoutTradeInventoryListener(SpoutTrade instance) {
		plugin = instance;
	}

	HashMap<Player, Trade> trades = plugin.trades;

	public void onInventoryClick(InventoryClickEvent event) {

		System.out.println(event.getInventory().getName());

		Player player = event.getPlayer();

		// do nothing if the player isn't trading
		if (!trades.containsKey(player))
			return;

		// get the trade instance associated with the player
		Trade trade = trades.get(player);

		Inventory inventory = event.getInventory();
		ItemStack item = event.getItem();

		// stop any NPEs
		if (item == null)
			return;

		if (!event.isLeftClick())
			event.setCancelled(true);

		if (!trade.onClickEvent(player, item, event.getSlot(), inventory))
			event.setCancelled(true);

	}

	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = event.getPlayer();

		// do nothing if the player is't trading
		if (!trades.containsKey(player))
			return;

		// retrieve the trade instance and notify of an inventory close
		trades.get(player).onClose(player);
	}

	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = event.getPlayer();

		// if they're not trading, do nothing
		if (!trades.containsKey(player))
			return;

	}
}
