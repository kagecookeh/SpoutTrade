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
import net.ark3l.SpoutTrade.GUI.RequestPopup;
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

    private SpoutPlayer sPlayer;
    private RequestPopup requestPopup;

    /**
     * @param player
     */
    public SpoutRequestPlayer(Player player) {
        super(player);
    }

    @Override
    public void request(Player otherPlayer) {
        sPlayer = ((SpoutPlayer) getPlayer());

        if (!sPlayer.isSpoutCraftEnabled()) {
            super.request(otherPlayer);
        } else {
            requestPopup = new RequestPopup(getsPlayer(), ChatColor.RED
                    + otherPlayer.getName() + ChatColor.WHITE
                    + " has requested to trade with you");
        }
    }

    /**
     * Closes the currently open request dialogue
     */
    public void close() {
        if (getsPlayer().isSpoutCraftEnabled()) {
            getsPlayer().getMainScreen().closePopup();
        }
    }

    /**
     * @param button the button to check
     * @return whether the button is the accept button
     */
    public boolean isAcceptButton(Button button) {
        if (button.getId() == requestPopup.getAcceptID()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param button the button to check
     * @return whether the button is the decline button
     */
    public boolean isDeclineButton(Button button) {
        if (button.getId() == requestPopup.getDeclineID()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the sPlayer
     */
    public SpoutPlayer getsPlayer() {
        return sPlayer;
    }
}
