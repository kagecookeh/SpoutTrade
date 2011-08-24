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
package net.ark3l.SpoutTrade.Trade;

import java.util.HashMap;

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Chests.TradeChest;
import net.ark3l.SpoutTrade.Spout.SpoutTradePlayer;
import net.ark3l.SpoutTrade.Util.LogLevel;
import net.ark3l.SpoutTrade.Util.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.Button;

/**
 * @author Oliver
 * 
 */
public class Trade {

	private SpoutTradePlayer target;
	private SpoutTradePlayer initiator;

	public TradeChest chest;

	private Util util = Util.getInstance();

	public enum TradeState {
		DEFAULT, GUIOPEN, CONFIRMING, CONFIRMED, ERROR, CANCELLED, CONFIRM
	}

	TradeState globalTradeState = TradeState.DEFAULT;
	TradeState targetState = TradeState.DEFAULT;
	TradeState initiatorState = TradeState.DEFAULT;

	HashMap<Player, Trade> traders;

	private ItemStack[] targetBackup;
	private ItemStack[] initiatorBackup;

	String chestIdentifier = "Trade " + this.hashCode();

	public Trade(Player initiator, Player target) {
		this.initiator = new SpoutTradePlayer(initiator);
		this.target = new SpoutTradePlayer(target);

		traders = SpoutTrade.trades;

		traders.put(initiator, this);
		traders.put(target, this);

		sendMessage(ChatColor.GREEN + "Trade started");
		util.log(LogLevel.INFO,
				target.getName() + " accepted " + initiator.getName()
						+ "'s request to trade.");

		this.targetBackup = target.getInventory().getContents();
		this.initiatorBackup = initiator.getInventory().getContents();

		// Change the trade state
		targetState = TradeState.GUIOPEN;
		initiatorState = TradeState.GUIOPEN;
		globalTradeState = TradeState.GUIOPEN;

		chest = new TradeChest(chestIdentifier, initiator, target);
		chest.openChest(initiator, target);

		updateTrade();

	}

	/**
	 * @param sender
	 *            - the player who sent the confirmation
	 */
	public void confirm(Player sender) {

		if (globalTradeState == TradeState.CONFIRMING) {
			if (sender == target.player) {
				targetState = TradeState.CONFIRMED;
			} else if (sender == initiator.player) {
				initiatorState = TradeState.CONFIRMED;
			}
			sender.sendMessage(ChatColor.GREEN + "Trade confirmed");

			if (sender == initiator.player
					&& targetState == TradeState.CONFIRMING)
				sender.sendMessage(ChatColor.GREEN + "Waiting for "
						+ ChatColor.RED + target.getName() + ChatColor.GREEN
						+ " to confirm");
			else if (sender == target.player
					&& initiatorState == TradeState.CONFIRMING)
				sender.sendMessage(ChatColor.GREEN + "Waiting for "
						+ ChatColor.RED + initiator.getName() + ChatColor.GREEN
						+ " to confirm");

			updateTrade();
		} else
			sender.sendMessage("You can't do that now!");
	}

	public void abort() {
		returnItems();

		util.log(LogLevel.INFO, "The trade between " + target.getName()
				+ " and " + initiator.getName() + " was cancelled.");
		traders.remove(initiator.player);
		traders.remove(target.player);
	}

	public void reject() {

		sendMessage(ChatColor.RED + "Trade cancelled");

		abort();

	}

	/**
	 * @param player
	 *            - the player who clicked
	 * @param item
	 *            - the item they clicked on
	 * @param slot
	 *            - the slot they clicked on
	 * @param clickedInventory
	 *            - the inventory they clicked on
	 * @return Whether or not to cancel the event
	 */
	public boolean onClickEvent(Player player, ItemStack item, int slot,
			Inventory clickedInventory) {

		if (clickedInventory.getName() == chestIdentifier)
			return removeFromTrade(player, item, slot);
		else if (clickedInventory.getName() == "Inventory")
			return addToTrade(player, item);
		else
			return false;

	}

	/**
	 * @param player
	 * @param item
	 */
	private boolean addToTrade(Player player, ItemStack item) {

		if (globalTradeState != TradeState.GUIOPEN) {
			player.sendMessage(ChatColor.RED
					+ "You can't add/remove items after the other player has finished!");
			return false;
		}

		chest.addItem(player, item);
		player.getInventory().removeItem(item);

		return true;
	}

	/**
	 * @param player
	 * @param item
	 */
	private boolean removeFromTrade(Player player, ItemStack item, int slot) {

		// prevents adding/removing items after one player has closed window
		// (would result in scams)
		if (globalTradeState != TradeState.GUIOPEN) {
			player.sendMessage(ChatColor.RED
					+ "You can't add/remove items after the other player has finished!");
			return false;
		}

		if (slot < 27) {
			if (player != initiator) {
				player.sendMessage(ChatColor.RED + "Not your item!");
				return false;
			}
			initiator.getInventory().addItem(item);
		} else {
			if (player != target) {
				player.sendMessage(ChatColor.RED + "Not your item!");
				return false;
			}

			target.getInventory().addItem(item);
		}
		chest.removeItem(player, item);

		return true;

	}

