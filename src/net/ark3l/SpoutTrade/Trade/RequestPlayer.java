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

package net.ark3l.SpoutTrade.Trade;

import net.ark3l.SpoutTrade.Config.LanguageManager;
import net.ark3l.SpoutTrade.GUI.RequestPopup;
import net.ark3l.SpoutTrade.SpoutTrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver
 */
class RequestPlayer {

    private final SpoutPlayer player;
    private RequestPopup requestPopup;
    private LanguageManager lang = SpoutTrade.getInstance().getLang();

    public RequestPlayer(SpoutPlayer player) {
        this.player = player;
    }

// --Commented out by Inspection START (04/09/11 11:06):
//	/**
//	 * @return
//	 */
//	public String getName() {
//		return getPlayer().getName();
//	}
// --Commented out by Inspection STOP (04/09/11 11:06)

    /**
     * sends the message to the player using the default minecraft chatbox
     *
     * @param msg the message to send
     */
    public void sendMessage(String msg) {

        if (player.isSpoutCraftEnabled() && msg.length() < 26) {
            player.sendNotification("Trade", msg, Material.SIGN);
        } else {
            player.sendMessage(msg);
        }

    }

    /**
     * Notify the player of the trade request
     *
     * @param otherPlayer the player who is sending the request
     */
    public void request(Player otherPlayer) {
        if (this.player.isSpoutCraftEnabled()) {
            requestPopup = new RequestPopup(player, ChatColor.RED
                    + otherPlayer.getName() + ChatColor.WHITE
                    + lang.getString(LanguageManager.Strings.REQUESTED));
        } else {
            getPlayer().sendMessage(ChatColor.RED + otherPlayer.getName()
                    + ChatColor.GREEN + lang.getString(LanguageManager.Strings.REQUESTED));
            getPlayer().sendMessage(ChatColor.RED + "/trade accept" + ChatColor.GREEN + lang.getString(LanguageManager.Strings.TOACCEPT));
            player.sendMessage(ChatColor.RED + "/trade decline" + ChatColor.GREEN + lang.getString(LanguageManager.Strings.TODECLINE));
        }
    }

    /**
     * @return the SpoutPlayer
     */
    public SpoutPlayer getPlayer() {
        return player;
    }

    /**
     * Closes the currently open request dialogue
     */
    public void close() {
        if (requestPopup != null) {
            requestPopup.close();
        }
    }

    /**
     * @param button the button to check
     * @return whether the button is the accept button
     */
    public boolean isAcceptButton(Button button) {
        return requestPopup.isAccept(button);
    }

    /**
     * @param button the button to check
     * @return whether the button is the decline button
     */
    public boolean isDeclineButton(Button button) {
        return requestPopup.isDecline(button);
    }

}
