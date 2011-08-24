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

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Trade.TradeRequest;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerListener;

public class SpoutTradePlayerListener extends PlayerListener {

	private SpoutTrade plugin;

	public SpoutTradePlayerListener(SpoutTrade instance) {
		plugin = instance;
	}

	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player))
			return;

		Player player = event.getPlayer();
		Player target = (Player) event.getRightClicked();

		if (SpoutTrade.trades.containsKey(target)
				|| SpoutTrade.requests.containsKey(target)) {
			player.sendMessage(ChatColor.RED + "That player is already trading");
			return;
		}

		new TradeRequest(player, target);
		event.setCancelled(true);

	}

}
