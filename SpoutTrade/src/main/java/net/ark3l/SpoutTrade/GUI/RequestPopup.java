package net.ark3l.SpoutTrade.GUI;

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

import net.ark3l.SpoutTrade.SpoutTrade;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver
 * 
 */
public class RequestPopup extends GenericPopup {

	protected GenericPopup popup;
	protected Container box;

	private UUID acceptID;
	private UUID declineID;

	public RequestPopup(SpoutPlayer sPlayer, String text) {

		Plugin spoutTrade = SpoutTrade.getInstance();

		popup = new GenericPopup();
		box = new GenericContainer();

		int widthScale = sPlayer.getMainScreen().getWidth() / 100;
		int heightScale = sPlayer.getMainScreen().getHeight() / 100;

		GenericLabel label = new GenericLabel(text);
		label.setAlign(WidgetAnchor.CENTER_CENTER);
		label.setAnchor(WidgetAnchor.CENTER_CENTER);
		
		GenericButton acceptButton = new GenericButton("Accept");
		acceptButton.setAlign(WidgetAnchor.CENTER_CENTER);
		acceptButton.setAnchor(WidgetAnchor.CENTER_CENTER);
		acceptButton.setHoverColor(new Color(0, 255, 0));
	
		GenericButton declineButton = new GenericButton("Decline");
		declineButton.setAlign(WidgetAnchor.CENTER_CENTER);
		declineButton.setAnchor(WidgetAnchor.CENTER_CENTER);
		declineButton.setHoverColor(new Color(0, 255, 0));
		
		box.addChildren(acceptButton, declineButton);
		box.setLayout(ContainerType.HORIZONTAL);
		box.setAnchor(WidgetAnchor.CENTER_CENTER);
		box.setWidth(widthScale * 35).setHeight(heightScale * 10);
		box.shiftYPos(20);
		box.shiftXPos(-acceptButton.getWidth());

		popup.setTransparent(true);
		popup.attachWidget(spoutTrade, label);
		popup.attachWidget(spoutTrade, box);

		acceptID = acceptButton.getId();
		declineID = declineButton.getId();

		sPlayer.getMainScreen().attachPopupScreen(popup);
	}

    /**
     * @return the acceptID
     */
    public UUID getAcceptID() {
        return acceptID;
    }

    /**
     * @return the declineID
     */
    public UUID getDeclineID() {
        return declineID;
    }

}
