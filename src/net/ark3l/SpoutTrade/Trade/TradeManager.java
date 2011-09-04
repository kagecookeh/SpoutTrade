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

import net.ark3l.SpoutTrade.Inventory.TradeInventory;
import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Config.ConfigManager;

import net.minecraft.server.Packet101CloseWindow;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver Brown
 * 
 */
public class TradeManager {
    // This is where the magic happens

    private TradePlayer initiator;
    private TradePlayer target;
    private String chestID = Integer.toString(this.hashCode());
    private SpoutTrade st = SpoutTrade.getInstance();
    private ConfigManager config = st.getConfig();
    private TradeInventory inventory;

    public TradeManager(SpoutPlayer initiator, SpoutPlayer target) {
        st.trades.put(initiator, this);
        st.trades.put(target, this);

        inventory = new TradeInventory(chestID);

        this.initiator = new TradePlayer(initiator);
        this.target = new TradePlayer(target);
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
        st.trades.remove(target.player);
        st.trades.remove(initiator.player);

        target.restore();
        initiator.restore();

        sendMessage("Trade cancelled");
    }

    public void confirm(SpoutPlayer player) {

        if (player.equals(initiator)) {
            initiator.setState(TradeState.CONFIRMED);
            initiator.sendMessage("Confirmed.");
        } else {
            target.setState(TradeState.CONFIRMED);
            target.sendMessage("Confirmed.");
        }

        if (target.getState().equals(TradeState.CONFIRMED) && initiator.getState().equals(TradeState.CONFIRMED)) {
            doTrade();
        }

    }

    public int countItems() {

        return 0;
    }

    public void reject() {
        abort();
    }

    public boolean onClickEvent(SpoutPlayer player, ItemStack item, int slot, Inventory inventory) {
        inventory.addItem(item);
        return false;
    }

    private void doTrade() {


        sendMessage("Trade finished");
    }

    public void sendMessage(String msg) {
        target.sendMessage(msg);
        initiator.sendMessage(msg);
    }
}