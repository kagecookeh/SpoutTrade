/**
 * 
 */
package net.ark3l.SpoutTrade.Trade;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Oliver
 * 
 */
public class RequestPlayer {

	private Player player;

	public RequestPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return
	 */
	public String getName() {
		return getPlayer().getName();
	}

	/**
         * sends the message to the player using the default minecraft chatbox
	 * @param string
	 */
	public void sendMessage(String string) {
		getPlayer().sendMessage(string);

	}

	/**
         * Notify the player of the trade request
	 * @param otherPlayer the player who is sending the request
	 */
	public void request(Player otherPlayer) {
		getPlayer().sendMessage(ChatColor.RED + otherPlayer.getName()
				+ ChatColor.GREEN + " has requested to trade with you.");
		getPlayer().sendMessage(ChatColor.GREEN + "Type " + ChatColor.RED
				+ "/trade accept" + ChatColor.GREEN + " to accept or "
				+ ChatColor.RED + "/trade decline" + ChatColor.GREEN
				+ " to decline");
	}

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

}
