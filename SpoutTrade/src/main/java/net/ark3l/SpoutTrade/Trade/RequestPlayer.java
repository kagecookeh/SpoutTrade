/**
 * 
 */
package net.ark3l.SpoutTrade.Trade;

import net.ark3l.SpoutTrade.GUI.RequestPopup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Oliver
 * 
 */
public class RequestPlayer {

	private SpoutPlayer player;
        private RequestPopup requestPopup;

	public RequestPlayer(SpoutPlayer player) {
		this.player = player;
	}

	/**
	 * @return
	 */
	public String getName() {
		return getPlayer().getName();
	}

	/**
         *
	 * @param msg - message to send
	 */
	public void sendMessage(String msg) {
				
		if (player.isSpoutCraftEnabled() && msg.length() < 26) {
			player.sendNotification("net/ark3l/SpoutTrade/Trade", msg, Material.SIGN);
		} else {
            player.sendMessage(msg);
        }

	}

	/**
         * Notify the player of the trade request
	 * @param otherPlayer the player who is sending the request
	 */
	public void request(Player otherPlayer) {
            if(player.isSpoutCraftEnabled()) {
                requestPopup = new RequestPopup(player, ChatColor.RED
                    + otherPlayer.getName() + ChatColor.WHITE
                    + " has requested to trade with you");
            }
            else {
		getPlayer().sendMessage(ChatColor.RED + otherPlayer.getName()
				+ ChatColor.GREEN + " has requested to trade with you.");
		getPlayer().sendMessage(ChatColor.GREEN + "Type " + ChatColor.RED
				+ "/trade accept" + ChatColor.GREEN + " to accept or "
				+ ChatColor.RED + "/trade decline" + ChatColor.GREEN
				+ " to decline");
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
        if (player.isSpoutCraftEnabled()) {
            player.getMainScreen().closePopup();
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

}