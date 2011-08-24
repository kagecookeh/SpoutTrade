package net.ark3l.SpoutTrade.Trade;

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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TradePlayer {

	protected Player player;

	public TradePlayer(Player player) {
		this.player = player;
	}

	public void sendMessage(String msg) {
		SpoutPlayer sPlayer = (SpoutPlayer) player;
		if (sPlayer.isSpoutCraftEnabled() && msg.length() < 26) {
			sPlayer.sendNotification("Trade", msg, Material.SIGN);
		} else
			player.sendMessage(msg);
	}

	/**
	 * @return
	 */
	public String getName() {
		return player.getName();
	}

	/**
	 * @return
	 */
	public Inventory getInventory() {
		return player.getInventory();
	}

	/**
	 * @param forInitiator
	 * @param forTarget
	 */
	public void requestConfirm(ItemStack[] chest, ItemStack[] chest2) {

		player.sendMessage(ChatColor.GREEN + "Are you sure you want to trade "
				+ ChatColor.RED + toItemList(chest) + ChatColor.GREEN + "for "
				+ ChatColor.RED + toItemList(chest2));
		player.sendMessage(ChatColor.GREEN + "Type " + ChatColor.RED
				+ "/trade confirm" + ChatColor.GREEN + " to confirm or "
				+ ChatColor.RED + "/trade reject" + ChatColor.GREEN
				+ " to reject");

	}

	private String toItemList(ItemStack[] stackList) {
		String list = "";

		for (int i = 0; i < stackList.length; i++) {
			ItemStack item = stackList[i];
			if (item != null)
				list += item.getType() + "x" + item.getAmount() + ", ";
		}

		return list;
	}

}