	public void onClose(Player player) {

		// if the players GUI was open, update

		if (player == target.player && targetState.equals(TradeState.GUIOPEN)) {
			targetState = TradeState.CONFIRM;
			initiator.sendMessage(ChatColor.RED + initiator.getName()
					+ ChatColor.GREEN + " Stopped trading");
			globalTradeState = TradeState.DEFAULT;
			updateTrade();
		} else if (player == initiator.player
				&& initiatorState.equals(TradeState.GUIOPEN)) {
			initiatorState = TradeState.CONFIRM;
			target.sendMessage(ChatColor.RED + target.getName()
					+ ChatColor.GREEN + " Stopped trading");
			globalTradeState = TradeState.DEFAULT;
			updateTrade();
		}

	}

	public boolean canUseInventory(Player player) {
		if (player == initiator && initiatorState != TradeState.GUIOPEN) {
			return false;
		} else if (player == target && targetState != TradeState.GUIOPEN) {
			return false;
		}

		return true;
	}

	public int getRoomRemaining(Inventory inv) {
		// TODO - make this a pull request to bukkit

		ItemStack[] items = inv.getContents();
		int count = 0;

		for (int i = 0; i < items.length; i++) {
			if (inv.getItem(i) != null)
				count++;
		}
		return count;
	}

	private void returnItems() {
		// set the inventories to the backups we created earlier
		target.getInventory().setContents(targetBackup);
		initiator.getInventory().setContents(initiatorBackup);
	}

	private void sendMessage(String message) {
		initiator.sendMessage(message);
		target.sendMessage(message);
	}

	private boolean doTrade() {

		// if there isn't enough room, flag the trade with an error, otherwise,
		// go ahead and trade the items

		ItemStack[] subChestContents = chest.subChest.getContents();
		ItemStack[] subChest2Contents = chest.subChest2.getContents();

		if (getRoomRemaining(target.getInventory()) - subChestContents.length < 0
				|| getRoomRemaining(initiator.getInventory())
						- subChest2Contents.length < 0) {
			sendMessage(ChatColor.RED
					+ "The trade could not be completed: Not enough room.");

			globalTradeState = TradeState.ERROR;
			return false;
		}

		for (int i = 0; i < subChestContents.length; i++) {
			ItemStack item = subChestContents[i];
			if (item != null)
				target.getInventory().addItem(item);
		}

		for (int x = 0; x < subChest2Contents.length; x++) {
			ItemStack item = subChest2Contents[x];
			if (item != null)
				initiator.getInventory().addItem(item);
		}

		sendMessage(ChatColor.GOLD + "Trade Finished!");
		util.log(LogLevel.INFO,
				target.getName() + " and " + initiator.getName()
						+ " finished trading.");

		traders.remove(initiator.player);
		traders.remove(target.player);

		return true;
	}

	private void updateTrade() {

		switch (initiatorState) {
		case CONFIRM:
			if (targetState.equals(TradeState.CONFIRM))
				globalTradeState = TradeState.CONFIRM;
			break;
		case CONFIRMING:
			if (targetState.equals(TradeState.CONFIRMING))
				globalTradeState = TradeState.CONFIRMING;
			break;
		case CONFIRMED:
			if (targetState.equals(TradeState.CONFIRMED))
				globalTradeState = TradeState.CONFIRMED;
			break;
		case ERROR:
			globalTradeState = TradeState.ERROR;
			break;
		}

		switch (targetState) {
		case CONFIRM:
			if (initiatorState.equals(TradeState.CONFIRM))
				globalTradeState = TradeState.CONFIRM;
		case CONFIRMING:
			if (initiatorState.equals(TradeState.CONFIRMING))
				globalTradeState = TradeState.CONFIRMING;
			break;
		case CONFIRMED:
			if (initiatorState.equals(TradeState.CONFIRMED))
				globalTradeState = TradeState.CONFIRMED;
			break;
		case ERROR:
			globalTradeState = TradeState.ERROR;
			break;
		}

		switch (globalTradeState) {
		case CONFIRM:
			target.requestConfirm(chest.subChest.getContents(),
					chest.subChest2.getContents());
			initiator.requestConfirm(chest.subChest2.getContents(),
					chest.subChest.getContents());
			globalTradeState = TradeState.CONFIRMING;
			targetState = TradeState.CONFIRMING;
			initiatorState = TradeState.CONFIRMING;
			break;
		case CONFIRMED:
			doTrade();
			break;
		}

	}

	/**
	 * @param button
	 */
	public void onButtonClick(Button button, Player player) {
		// TODO Auto-generated method stub

	}

}
