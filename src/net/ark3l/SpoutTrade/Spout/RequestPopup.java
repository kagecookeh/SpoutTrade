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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver
 * 
 */
public class RequestPopup extends GenericPopup {

	private GenericPopup popup;

	public UUID acceptID;
	public UUID declineID;

	public RequestPopup(SpoutPlayer sPlayer, String text) {

		Plugin spoutTrade = Bukkit.getServer().getPluginManager()
				.getPlugin("SpoutTrade");

		popup = new GenericPopup();

		int center = sPlayer.getMainScreen().getWidth() / 2;
		int widthScale = sPlayer.getMainScreen().getWidth() / 100;
		int heightScale = sPlayer.getMainScreen().getHeight() / 100;

		GenericLabel label = new GenericLabel(text);
		label.setAlign(WidgetAnchor.CENTER_CENTER);
		label.setAnchor(WidgetAnchor.CENTER_CENTER);

		GenericButton acceptButton = new GenericButton("Accept");
		acceptButton.setX(center - (widthScale * 10)).setY(heightScale * 70);
		acceptButton.setWidth(widthScale * 20).setHeight(heightScale * 8);

		GenericButton declineButton = new GenericButton("Decline");
		declineButton.setX(center - (widthScale * 10)).setY(heightScale * 80);
		declineButton.setWidth(widthScale * 20).setHeight(heightScale * 8);

		popup.setTransparent(true);
		popup.attachWidget(spoutTrade, label);
		popup.attachWidget(spoutTrade, acceptButton);
		popup.attachWidget(spoutTrade, declineButton);

		acceptID = acceptButton.getId();
		declineID = declineButton.getId();

		sPlayer.getMainScreen().attachPopupScreen(popup);
	}

}
