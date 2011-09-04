package net.ark3l.SpoutTrade.Listeners;

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
import java.util.HashMap;

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Trade.TradeManager;

import org.bukkit.event.Event.Result;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventorySlotType;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutTradeInventoryListener extends InventoryListener {

    private SpoutTrade plugin;

    public SpoutTradeInventoryListener(SpoutTrade instance) {
        plugin = instance;
    }

    /**
     * Handles an inventory click event
     * @param event
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        HashMap<SpoutPlayer, TradeManager> trades = plugin.trades;
        

        SpoutPlayer player = (SpoutPlayer) event.getPlayer();

        // do nothing if the player isn't trading
        if (!plugin.isBusy(player)) {
            return;
        }

        if (!event.isLeftClick()) {
            event.setCancelled(true);
            return;
        }

        // cancel to prevent item loss during trade
        if (event.getSlotType() == InventorySlotType.OUTSIDE) {
            event.setCancelled(true);
            return;
        }
        

        // get the trade instance associated with the player
        TradeManager trade = trades.get(player);

        Inventory inventory = event.getInventory();
        ItemStack item = event.getItem();

        // stop any NPEs
        if (item == null) {
            return;
        }
        
        event.setResult(Result.ALLOW);
        event.setCursor(null);

        if (!trade.onClickEvent(player, item, event.getSlot(), inventory)) {
            event.setCancelled(true);
        }

    }

    /**
     * Handles an inventory close event
     * @param event
     */
    @Override
    public void onInventoryClose(InventoryCloseEvent event) {

        SpoutPlayer player = (SpoutPlayer) event.getPlayer();

        // do nothing if the player is't trading
        if (!plugin.isBusy(player)) {
            return;
        }

        // retrieve the trade instance and notify of an inventory close
        plugin.trades.get(player).onClose(player);
    }
}
