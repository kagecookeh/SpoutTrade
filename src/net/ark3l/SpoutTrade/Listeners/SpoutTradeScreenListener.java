package net.ark3l.SpoutTrade.Listeners;

/*   SpoutTrade - In game GUI trading for Bukkit Minecraft servers with Spout
 Copyright (C) 2011  Oliver Brown (Arkel)

 TileEntityVirtualChest and VirtualChest classes are attributed to Balor and
 Timberjaw, the authors of GiftPost

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

import java.util.HashMap;

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Trade.Trade;
import net.ark3l.SpoutTrade.Trade.TradeRequest;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;

/**
 * @author Oliver
 * 
 */
public class SpoutTradeScreenListener extends ScreenListener {

	public static SpoutTrade plugin;

	public SpoutTradeScreenListener(SpoutTrade instance) {
		plugin = instance;
	}

	HashMap<Player, Trade> trades = plugin.trades;
	HashMap<Player, TradeRequest> requests = plugin.requests;

	public void onButtonClick(ButtonClickEvent event) {

		Player player = event.getPlayer();

		if (trades.get(player) != null) {
			trades.get(player).onButtonClick(event.getButton(), player);
		} else if (requests.get(player) != null) {
			requests.get(player).onButtonClick(event.getButton(), player);
		} else {
			return;
		}
	}
	

}
