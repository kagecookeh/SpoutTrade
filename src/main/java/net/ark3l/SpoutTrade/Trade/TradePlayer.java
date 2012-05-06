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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TradePlayer {

    private Player player;

    private TradeState state = TradeState.CHEST_OPEN;

    public TradePlayer(Player player) {
        this.player = player;
    }

    /**
     * Send a message, either through SpoutCraft or through chat
     *
     * @param msg the message to be sent
     */
    public void sendMessage(String msg) {
        player.sendMessage(msg);
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
        player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.SURE) + " " + ChatColor.RED + toItemList(upperContents) + ChatColor.WHITE + " |-| " + ChatColor.RED + toItemList(lowerContents));
        player.sendMessage(ChatColor.RED + "/trade accept " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.TOACCEPT));
        player.sendMessage(ChatColor.RED + "/trade decline " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.TODECLINE));

    }

    private String toItemList(ItemStack[] items) {
        StringBuffer buf = new StringBuffer();

        for (ItemStack item : items) {
            if (item != null) {
                buf.append(item.getType()).append("x").append(item.getAmount()).append(", ");
            }
        }

        return buf.toString();
    }

    /**
     * Restore the players inventory by looping through items they put in the chest
     *
     * @param contents
     */
    public void restore(ItemStack[] contents) {
        Inventory inventory = player.getInventory();

        for (ItemStack item : contents) {
            if (item != null) {
                HashMap<Integer, ItemStack> leftover = inventory.addItem(item);

                if (leftover != null) {
                    for (int i = 0; i < leftover.size(); i++) {
                        player.getWorld().dropItemNaturally(player.getLocation(), leftover.get(i));
                    }
                }
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
        for (ItemStack item : items) {
            if (item != null) {
                inv.addItem(item);
            }
        }
    }

    public void request(TradePlayer otherPlayer) {
        player.sendMessage(ChatColor.RED + otherPlayer.getName() + " " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.REQUESTED));
        player.sendMessage(ChatColor.RED + "/trade accept " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.TOACCEPT));
        player.sendMessage(ChatColor.RED + "/trade decline " + ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.TODECLINE));

    }

    /**
     * @return the SpoutPlayer
     */
    public Player getPlayer() {
        return player;
    }

}
