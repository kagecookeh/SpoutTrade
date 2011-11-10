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

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Util.Log;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Oliver Brown
 */
public class TradeManager {

    HashMap<SpoutPlayer, TradeRequest> requests = new HashMap<SpoutPlayer, TradeRequest>();
    HashMap<SpoutPlayer, Trade> trades = new HashMap<SpoutPlayer, Trade>();

    SpoutTrade st;

    private final String chestID = Integer.toString(this.hashCode());
    private int cancellerID;

    public TradeManager(SpoutTrade st) {
        this.st = st;
    }


    public void onButtonClick(Button button, SpoutPlayer player) {
        if (trades.containsKey(player)) {
            // TODO - button clicks for trades
        } else if (requests.containsKey(player)) {
            (requests.get(player)).onButtonClick(button, player);
        }
    }

    public void finish(TradeRequest request) {
        for (
                Iterator<Map.Entry<SpoutPlayer, TradeRequest>> iter = requests.entrySet().iterator();
                iter.hasNext();
                ) {
            Map.Entry<SpoutPlayer, TradeRequest> entry = iter.next();
            if (request.equals(entry.getValue())) {
                iter.remove();
            }
        }
    }

    public void finish(Trade trade) {
        for (
                Iterator<Map.Entry<SpoutPlayer, Trade>> iter = trades.entrySet().iterator();
                iter.hasNext();
                ) {
            Map.Entry<SpoutPlayer, Trade> entry = iter.next();
            if (trade.equals(entry.getValue())) {
                iter.remove();
            }
        }
    }

    public Trade getTrade(SpoutPlayer player) {
        if (trades.containsKey(player)) {
            return trades.get(player);
        }

        return null;
    }

    public void onPlayerQuit(SpoutPlayer player) {
        if (trades.containsKey(player)) {
            trades.get(player).abort();
        } else if (requests.containsKey(player)) {
            requests.get(player).decline();
        }
    }

    public void begin(TradePlayer player, TradePlayer target) {
        TradeRequest request = new TradeRequest(player, target, this);

        requests.put(player.getPlayer(), request);
        requests.put(target.getPlayer(), request);
    }

    public void progress(TradeRequest request) {
        finish(request);

        Trade trade = new Trade(request, this);
        trades.put(request.initiator.getPlayer(), trade);
        trades.put(request.target.getPlayer(), trade);
    }

    public boolean isBusy(SpoutPlayer player) {
        return trades.containsKey(player) || requests.containsKey(player);
    }

    public boolean isTrading(SpoutPlayer player) {
        return trades.containsKey(player);
    }

    public void handleCommand(String command, SpoutPlayer player) {
        if (trades.containsKey(player)) {
            if (command.equalsIgnoreCase("accept")) {
                trades.get(player).confirm(player);
            } else {
                trades.get(player).abort();
            }
        } else if (requests.containsKey(player)) {
            if (command.equalsIgnoreCase("decline")) {
                requests.get(player).decline();
            } else {
                requests.get(player).accept(player);
            }
        }
    }

    public void terminateActiveTrades() {

        if (!trades.isEmpty() || !requests.isEmpty()) {
            Log.warning("SpoutTrade detected that players were still trading. Attempting to cancel trades...");
            Player[] players = st.getServer().getOnlinePlayers();
            for (Player player : players) {
                if (trades.get(player) != null) {
                    trades.get(player).abort();
                } else if (requests.get(player) != null) {
                    requests.get(player).decline();
                }
            }
            Log.info("Trades cancelled");
        }

    }


}