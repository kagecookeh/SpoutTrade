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

package net.ark3l.SpoutTrade.Trade;

import net.ark3l.SpoutTrade.Config.LanguageManager;
import net.ark3l.SpoutTrade.GUI.ConfirmPopup;
import net.ark3l.SpoutTrade.GUI.RequestPopup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.ArrayList;
import java.util.List;

public class TradePlayer {

	private SpoutPlayer player;

	private List<ItemStack> backup = new ArrayList();
	private TradeState state = TradeState.CHEST_OPEN;
	private ConfirmPopup popup;
	private RequestPopup requestPopup;

	public TradePlayer(SpoutPlayer player) {
		this.player = player;
	}

	public void backup() {
				// Simply retrieving the contents and storing that in an array seems to cause a dupe glitch
			for(ItemStack i : player.getInventory().getContents()) {

			 backup.add(i);

		}
	}

	/**
	 * Send a message, either through SpoutCraft or through chat
	 *
	 * @param msg the message to be sent
	 */
	public void sendMessage(String msg) {

		if(player.isSpoutCraftEnabled() && msg.length() < 26) {
			player.sendNotification("Trade", msg, Material.SIGN);
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

	public void requestConfirm(ItemStack[] lowerContents, ItemStack[] upperContents) {

		//        if(player.isSpoutCraftEnabled()) {
		//           popup = new ConfirmPopup(this.player, toItemList(lowerContents), toItemList(upperContents));
		//        }else {

		player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.SURE) + " " + ChatColor.RED + toItemList(upperContents) + ChatColor.WHITE + " |-| " + ChatColor.RED + toItemList(lowerContents));
		player.sendMessage(ChatColor.RED + "/trade accept " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.TOACCEPT));
		player.sendMessage(ChatColor.RED + "/trade decline " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.TODECLINE));

		//        }
	}

	private String toItemList(ItemStack[] items) {
  StringBuffer buf = new StringBuffer();

		for(ItemStack item : items) {
			if(item != null) {
				buf.append(item.getType()).append("x").append(item.getAmount()).append(", ");
			}
		}

		return buf.toString();
	}

	/**
	 * Restore the players inventory to the state it was in when the TradePlayer was instantiated
	 */
	public void restore() {
		player.getInventory().clear();

		for(ItemStack i : backup) {
            if(i != null) {
			player.getInventory().addItem(i);
            }
		}
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


	public void doTrade(ItemStack[] items) {
		Inventory inv = player.getInventory();
		for(ItemStack item : items) {
			if(item != null) {
				inv.addItem(item);
			}
		}
	}

		public void request(TradePlayer otherPlayer) {
		if(this.player.isSpoutCraftEnabled()) {
			requestPopup = new RequestPopup(player, ChatColor.RED + otherPlayer.getName() + " " + ChatColor.WHITE + LanguageManager.getString(LanguageManager.Strings.REQUESTED));
		}

		getPlayer().sendMessage(ChatColor.RED + otherPlayer.getName() + " " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.REQUESTED));
		getPlayer().sendMessage(ChatColor.RED + "/trade accept " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.TOACCEPT));
		player.sendMessage(ChatColor.RED + "/trade decline " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.TODECLINE));

	}

	/**
	 * @return the SpoutPlayer
	 */
	public SpoutPlayer getPlayer() {
		return player;
	}

	/**
	 * Closes the currently open request dialogue
	 */
	public void close() {
		if(requestPopup != null && requestPopup.isVisible()) {
			requestPopup.close();
		}
	}

	/**
	 * @param button the button to check
	 * @return whether the button is the accept button
	 */
	public boolean isAcceptButton(Button button) {
		return requestPopup.isAccept(button);
	}

	/**
	 * @param button the button to check
	 * @return whether the button is the decline button
	 */
	public boolean isDeclineButton(Button button) {
		return requestPopup.isDecline(button);
	}

}
