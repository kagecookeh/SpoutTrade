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
import net.ark3l.SpoutTrade.Util.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TradeRequest {

    TradePlayer initiator;
    TradePlayer target;
    private int cancellerID;
    private TradeManager manager;


    public TradeRequest(TradePlayer player, TradePlayer target, TradeManager manager) {
        this.manager = manager;

        this.initiator = player;
        this.target = target;

        this.target.request(player);

        // Request sent
        player.sendMessage(ChatColor.GREEN + LanguageManager.getString(LanguageManager.Strings.SENT));
        Log.trade(player.getName() + " requested to trade with " + target.getName());

        scheduleCancellation();
    }

    private void scheduleCancellation() {

        cancellerID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(manager.spoutTrade, new Runnable() {

            public void run() {
                decline();
                Log.trade("The trade request between " + initiator.getName() + " and " + target.getName() + " timed out");
            }
        }, 300L);

    }

    /**
     * Ensures the sender is the target of the trade then creates a new trade instance
     *
     * @param sender - the player who sent the accept command
     */
    public void accept(Player sender) {
        if (sender != target.getPlayer()) {
            return;
        }

        unscheduleCancellation();
        manager.progress(this);

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
        initiator.sendMessage(ChatColor.RED + LanguageManager.getString(LanguageManager.Strings.DECLINED));
        manager.finish(this);

    }

}
