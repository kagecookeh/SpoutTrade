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

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;

public class InvListener extends InventoryListener {

	public static SpoutTrade plugin;

	public InvListener(SpoutTrade instance) {
		plugin = instance;
	}

	public void onInventoryClick(InventoryClickEvent event) {
		Player player = event.getPlayer();

		// do nothing if the player isn't trading
		if (!SpoutTrade.getTraders().containsKey(player))
			return;

		// get the trade instance associated with the player
		Trade trade = SpoutTrade.getTraders().get(player);

		Inventory inventory = event.getInventory();
		ItemStack item = event.getItem();

		// stops NPE caused by air
		if (item == null)
			return;

		// if the click is a shift click, notify the trade instance so
		if (event.isShiftClick()) {
			if (!trade.onReverseClick(player, inventory, item, event.getSlot()))
				event.setCancelled(true);
		}
		// otherwise tell the trade instance it's a normal click
		else {
			if (!trade.onClick(player, item, event.getSlot(), inventory))
				event.setCancelled(true);
		}

	}

	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = event.getPlayer();

		// do nothing if the player is't trading
		if (!SpoutTrade.getTraders().containsKey(player))
			return;

		// retrieve the trade instance and notify of an inventory close
		SpoutTrade.getTraders().get(player).onClose(player);
	}

	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = event.getPlayer();

		// if they're not trading, do nothing
		if (!SpoutTrade.getTraders().containsKey(player))
			return;

	}

	/*
	 * public void onInventoryEvent(InventoryEvent event) { if
	 * (event.getPlayer() != null) { Player player = event.getPlayer();
	 * 
	 * if (!SpoutTrade.getTraders().get(player).canUseInventory(player)) {
	 * player.sendMessage(ChatColor.RED + "Warning: " + ChatColor.WHITE +
	 * "Using any inventory during a trade may result in item loss!");
	 * event.setCancelled(true); } } }
	 */
}
