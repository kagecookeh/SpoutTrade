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

	protected Player player;

	public RequestPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return
	 */
	public String getName() {
		return player.getName();
	}

	public Player toPlayer() {
		return player;
	}

	/**
	 * @param string
	 */
	public void sendMessage(String string) {
		player.sendMessage(string);

	}

	/**
	 * @param p2
	 */
	public void request(Player otherPlayer) {
		player.sendMessage(ChatColor.RED + otherPlayer.getName()
				+ ChatColor.GREEN + " has requested to trade with you.");
		player.sendMessage(ChatColor.GREEN + "Type " + ChatColor.RED
				+ "/trade accept" + ChatColor.GREEN + " to accept or "
				+ ChatColor.RED + "/trade decline" + ChatColor.GREEN
				+ " to decline");
	}

}
