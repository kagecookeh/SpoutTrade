package net.ark3l.SpoutTrade.Trade;

import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Spout.SpoutRequestPlayer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;

public class TradeRequest {

	public Player initiator;
	public SpoutRequestPlayer target;

	public TradeRequest(Player player, Player target) {

		player.sendMessage(ChatColor.GREEN + "Trade request sent to "
				+ ChatColor.RED + target.getName());

		SpoutTrade.requests.put(initiator, this);
		SpoutTrade.requests.put(target, this);

		this.initiator = player;

		this.target = new SpoutRequestPlayer(target);
		this.target.request(player);
	}

	public void accept(Player sender) {
		new Trade(initiator, target.toPlayer());

		SpoutTrade.requests.remove(initiator);
		SpoutTrade.requests.remove(target.toPlayer());
	}

	public void decline(Player sender) {
		SpoutTrade.requests.remove(initiator);
		SpoutTrade.requests.remove(target.toPlayer());

		target.close();
	}

	/**
	 * @param button
	 * @param player
	 */
	public void onButtonClick(Button button, Player player) {

		if (target.isAcceptButton(button)) {
			accept(player);
		} else if (target.isDeclineButton(button)) {
			decline(player);
		}
	}
}
