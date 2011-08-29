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

import net.ark3l.SpoutTrade.Trade.RequestPlayer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver
 * 
 */
public class SpoutRequestPlayer extends RequestPlayer {

	public SpoutPlayer sPlayer;
	private RequestPopup requestPopup;

	/**
	 * @param player
	 */
	public SpoutRequestPlayer(Player player) {
		super(player);
	}

	@Override
	public void request(Player otherPlayer) {
		sPlayer = ((SpoutPlayer) player);

		if (!sPlayer.isSpoutCraftEnabled())
			super.request(otherPlayer);
		else {
			requestPopup = new RequestPopup(sPlayer, ChatColor.RED
					+ otherPlayer.getName() + ChatColor.WHITE
					+ " has requested to trade with you");
		}
	}

	/**
	 * 
	 */
	public void close() {
		if (sPlayer.isSpoutCraftEnabled())
			sPlayer.getMainScreen().closePopup();
	}

	/**
	 * @param button
	 * @return
	 */
	public boolean isAcceptButton(Button button) {
		if (button.getId() == requestPopup.acceptID)
			return true;
		else
			return false;
	}

	/**
	 * @param button
	 * @return
	 */
	public boolean isDeclineButton(Button button) {
		if (button.getId() == requestPopup.declineID)
			return true;
		else
			return false;
	}

}
