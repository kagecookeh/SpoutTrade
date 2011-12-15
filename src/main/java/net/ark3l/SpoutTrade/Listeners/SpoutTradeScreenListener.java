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

package net.ark3l.SpoutTrade.Listeners;

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Trade.TradeManager;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver
 */
public class SpoutTradeScreenListener extends ScreenListener {

    private final SpoutTrade plugin;

    public SpoutTradeScreenListener(SpoutTrade instance) {
        plugin = instance;
    }

    /**
     * Handles a button click event
     *
     * @param event the event
     */
    @Override
    public void onButtonClick(ButtonClickEvent event) {

        SpoutPlayer player = event.getPlayer();

        TradeManager manager = plugin.getTradeManager();

        if (manager.isBusy(player)) {
            manager.onButtonClick(event.getButton(), player);
        }
    }
}
