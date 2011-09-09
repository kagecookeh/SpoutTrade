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

import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver
 */
public class RequestPopup extends YesNoPopup {


    public RequestPopup(SpoutPlayer sPlayer, String text) {
        super(sPlayer);

        GenericLabel label = new GenericLabel(text);
        label.setAlign(WidgetAnchor.CENTER_CENTER);
        label.setAnchor(WidgetAnchor.CENTER_CENTER);

        this.attachWidget(st, label);

        sPlayer.getMainScreen().attachPopupScreen(this);
    }


}
