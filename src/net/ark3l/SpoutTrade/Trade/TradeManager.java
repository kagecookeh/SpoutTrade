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
import net.ark3l.SpoutTrade.Inventory.TradeInventory;
import net.ark3l.SpoutTrade.SpoutTrade;
import net.minecraft.server.Packet101CloseWindow;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver Brown
 */
public class TradeManager {
	// This is where the magic happens

	private final TradePlayer initiator;
	private final TradePlayer target;
	private final SpoutTrade st = SpoutTrade.getInstance();
	private LanguageManager lang = st.getLang();
	private final TradeInventory inventory;
	private final String chestID = Integer.toString(this.hashCode());

	public TradeManager(SpoutPlayer initiator, SpoutPlayer target) {
		st.trades.put(initiator, this);
		st.trades.put(target, this);

		inventory = new TradeInventory(chestID);

		this.initiator = new TradePlayer(initiator);
		this.target = new TradePlayer(target);

		Inventory inv;
		inv = new CraftInventory(inventory);

		initiator.openInventoryWindow(inv);
		target.openInventoryWindow(inv);
	}


	public void onButtonClick(Button button, Player player) {
	}

	public void onClose(SpoutPlayer player) {


		if(target.getState() == TradeState.CHEST_OPEN || initiator.getState() == TradeState.CHEST_OPEN) {
			if(player.equals(initiator.player)) {
				CraftPlayer cPlayer = (CraftPlayer) target.player;
				cPlayer.getHandle().netServerHandler.sendPacket(new Packet101CloseWindow());
			} else {
				CraftPlayer cPlayer = (CraftPlayer) initiator.player;
				cPlayer.getHandle().netServerHandler.sendPacket(new Packet101CloseWindow());
			}

			target.setState(TradeState.CHEST_CLOSED);
			initiator.setState(TradeState.CHEST_CLOSED);

			if(getUsedCases(inventory.getUpperContents()) > getEmptyCases(target.getInventory().getContents()) || getUsedCases(inventory.getLowerContents()) > getEmptyCases(initiator.getInventory().getContents())) {
				abort();
				sendMessage(ChatColor.RED + lang.getString(LanguageManager.Strings.NOROOM));
				return;
			}

			initiator.requestConfirm(inventory.getLowerContents(), inventory.getUpperContents());
			target.requestConfirm(inventory.getLowerContents(), inventory.getUpperContents());
		}

	}

	public void abort() {
		st.trades.remove(initiator.player);
		st.trades.remove(target.player);

		target.restore();
		initiator.restore();

		sendMessage(lang.getString(LanguageManager.Strings.CANCELLED));
	}

	public void confirm(SpoutPlayer player) {

		if(player.equals(initiator.player)) {
			initiator.setState(TradeState.CONFIRMED);
			initiator.sendMessage(lang.getString(LanguageManager.Strings.CONFIRMED));
		} else {
			target.setState(TradeState.CONFIRMED);
			target.sendMessage(lang.getString(LanguageManager.Strings.CONFIRMED));
		}

		if(target.getState().equals(TradeState.CONFIRMED) && initiator.getState().equals(TradeState.CONFIRMED)) {
			doTrade();
		}

	}

	public void reject() {
		abort();
	}

	public Result onClickEvent(SpoutPlayer player, ItemStack item, int slot, Inventory inv) {
		if(target.getState() != TradeState.CHEST_OPEN || initiator.getState() != TradeState.CHEST_OPEN) {
			return Result.DENY;
		}

		if("Inventory".equals(inv.getName())) {
			return Result.ALLOW;
		} else if(inv.getName().equals(chestID)) {
			if(player.equals(initiator.player) && slot < 27) {
				return Result.ALLOW;
			} else if(player.equals(target.player) && slot >= 27) {
				return Result.ALLOW;

			} else {
				player.sendMessage(ChatColor.RED + lang.getString(LanguageManager.Strings.NOTYOURS));
			}
		}

		return Result.DENY;
	}

	private void doTrade() {

		initiator.doTrade(inventory.getLowerContents());
		target.doTrade(inventory.getUpperContents());

		st.trades.remove(target.player);
		st.trades.remove(initiator.player);

		sendMessage(lang.getString(LanguageManager.Strings.FINISHED));

	}

	private int getEmptyCases(ItemStack[] contents) {
		int count = 0;
		for(ItemStack content : contents) {
			if(content == null) {
				count++;
			}
		}
		return count;
	}

	private int getUsedCases(ItemStack[] contents) {
		int count = 0;
		for(ItemStack content : contents) {
			if(content != null) {
				count++;
			}
		}
		return count;
	}

	void sendMessage(String msg) {
		target.sendMessage(msg);
		initiator.sendMessage(msg);
	}
}