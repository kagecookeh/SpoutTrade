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

import net.ark3l.SpoutTrade.Config.ConfigManager;
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
    private ConfigManager config = st.getConfig();
    private final TradeInventory inventory;
    private final int itemCount;
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

        itemCount = countItems();
    }


    public void onButtonClick(Button button, Player player) {
    }

    public void onClose(SpoutPlayer player) {


        if (target.getState() == TradeState.CHEST_OPEN || initiator.getState() == TradeState.CHEST_OPEN) {
            if (player.equals(initiator)) {
                CraftPlayer cPlayer = (CraftPlayer) target.player;
                cPlayer.getHandle().netServerHandler.sendPacket(new Packet101CloseWindow());
            } else {
                CraftPlayer cPlayer = (CraftPlayer) initiator.player;
                cPlayer.getHandle().netServerHandler.sendPacket(new Packet101CloseWindow());
            }

            target.setState(TradeState.CHEST_CLOSED);
            initiator.setState(TradeState.CHEST_CLOSED);

            initiator.requestConfirm(inventory.getLowerContents(), inventory.getUpperContents());
            target.requestConfirm(inventory.getLowerContents(), inventory.getUpperContents());
        }

    }

    public void abort() {
        st.trades.remove(initiator.player);
        st.trades.remove(target.player);

        target.restore();
        initiator.restore();

        sendMessage("Trade cancelled");
    }

    public void confirm(SpoutPlayer player) {

        if (player.equals(initiator.player)) {
            initiator.setState(TradeState.CONFIRMED);
            initiator.sendMessage("Confirmed");
        } else {
            target.setState(TradeState.CONFIRMED);
            target.sendMessage("Confirmed");
        }

        if (target.getState().equals(TradeState.CONFIRMED) && initiator.getState().equals(TradeState.CONFIRMED)) {
            doTrade();
        }

    }

    private int countItems() {
        int count = 0;

        ItemStack[] initContents = initiator.getInventory().getContents();
        ItemStack[] targetContents = target.getInventory().getContents();

        for (ItemStack initContent : initContents) {
            if (initContent != null) {
                if(initContent.getAmount() == 0) {
                count++;
                }
                else {
                count += initContent.getAmount();
                }
            }
        }

        for (ItemStack targetContent : targetContents) {
            if (targetContent != null) {
                if(targetContent.getAmount() == 0) {
                count++;
                }
                else {
                count += targetContent.getAmount();
                }
            }
        }

        return count;
    }

    public void reject() {
        abort();
    }

    public Result onClickEvent(SpoutPlayer player, ItemStack item, int slot, Inventory inv) {
        if (target.getState() != TradeState.CHEST_OPEN || initiator.getState() != TradeState.CHEST_OPEN) {
            return Result.DENY;
        }

        if ("Inventory".equals(inv.getName())) {
            return Result.ALLOW;
        } else if (inv.getName() == chestID) {
            if (player.equals(initiator.player) && slot < 27) {
                System.out.println(2);
                return Result.ALLOW;
            } else if (player.equals(target.player) && slot >= 27) {
                System.out.println(3);
                return Result.ALLOW;

            }
            else {
                player.sendMessage(ChatColor.RED + "Not your slot");
            }
        }

             return Result.DENY;
    }

    private void doTrade() {

        if (inventory.count() != itemCount) {
            abort();
            sendMessage(ChatColor.RED + "Item duplication or loss detected. Aborted trade.");
            return;
        }

        if (inventory.getUpperContents().size() > getRoomRemaining(target.getInventory().getContents()) || inventory.getLowerContents().size() > getRoomRemaining(initiator.getInventory().getContents())) {
            abort();
            sendMessage(ChatColor.RED + "Not enough room in a players inventory to complete the trade");
            return;
        }

        initiator.doTrade(inventory.getLowerContents());
        target.doTrade(inventory.getUpperContents());

        st.trades.remove(target.player);
        st.trades.remove(initiator.player);

        sendMessage("Trade finished");
    }

    private int getRoomRemaining(ItemStack[] contents) {
        int count = 0;
        for (ItemStack content : contents) {
            if (content == null) {
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