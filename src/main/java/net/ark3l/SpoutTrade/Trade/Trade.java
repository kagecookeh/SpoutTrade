package net.ark3l.SpoutTrade.Trade;

import net.ark3l.SpoutTrade.Config.LanguageManager;
import net.ark3l.SpoutTrade.Inventory.VirtualLargeChest;
import net.ark3l.SpoutTrade.Util.Log;
import net.minecraft.server.Packet101CloseWindow;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver Brown (Arkel)
 *         Date: 30/10/11
 */
public class Trade {

    private final TradePlayer initiator;
    private final TradePlayer target;

    private final VirtualLargeChest inventory;
    private final String chestID = Integer.toString(this.hashCode());

    private TradeManager manager;

    private int cancellerID;

    public Trade(TradeRequest request, TradeManager manager) {
        this.initiator = request.initiator;
        this.target = request.target;
        this.manager = manager;

        inventory = new VirtualLargeChest(chestID);

        inventory.openChest(target.getPlayer());
        inventory.openChest(initiator.getPlayer());

        Log.trade(initiator.getName() + " began trading with " + target.getName());
    }

    private void scheduleCancellation() {

        cancellerID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(manager.st, new Runnable() {

            public void run() {
                abort();

                initiator.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.TIMED));
                target.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.TIMED));
                Log.trade("The trade between " + initiator.getName() + " and " + target.getName() + " timed out");
            }
        }, 600L);

    }

    private void unscheduleCancellation() {
        Bukkit.getServer().getScheduler().cancelTask(cancellerID);
    }

    public void onClose(SpoutPlayer player) {

        if (target.getState() == TradeState.CHEST_OPEN || initiator.getState() == TradeState.CHEST_OPEN) {
            if (player.equals(initiator.getPlayer())) {
                CraftPlayer cPlayer = (CraftPlayer) target.getPlayer();
                cPlayer.getHandle().netServerHandler.sendPacket(new Packet101CloseWindow());
            } else {
                CraftPlayer cPlayer = (CraftPlayer) initiator.getPlayer();
                cPlayer.getHandle().netServerHandler.sendPacket(new Packet101CloseWindow());
            }

            target.setState(TradeState.CHEST_CLOSED);
            initiator.setState(TradeState.CHEST_CLOSED);

            if (getUsedCases(inventory.getUpperContents()) > getEmptyCases(target.getInventory().getContents()) || getUsedCases(inventory.getLowerContents()) > getEmptyCases(initiator.getInventory().getContents())) {
                abort();
                sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.NOROOM));
                return;
            }

            scheduleCancellation();

            initiator.requestConfirm(inventory.getUpperContents(), inventory.getLowerContents());
            target.requestConfirm(inventory.getLowerContents(), inventory.getUpperContents());
        }

    }

    public void abort() {

        if (!Bukkit.getServer().getScheduler().isCurrentlyRunning(cancellerID)) {
            unscheduleCancellation();
        }

        target.close();
        initiator.close();

        initiator.restore(inventory.getUpperContents());
        target.restore(inventory.getLowerContents());

        manager.finish(this);

        Log.trade("The trade between " + initiator.getName() + " and " + target.getName() + " was aborted");

        sendMessage(LanguageManager.getString(LanguageManager.Strings.CANCELLED));
    }

    public void confirm(SpoutPlayer player) {

        if (player.equals(initiator.getPlayer())) {
            initiator.setState(TradeState.CONFIRMED);
            initiator.sendMessage(LanguageManager.getString(LanguageManager.Strings.CONFIRMED));
        } else {
            target.setState(TradeState.CONFIRMED);
            target.sendMessage(LanguageManager.getString(LanguageManager.Strings.CONFIRMED));
        }

        if (target.getState().equals(TradeState.CONFIRMED) && initiator.getState().equals(TradeState.CONFIRMED)) {
            unscheduleCancellation();
            doTrade();
        }

    }

    public void reject() {
        abort();
    }

    public Event.Result slotCheck(SpoutPlayer player, int slot, Inventory inv) {

        if (inv.getName().equals(chestID)) {
            if (player.equals(initiator.getPlayer()) && slot < 27) {
                return Event.Result.DEFAULT;
            } else if (player.equals(target.getPlayer()) && slot >= 27) {
                return Event.Result.DEFAULT;
            } else {
                player.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.NOTYOURS));
            }
        }

        return Event.Result.DENY;
    }

    public boolean canUseInventory() {
        return target.getState() != TradeState.CHEST_OPEN || initiator.getState() != TradeState.CHEST_OPEN;
    }

    private void doTrade() {

        initiator.doTrade(inventory.getUpperContents());
        target.doTrade(inventory.getLowerContents());

        manager.finish(this);

        sendMessage(LanguageManager.getString(LanguageManager.Strings.FINISHED));
        Log.trade("The trade between " + initiator.getName() + " and " + target.getName() + " was completed");
    }

    private int getEmptyCases(ItemStack[] contents) {
        int count = 0;
        for (ItemStack content : contents) {
            if (content == null) {
                count++;
            }
        }
        return count;
    }

    private int getUsedCases(ItemStack[] contents) {
        int count = 0;
        for (ItemStack content : contents) {
            if (content != null) {
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
