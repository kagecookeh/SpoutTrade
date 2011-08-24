package net.ark3l.SpoutTrade.Chests;

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

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.InventoryLargeChest;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author Oliver Brown
 */
public class TradeChest {

	public VirtualChest subChest;
	public VirtualChest subChest2;

	protected InventoryLargeChest lc;

	private String p1Name;
	private String p2Name;

	public TradeChest(String chestName, Player p1, Player p2) {
		subChest = new VirtualChest(chestName + " sub");
		subChest2 = new VirtualChest(chestName + " sub2");

		this.p1Name = p1.getName();
		this.p2Name = p2.getName();

		lc = new InventoryLargeChest(chestName, subChest.chest, subChest2.chest);

	}

	/**
	 * @param player - the player to add the item for
	 * @param item - the item to add
	 */
	public void addItem(Player player, ItemStack item) {
		if (player.getName() == p1Name) {
			subChest.addItem(item);
		} else {
			subChest2.addItem(item);
		}
	}

	/**
	 * @param player - the player to remove the item for
	 * @param item - the item to remove
	 */
	public void removeItem(Player player, org.bukkit.inventory.ItemStack item) {
		if (player.getName() == p1Name) {
			org.bukkit.inventory.ItemStack[] items = subChest.getContents();
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null && items[i].equals(item)) {
					subChest.removeItemStack(i);
				}
			}
		} else {
			org.bukkit.inventory.ItemStack[] items = subChest2.getContents();
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null && items[i].equals(item)) {
					subChest2.removeItemStack(i);
				}
			}
		}
	}

	public void openChest(Player p, Player p2) {
		EntityPlayer eh = ((CraftPlayer) p).getHandle();
		eh.a(lc);
		EntityPlayer eh2 = ((CraftPlayer) p2).getHandle();
		eh2.a(lc);
	}

}