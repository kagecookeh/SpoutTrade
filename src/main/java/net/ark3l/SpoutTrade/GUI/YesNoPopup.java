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

package net.ark3l.SpoutTrade.GUI;

import net.ark3l.SpoutTrade.SpoutTrade;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.UUID;

/**
 * @author Oliver Brown (Arkel)
 *         Date: 05/09/11
 */
public abstract class YesNoPopup extends GenericPopup {

	private final UUID acceptID;
	private final UUID declineID;
	protected SpoutTrade st = (SpoutTrade) getPlugin().getServer().getPluginManager().getPlugin("SpoutTrade");

	YesNoPopup(SpoutPlayer sPlayer) {

		Container box = new GenericContainer();

		int widthScale = sPlayer.getMainScreen().getWidth() / 100;
		int heightScale = sPlayer.getMainScreen().getHeight() / 100;

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

		this.setTransparent(true);
		this.attachWidget(st, box);

		acceptID = acceptButton.getId();
		declineID = declineButton.getId();
	}

	public boolean isAccept(Button button) {
		return button.getId().equals(acceptID);
	}

	public boolean isDecline(Button button) {
		return button.getId().equals(declineID);
	}


}
