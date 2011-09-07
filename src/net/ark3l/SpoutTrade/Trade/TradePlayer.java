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

import net.ark3l.SpoutTrade.Config.ConfigManager;
import net.ark3l.SpoutTrade.GUI.ConfirmPopup;
import net.ark3l.SpoutTrade.SpoutTrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.List;

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

    public void requestConfirm(List<net.minecraft.server.ItemStack> lowerContents, List<net.minecraft.server.ItemStack> upperContents) {

//        if(player.isSpoutCraftEnabled()) {
//           popup = new ConfirmPopup(this.player, toItemList(lowerContents), toItemList(upperContents));
//        }else {
        ConfigManager config = SpoutTrade.getInstance().getConfig();

        player.sendMessage(ChatColor.GREEN + config.getString(12)
                + ChatColor.RED + toItemList(upperContents) + ChatColor.GREEN + config.getString(13)
                + ChatColor.RED + toItemList(lowerContents));
        player.sendMessage(ChatColor.GREEN + config.getString(14));
//        }
    }

    private String toItemList(List<net.minecraft.server.ItemStack> stackList) {
        String list = "";

        for (net.minecraft.server.ItemStack aStackList : stackList) {
            if (aStackList != null) {
                ItemStack item = new ItemStack(aStackList.id, aStackList.count);
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


    public void doTrade(List<net.minecraft.server.ItemStack> contents) {
        Inventory inv = player.getInventory();
        for (net.minecraft.server.ItemStack content : contents) {
            if (content != null) {
                inv.addItem(new ItemStack(content.id, content.count));
            }
        }
    }
}
