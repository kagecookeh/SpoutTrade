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

import net.ark3l.SpoutTrade.Config.LanguageManager;
import net.ark3l.SpoutTrade.GUI.ConfirmPopup;
import net.ark3l.SpoutTrade.SpoutTrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

class TradePlayer {

    final SpoutPlayer player;
    private final ItemStack[] backup;
    private TradeState state = TradeState.CHEST_OPEN;
    private ConfirmPopup popup;

    public TradePlayer(SpoutPlayer player) {
        this.player = player;
        this.backup = player.getInventory().getContents();
    }

    /**
     * Send a message, either through SpoutCraft or through chat
     *
     * @param msg the message to be sent
     */
    public void sendMessage(String msg) {

        if (player.isSpoutCraftEnabled() && msg.length() < 26) {
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
        LanguageManager lang = SpoutTrade.getInstance().getLang();

        player.sendMessage(ChatColor.GREEN + lang.getString(LanguageManager.Strings.SURE) + " "
                + ChatColor.RED + toItemList(upperContents) + ChatColor.WHITE + " |-| "
                + ChatColor.RED + toItemList(lowerContents));
        player.sendMessage(ChatColor.RED + "/trade accept " + ChatColor.GREEN + lang.getString(LanguageManager.Strings.TOACCEPT));
        player.sendMessage(ChatColor.RED + "/trade decline " + ChatColor.GREEN + lang.getString(LanguageManager.Strings.TODECLINE));

//        }
    }

    private String toItemList(ItemStack[] items) {
        String list = "";

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                list += items[i].getType() + "x" + items[i].getAmount() + ", ";
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


    public void doTrade(ItemStack[] items) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                inv.addItem(items[i]);
            }
        }
    }
}
