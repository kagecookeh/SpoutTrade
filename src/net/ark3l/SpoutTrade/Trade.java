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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.ark3l.SpoutTrade.Chest.VirtualChest;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Trade {

	private Player target;

	private Player initiator;
	public VirtualChest chest;

	private List<ItemStack> initiatorItems = new ArrayList<ItemStack>();

	private List<ItemStack> targetItems = new ArrayList<ItemStack>();

	public enum TradeState {
		DEFAULT, REQUEST, GUIOPEN, CONFIRMING, CONFIRMED, ERROR, CANCELLED, CONFIRM
	}

	TradeState globalTradeState = TradeState.DEFAULT;
	TradeState targetState = TradeState.DEFAULT;
	TradeState initiatorState = TradeState.DEFAULT;

	Logger log = SpoutTrade.log;

	HashMap<Player, Trade> traders;

	private boolean canAddItems = true;

//	private ItemStack[] targetIGuard;
//	private ItemStack[] initiatorIGuard;

	public Trade(Player initiator, Player target) {
		this.initiator = initiator;
		this.target = target;

		traders = SpoutTrade.getTraders();

		traders.put(initiator, this);
		traders.put(target, this);

		initiator.sendMessage(ChatColor.GREEN + "Waiting for " + ChatColor.RED
				+ target.getName() + ChatColor.GREEN
				+ " to accept the trade...");

		target.sendMessage(ChatColor.RED + initiator.getName()
				+ ChatColor.GREEN + " has requested to trade with you.");
		target.sendMessage(ChatColor.GREEN + "Type " + ChatColor.RED
				+ "/trade accept" + ChatColor.GREEN + " to accept or "
				+ ChatColor.RED + "/trade deny" + ChatColor.GREEN + " to deny");

		globalTradeState = TradeState.REQUEST;
	}

	public void confirm(Player sender) {
		if (globalTradeState == TradeState.CONFIRMING) {
			if (sender == target) {
				targetState = TradeState.CONFIRMED;
			} else if (sender == initiator) {
				initiatorState = TradeState.CONFIRMED;
			}
			sender.sendMessage(ChatColor.GREEN + "Trade confirmed");

			if (sender == initiator && targetState == TradeState.CONFIRMING)
				sender.sendMessage(ChatColor.GREEN + "Waiting for "
						+ ChatColor.RED + target.getName() + ChatColor.GREEN
						+ " to confirm");
			else if (sender == target
					&& initiatorState == TradeState.CONFIRMING)
				sender.sendMessage(ChatColor.GREEN + "Waiting for "
						+ ChatColor.RED + initiator.getName() + ChatColor.GREEN
						+ " to confirm");

			updateTrade();
		} else
			sender.sendMessage("You can't do that now!");
	}

	public void acceptTrade(Player player) {
		if (player != target)
			return;

		if (globalTradeState == TradeState.REQUEST) {
			sendMessage(ChatColor.GREEN + "Trade started");
			log.info(target.getName() + " accepted " + initiator.getName()
					+ "'s request to trade.");

			targetState = TradeState.GUIOPEN;
			initiatorState = TradeState.GUIOPEN;

			chest = new VirtualChest(initiator.getName() + "-"
					+ target.getName());
			chest.setItem(13, new ItemStack(7));

			chest.openChest(initiator);
			chest.openChest(target);

			updateTrade();
		} else
			player.sendMessage("You can't do that now!");
	}

	public boolean onClick(Player player, ItemStack item, int slot,
			Inventory clickedInventory) {
		Inventory inventory = player.getInventory();

		if (!clickedInventory.equals(inventory))
			return false;
		
		if (!canAddItems)
			return false;

		boolean placed = false;

		// prevents a dupe exploit
		if (initiatorItems.contains(item) || targetItems.contains(item))
			return false;

		// in case of chests or other containers
		if (!player.getInventory().contains(item))
			return false;

		// prevents players getting bedrock
		if (item.getTypeId() == 7)
			return false;

		if (player == initiator) {
			// slots 14-26 (bottom)
			for (int i = 14; i <= 26; i++)
				if (chest.getItemStack(i) == null) {
					initiatorItems.add(item);
					chest.setItem(i, item);
					placed = true;
					break;
				}

		} else
			// slots 0-12 (top)
			for (int i = 0; i <= 12; i++)
				if (chest.getItemStack(i) == null) {
					targetItems.add(item);
					chest.setItem(i, item);
					placed = true;
					break;
				}

		if (!placed) {
			player.sendMessage(ChatColor.RED
					+ "Not enough room left in the trade!");
			return false;
		}

		inventory.removeItem(item);

		return true;
	}

	public boolean onReverseClick(Player player, Inventory inventory,
			ItemStack item, int slot) {
		// if the item is bedrock, don't allow it to be returned, this prevents
		// players stealing the centre bedrock
		if (item.getTypeId() == 7)
			return false;
		
		if (!canAddItems)
			return false;

		if (player == target) {
			// prevent exploits
			if (inventory.equals(target.getInventory()))
				return false;

			if (slot >= 14)
				return false;

			// move the item back into the players inventory and remove it from
			// the trade list
			chest.removeItemStack(slot);
			target.getInventory().addItem(item);
			targetItems.remove(item);
		} else {
			// prevent exploits
			if (inventory.equals(initiator.getInventory()))
				return false;

			if (slot <= 12)
				return false;

			// move the item back into the players inventory and remove it from
			// the trade list
			chest.removeItemStack(slot);
			initiator.getInventory().addItem(item);
			initiatorItems.remove(item);
		}

		return true;
	}

	public void onClose(Player player) {

		// if the players GUI was open, update

		if (player == target && targetState.equals(TradeState.GUIOPEN))
		{
			targetState = TradeState.CONFIRM;
			initiator.sendMessage(ChatColor.RED + target.getName() + ChatColor.GREEN + " Stopped trading");
		}
		else if (player == initiator
				&& initiatorState.equals(TradeState.GUIOPEN))
		{
			initiatorState = TradeState.CONFIRM;
			target.sendMessage(ChatColor.RED + target.getName() + ChatColor.GREEN + " Stopped trading");
		}

		// inventoryGuard(player);
canAddItems  = false;
		updateTrade();
	}

	/*
	 * private void inventoryGuard(Player player) { if(player.equals(initiator))
	 * { initiatorIGuard = player.getInventory().getContents(); } else
	 * if(player.equals(target)) { targetIGuard =
	 * target.getInventory().getContents(); }
	 * 
	 * player.sendMessage(ChatColor.RED +
	 * "As an added security measure your inventory has been cleared until the trade is finished."
	 * ); player.getInventory().clear(); }
	 */

	public boolean canUseInventory(Player player) {
		if (player == initiator && initiatorState != TradeState.GUIOPEN) {
			return false;
		} else if (player == target && targetState != TradeState.GUIOPEN) {
			return false;
		}

		return true;
	}

	public void cancelTrade() {
		// Inform players that the trade is cancelled and return their items
		if (globalTradeState != TradeState.REQUEST) {
			sendMessage(ChatColor.RED
					+ "A player cancelled the trade. Returning items.");
			returnItems();
		} else
			sendMessage(ChatColor.RED + target.getName() + ChatColor.GREEN
					+ " rejected the trade");

		log.info("The trade between " + target.getName() + " and "
				+ initiator.getName() + " was cancelled.");
		traders.remove(initiator);
		traders.remove(target);
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
//		// return the guarded contents
//		if (targetIGuard.length != 0)
//			target.getInventory().setContents(targetIGuard);

		// read off the items in the list and return them
		for (int i = 0; i < initiatorItems.size(); i++) {
			ItemStack item = initiatorItems.get(i);
			initiator.getInventory().addItem(item);
		}

//		// return the guarded contents
//		if (initiatorIGuard.length != 0)
//			initiator.getInventory().setContents(initiatorIGuard);

		// read off the items in the list and return them
		for (int i = 0; i < targetItems.size(); i++) {
			ItemStack item = targetItems.get(i);
			target.getInventory().addItem(item);
		}

	}

	private void sendMessage(String message) {
		initiator.sendMessage(message);
		target.sendMessage(message);
	}

	private boolean doTrade() {

		// return guarded contents
//		if (targetIGuard.length != 0)
//			target.getInventory().setContents(targetIGuard);
//		if (initiatorIGuard.length != 0)
//			initiator.getInventory().setContents(initiatorIGuard);

		// if there isn't enough room, flag the trade with an error, otherwise,
		// go ahead and trade the items

		if (getRoomRemaining(target.getInventory()) - initiatorItems.size() < 0
				|| getRoomRemaining(initiator.getInventory())
						- targetItems.size() < 0) {
			sendMessage(ChatColor.RED
					+ "Not enough room in players inventory(s). Returning items.");

			globalTradeState = TradeState.ERROR;
			return false;
		}

		for (int i = 0; i < initiatorItems.size(); i++) {
			ItemStack item = initiatorItems.get(i);
			target.getInventory().addItem(item);
		}

		for (int i = 0; i < targetItems.size(); i++) {
			ItemStack item = targetItems.get(i);
			initiator.getInventory().addItem(item);
		}

		sendMessage(ChatColor.GOLD + "Trade Finished!");
		log.info(target.getName() + " and " + initiator.getName()
				+ " finished trading.");

		traders.remove(initiator);
		traders.remove(target);

		return true;
	}

	private void updateTrade() {

		switch (initiatorState) {
		case DEFAULT:
			break;
		case REQUEST:
			break;
		case GUIOPEN:
			break;
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
		case CANCELLED:
			cancelTrade();
			break;
		}

		switch (targetState) {
		case DEFAULT:
			break;
		case REQUEST:
			break;
		case GUIOPEN:
			break;
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
		case CANCELLED:
			cancelTrade();
			break;
		}

		switch (globalTradeState) {
		case DEFAULT:
			break;
		case REQUEST:
			break;
		case GUIOPEN:
			break;
		case CONFIRM:
//			target.sendMessage(getConfirmDialogue(target));
//			initiator.sendMessage(getConfirmDialogue(initiator));
			sendMessage(ChatColor.GREEN + "Are you sure you want to trade?");
			sendMessage(ChatColor.GREEN + "Type " + ChatColor.RED
					+ "/trade confirm" + ChatColor.GREEN + " to confirm or "
					+ ChatColor.RED + "/trade reject" + ChatColor.GREEN
					+ " to reject");
			globalTradeState = TradeState.CONFIRMING;
			targetState = TradeState.CONFIRMING;
			initiatorState = TradeState.CONFIRMING;
			break;
		case CONFIRMING:
			break;
		case CONFIRMED:
			doTrade();
			break;
		case ERROR:
			cancelTrade();
			break;
		case CANCELLED:
			cancelTrade();
			break;
		}

	}

/*	//TODO - NYI
 * private String getConfirmDialogue(Player player) {
		String initiatorList = null;
		String targetList = null;

		for (int i = 0; i < initiatorItems.size(); i++) {
			ItemStack item = initiatorItems.get(i);
			initiatorList += item.getType() + "x" + item.getAmount() + ", ";
		}

		for (int i = 0; i < targetItems.size(); i++) {
			ItemStack item = targetItems.get(i);
			initiatorList += item.getType() + "x" + item.getAmount() + ", ";
		}

		if (player.equals(initiator)) {
			return ChatColor.GREEN + "Are you sure you want to trade "
					+ ChatColor.RED + initiatorList + ChatColor.GREEN + "for "
					+ ChatColor.RED + targetList;
		} else if (player.equals(target)) {
			return ChatColor.GREEN + "Are you sure you want to trade "
					+ ChatColor.RED + targetList + ChatColor.GREEN + "for "
					+ ChatColor.RED + initiatorList;
		}
		return "Error retrieving trade dialogue";
	}*/
}
