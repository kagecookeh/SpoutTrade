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

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Config.ConfigManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TradePlayer {

	protected Player player;
	private ItemStack[] backup;
	private TradeState state = TradeState.CHEST_OPEN;

	public TradePlayer(Player player) {
		this.player = player;
		this.backup = player.getInventory().getContents();
	}

        /**
         * Send a message, either through SpoutCraft or through chat
         * @param msg the message to be sent
         */
        public void sendMessage(String msg) {
		SpoutPlayer sPlayer = (SpoutPlayer) player;
		if (sPlayer.isSpoutCraftEnabled() && msg.length() < 26) {
			sPlayer.sendNotification("Trade", msg, Material.SIGN);
		} else {
            player.sendMessage(msg);
        }
	}

	/**
	 * @return the players name
	 */
	public String getName() {
		return player.getName();
	}

	/**
	 * @return the players inventory
	 */
	public Inventory getInventory() {
		return player.getInventory();
	}

	/**
	 * @param forInitiator items for the initiator
	 * @param forTarget items for the target
	 */
	public void requestConfirm(ItemStack[] forInitiator, ItemStack[] forTarget) {
		ConfigManager config = SpoutTrade.getInstance().getConfig();

		player.sendMessage(ChatColor.GREEN + config.getString(12)
				+ ChatColor.RED + toItemList(forInitiator) + ChatColor.GREEN + config.getString(13)
				+ ChatColor.RED + toItemList(forTarget));
		player.sendMessage(ChatColor.GREEN + config.getString(14));

	}

	private String toItemList(ItemStack[] stackList) {
		String list = "";

		for (int i = 0; i < stackList.length; i++) {
			ItemStack item = stackList[i];
			if (item != null) {
                list += item.getType() + "x" + item.getAmount() + ", ";
            }
		}

		return list;
	}

        /**
         * Restore the players inventory to the state it was in when the TradePlayer was instantiated
         */
        public void restore() {
		player.getInventory().setContents(backup);
	}

        
        /**
         * @return the players TradeState
         */
        public TradeState getState() {
		return state;
	}

        /**
         * @param state the state to set the player's TradeState to
         */
        public void setState(TradeState state) {
		this.state = state;
	}

}
