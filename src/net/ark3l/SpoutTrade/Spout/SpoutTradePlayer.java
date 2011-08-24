package net.ark3l.SpoutTrade.Spout;

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

import net.ark3l.SpoutTrade.Trade.TradePlayer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver
 * 
 */
public class SpoutTradePlayer extends TradePlayer {

	public SpoutPlayer sPlayer;

	/**
	 * @param player
	 */
	public SpoutTradePlayer(Player player) {
		super(player);
		this.sPlayer = (SpoutPlayer) player;

	}

	@Override
	public void requestConfirm(ItemStack[] chest, ItemStack[] chest2) {
		super.requestConfirm(chest, chest2);

		// TODO - implement a nice GUI
	}

}
