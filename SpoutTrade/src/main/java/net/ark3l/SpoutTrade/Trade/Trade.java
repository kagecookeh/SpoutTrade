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

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Chests.TradeChest;
import net.ark3l.SpoutTrade.Config.ConfigManager;
import net.ark3l.SpoutTrade.Spout.SpoutTradePlayer;
import net.ark3l.SpoutTrade.Util.Log;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver
 * 
 */
public class Trade {
    // This is where the magic happens

    private TradeChest chest;
    private SpoutTradePlayer initiator;
    private SpoutTradePlayer target;
    private int itemCount;
    private String chestID = Integer.toString(this.hashCode());
    private SpoutTrade st = SpoutTrade.getInstance();
    private ConfigManager config = st.getConfig();

    public Trade(SpoutPlayer initiator, SpoutPlayer target) {

        st.trades.put(initiator, this);
        st.trades.put(target, this);

        this.initiator = new SpoutTradePlayer(initiator);
        this.target = new SpoutTradePlayer(target);

        itemCount += numberOfItems(initiator.getInventory().getContents());
        itemCount += numberOfItems(target.getInventory().getContents());

        this.chest = new TradeChest(chestID, initiator, target);

    }

    private int numberOfItems(ItemStack[] contents) {
        int count = 0;
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                if (contents[i].getAmount() == 0) {
                    count++;
                } else {
                    count += contents[i].getAmount();
                }
            }
        }
        return count;
    }

    public void onButtonClick(Button button, Player player) {
    }

    /**
     * Handles an inventory click
     * @param player the player who caused the event to fire
     * @param item the item they clicked on
     * @param slot the slot they clicked on
     * @param inventory the inventory they clicked in
     * @return whether the event should be canceled
     */
    public boolean onClickEvent(Player player, ItemStack item, int slot,
            Inventory inventory) {

        if (target.getState() != TradeState.CHEST_OPEN
                || initiator.getState() != TradeState.CHEST_OPEN) {
            // you can't do that now!
            player.sendMessage(ChatColor.RED + config.getString(3));
            return false;
        }

        if (inventory.getName().equals(chestID)) {
            if (player == initiator.player && slot <= 26) {
                return chest.removeItem(player, item);
            } else if (player == target.player && slot >= 27) {
                return chest.removeItem(player, item);
            } else {
                // not your item!
                player.sendMessage(ChatColor.RED + config.getString(4));
            }
        } else if ("Inventory".equals(inventory.getName())) {
            return chest.addItem(player, item);
        } else {
            // invalid inventory
            player.sendMessage(ChatColor.RED + config.getString(5));
        }

        return false;
    }

    /**
     * Handles an inventory closure
     * @param player the player who closed the inventory
     */
    public void onClose(Player player) {
        if (player == initiator.player) {
            initiator.setState(TradeState.CHEST_CLOSED);
        } else {
            target.setState(TradeState.CHEST_CLOSED);
        }
        if (initiator.getState() == TradeState.CHEST_CLOSED
                && target.getState() == TradeState.CHEST_CLOSED) {
            target.requestConfirm(chest.subChest.getContents(),
                    chest.subChest2.getContents());
            initiator.requestConfirm(chest.subChest2.getContents(),
                    chest.subChest.getContents());

            initiator.setState(TradeState.CONFIRMING);
            target.setState(TradeState.CONFIRMING);
        }

    }

    /**
     * Abort the trade. Returning the players original items and notifying them
     */
    public void abort() {
        st.trades.remove(initiator.getSpoutPlayer());
        st.trades.remove(target.getSpoutPlayer());

        // Trade cancelled
        sendMessage(ChatColor.RED + config.getString(6));

        initiator.restore();
        target.restore();
    }

    /**
     * Handle a confirm command. If both players have confirmed, do the trade
     * @param player the player who sent the command
     */
    public void confirm(Player player) {

        // confirming trade
        player.sendMessage(ChatColor.GREEN + config.getString(7));

        if (player == initiator.player
                && initiator.getState() == TradeState.CONFIRMING) {
            initiator.setState(TradeState.CONFIRMED);
        } else if (player == target.player
                && target.getState() == TradeState.CONFIRMING) {
            target.setState(TradeState.CONFIRMED);
        }
        if (initiator.getState() == TradeState.CONFIRMED
                && target.getState() == TradeState.CONFIRMED) {
            doTrade();
        }

    }

    private void doTrade() {

        ItemStack[] subChestContents = chest.subChest.getContents();
        ItemStack[] subChest2Contents = chest.subChest2.getContents();

        if (numberOfItems(subChestContents) + numberOfItems(subChest2Contents)
                + numberOfItems(initiator.getInventory().getContents())
                + numberOfItems(target.getInventory().getContents()) != itemCount) {
            // item duplication or loss detected, aborting trade
            sendMessage(ChatColor.RED + config.getString(8));
            abort();
            return;
        }

        if (getRoomRemaining(target.getInventory()) - subChestContents.length < 0
                || getRoomRemaining(initiator.getInventory())
                - subChest2Contents.length < 0) {
            sendMessage(ChatColor.RED
                    // trade could not be completed, not enough room
                    + config.getString(9));

            abort();
        }

        for (int i = 0; i < subChestContents.length; i++) {
            ItemStack item = subChestContents[i];
            if (item != null) {
                target.getInventory().addItem(item);
            }
        }

        for (int x = 0; x < subChest2Contents.length; x++) {
            ItemStack item = subChest2Contents[x];
            if (item != null) {
                initiator.getInventory().addItem(item);
            }
        }
        // Trade finished!
        sendMessage(ChatColor.WHITE + config.getString(10));
        Log.info(target.getName() + " and " + initiator.getName()
                + " finished trading.");

        st.trades.remove(initiator.getSpoutPlayer());
        st.trades.remove(target.getSpoutPlayer());

    }

    public void reject() {
        sendMessage(ChatColor.RED + config.getString(11));
        abort();
    }

    private int getRoomRemaining(Inventory inv) {
        // TODO - make this a pull request to bukkit

        ItemStack[] items = inv.getContents();
        int count = 0;

        for (int i = 0; i < items.length; i++) {
            if (inv.getItem(i) != null) {
                count++;
            }
        }
        return count;
    }

    private void sendMessage(String msg) {
        initiator.sendMessage(msg);
        target.sendMessage(msg);
    }
}