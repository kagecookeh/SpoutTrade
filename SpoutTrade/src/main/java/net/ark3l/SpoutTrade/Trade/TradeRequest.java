package net.ark3l.SpoutTrade.Trade;

import net.ark3l.SpoutTrade.Spout.SpoutRequestPlayer;

import net.ark3l.SpoutTrade.SpoutTrade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TradeRequest {

    private SpoutPlayer initiator;
    private SpoutRequestPlayer target;
    private int cancellerID;
    private SpoutTrade st;

    public TradeRequest(Player player, Player target) {
        st = SpoutTrade.getInstance();

        // Request sent
        player.sendMessage(ChatColor.GREEN + st.getConfig().getString(15));

        this.initiator = (SpoutPlayer) player;

        this.target = new SpoutRequestPlayer(target);
        this.target.request(player);

        st.requests.put(this.initiator, this);
        st.requests.put((SpoutPlayer) target, this);

        scheduleCancellation();
    }

    private void scheduleCancellation() {

        cancellerID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(st,
                new Runnable() {

                    @Override
                    public void run() {
                        target.close();
                        // Request timed out
                        initiator.sendMessage(ChatColor.RED + st.getConfig().getString(6));
                        target.sendMessage(ChatColor.RED + st.getConfig().getString(6));

                        st.requests.remove(initiator);
                        st.requests.remove(target.getsPlayer());

                    }
                }, 300L);

    }

    /**
     * Ensures the sender is the target of the trade then creates a new trade instance
     * @param sender - the player who sent the accept command
     */
    public void accept(Player sender) {
        if (sender != target.getPlayer()) {
            return;
        }

        unscheduleCancellation();

        target.close();

        new Trade(initiator, target.getsPlayer());

        st.requests.remove(initiator);
        st.requests.remove(target.getsPlayer());

    }

    private void unscheduleCancellation() {
        Bukkit.getServer().getScheduler().cancelTask(cancellerID);
    }

    /**
     * Declines this instance of trade request
     */
    public void decline() {
        unscheduleCancellation();

        // request declined
        initiator.sendMessage(ChatColor.RED + st.getConfig().getString(16));
        target.close();

        st.requests.remove(initiator);
        st.requests.remove(target.getsPlayer());

    }

    /**
     * Determines if the button is accept or decline and calls the appropriate method
     * @param button - the button pressed
     * @param player - the player who pressed it
     */
    public void onButtonClick(Button button, Player player) {

        if (target.isAcceptButton(button)) {
            accept(player);
        } else if (target.isDeclineButton(button)) {
            decline();
        }
    }
}