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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpoutTradeTrade {

	private Player initiator;
	private Player target;
	public VirtualChest chest;
	HashMap<Player, SpoutTradeTrade> traders;

	private boolean isTargetFinished;
	private boolean isInitiatorFinished;

	private List<ItemStack> targetItems = new ArrayList<ItemStack>();
	private List<ItemStack> initiatorItems = new ArrayList<ItemStack>();

	private boolean hasWarned = false;
	
	private boolean isInitiatorConfirmed = false;
	private boolean isTargetConfirmed = false;
	
	private boolean isTargetConfirming = false;
	private boolean isInitiatorConfirming = false;
	
	public SpoutTradeTrade(Player initiator, Player target) {
		this.initiator = initiator;
		this.target = target;
		
		chest = new VirtualChest(initiator.getName() + "-" + target.getName());
		traders = SpoutTrade.getTraders();

		traders.put(initiator, this);
		traders.put(target, this);

		chest.openChest(initiator);
		chest.openChest(target);

		for (int slot = 9; slot <= 17; slot++) {
			chest.setItem(slot, new ItemStack(7));
		}
	}

	public boolean onClick(Player player, Inventory inventory, ItemStack item) {

		boolean placed = false;
		if(initiatorItems.contains(item) || targetItems.contains(item))
			return false;
		

		if (player == initiator) {
			// 18-26
			for (int i = 18; i <= 26; i++) {
				if (chest.getItemStack(i) == null) {
					initiatorItems.add(item);
					chest.setItem(i, item);
					placed = true;
					break;
				}
			}

		} else {
			// 0-8
			for (int i = 0; i <= 8; i++) {
				if (chest.getItemStack(i) == null) {
					targetItems.add(item);
					chest.setItem(i, item);
					placed = true;
					break;
				}
			}
		}

		if (!placed)
			return false;

		inventory.remove(item);

		return true;
	}

	public void onClose(Player player) {

		if (player == target && !isTargetFinished) 
			isTargetFinished = true;
		else if(player == initiator && !isInitiatorFinished)
			isInitiatorFinished = true;
			
			if (isTargetFinished && isInitiatorFinished) {
				if(!isTargetConfirming && !isInitiatorConfirming)
				{
				target.sendMessage(ChatColor.GREEN + "Are you sure you want to trade?");
				target.sendMessage(ChatColor.GREEN + "Type " + ChatColor.RED + "/trade confirm" + ChatColor.GREEN + " or " + ChatColor.RED + "/trade reject" + ChatColor.GREEN + " to reject");
				initiator.sendMessage(ChatColor.GREEN + "Are you sure you want to trade?");
				initiator.sendMessage(ChatColor.GREEN + "Type " + ChatColor.RED + "/trade confirm" + ChatColor.GREEN + " or " + ChatColor.RED + "/trade reject" + ChatColor.GREEN + " to reject");
				
				isTargetConfirming = true;
				isInitiatorConfirming = true;
				return;
				}
				
				if(!isTargetConfirmed || !isInitiatorConfirmed)
					return;
				
				if(doTrade())
				{
				target.sendMessage(ChatColor.GREEN + "Trade Finished!");
				initiator.sendMessage(ChatColor.GREEN + "Trade Finished!");
				}
				else
				{
					target.sendMessage(ChatColor.RED + "Trade failed");
					initiator.sendMessage(ChatColor.RED + "Trade failed");
				}
				traders.remove(target);
				traders.remove(initiator);
			}
			else if(!hasWarned)
			{
				target.sendMessage(ChatColor.YELLOW + "Waiting for players to finish trading...");
				initiator.sendMessage(ChatColor.YELLOW + "Waiting for players to finish trading...");
				hasWarned = true;
			}
			
		}
	

	private boolean doTrade() {
		
		if(getRoomRemaining(target.getInventory()) - initiatorItems.size() < 0 || getRoomRemaining(initiator.getInventory()) - targetItems.size() < 0)
		{
			target.sendMessage(ChatColor.RED + "Not enough room in players inventory(s). Returning items.");
			initiator.sendMessage(ChatColor.RED + "Not enough room in players inventory(s). Returning items.");
			returnItems();
			traders.remove(initiator);
			traders.remove(target);
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

		return true;
	}

	private void returnItems() {
		for (int i = 0; i < initiatorItems.size(); i++) {
			ItemStack item = initiatorItems.get(i);
			initiator.getInventory().addItem(item);
		}

		for (int i = 0; i < targetItems.size(); i++) {
			ItemStack item = targetItems.get(i);
			target.getInventory().addItem(item);
		}

		
	}

	public void onReverseClick(Player player, Inventory inventory,
			ItemStack item) {
		if (player == target) {
			targetItems.remove(item);
		} else {
			initiatorItems.remove(item);
		}

	}
	
	public int getRoomRemaining(Inventory inv)
	{
		ItemStack[] items = inv.getContents();
		int count = 0;
		
		for(int i = 0;i<items.length;i++)
		{
			if(inv.getItem(i) != null);
			count ++;
		}
		return count;
	}

	public void confirm(Player sender) {
		if(sender == target)
		{
			isTargetConfirmed = true;
		target.sendMessage("Trade confirmed");
		}
		else if(sender == initiator)
		{
			initiator.sendMessage("Trade confirmed");
			isInitiatorConfirmed = true;
		}
		onClose(sender);
	}

	public void reject(Player sender) {
		target.sendMessage(ChatColor.RED + "A player rejected the trade. Returning items.");
		initiator.sendMessage(ChatColor.RED + "A player rejected the trade. Returning items.");
		returnItems();
		traders.remove(initiator);
		traders.remove(target);
	}

}
