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

package net.ark3l.SpoutTrade.Listeners;

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Trade.Trade;
import net.ark3l.SpoutTrade.Trade.TradeManager;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventorySlotType;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutTradeInventoryListener extends InventoryListener {

    private final SpoutTrade plugin;
    private TradeManager manager;

    public SpoutTradeInventoryListener(SpoutTrade instance) {
        plugin = instance;
        manager = instance.getTradeManager();
    }

    /**
     * Handles an inventory click event
     *
     * @param event the event
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Event.Result result = Event.Result.DEFAULT;

        SpoutPlayer player = (SpoutPlayer) event.getPlayer();

        // do nothing if the player isn't trading
        if (!plugin.getTradeManager().isTrading(player)) {
            return;
        }

        if (event.isShiftClick() || event.getSlotType() == InventorySlotType.OUTSIDE) {
            event.setResult(Event.Result.DENY);
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack item = event.getItem();

        // get the trade instance associated with the player
        Trade trade = manager.getTrade(player);

        Inventory inventory = event.getInventory();

        if (!inventory.getName().equalsIgnoreCase("inventory")) {
            result = trade.slotCheck(player, event.getSlot(), inventory);
        }

        if (trade.canUseInventory()) {
            result = Event.Result.DENY;
        }

        // prevent glitchiness
        if (item != null && item.getAmount() < 0) {
            result = Event.Result.DENY;
        } else if (cursor != null && cursor.getAmount() < 0) {
            result = Event.Result.DENY;
        }

        event.setResult(result);
    }

    /**
     * Handles an inventory close event
     *
     * @param event the event
     */
    @Override
    public void onInventoryClose(InventoryCloseEvent event) {

        SpoutPlayer player = (SpoutPlayer) event.getPlayer();

        // do nothing if the player isn't trading
        if (!manager.isTrading(player)) {
            return;
        }

        // retrieve the trade instance and notify of an inventory close
        manager.getTrade(player).onClose(player);
    }
}
