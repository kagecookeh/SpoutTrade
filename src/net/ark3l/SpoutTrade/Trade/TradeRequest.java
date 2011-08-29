package net.ark3l.SpoutTrade.Trade;

import net.ark3l.SpoutTrade.Spout.SpoutRequestPlayer;

import net.ark3l.SpoutTrade.SpoutTrade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;

public class TradeRequest {

	public Player initiator;
	public SpoutRequestPlayer target;
	private int cancellerID;

	public TradeRequest(Player player, Player target) {

		player.sendMessage(ChatColor.GREEN + "Trade request sent to "
				+ ChatColor.RED + target.getName());

		SpoutTrade.requests.put(initiator, this);
		SpoutTrade.requests.put(target, this);

		this.initiator = player;

		this.target = new SpoutRequestPlayer(target);
		this.target.request(player);
		
		scheduleCancellation();
	}

	/**
	 * 
	 */
	private void scheduleCancellation() {
	
		cancellerID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("SpoutTrade"),
				new Runnable() {
			@Override
			public void run() {
				target.close();
				initiator.sendMessage(ChatColor.RED + "Trade request timed out");
				target.sendMessage(ChatColor.RED + "Trade request timed out");
				
				SpoutTrade.requests.remove(initiator);
				SpoutTrade.requests.remove(target.player);
				
			}
		}, 300L);
		
	}

	public void accept(Player sender) {
		if(sender != target.player)
			return;
		
		unscheduleCancellation();
		
		target.close();

		new Trade(initiator, target.toPlayer());

		SpoutTrade.requests.remove(initiator);
		SpoutTrade.requests.remove(target.toPlayer());
		
	}

	/**
	 * 
	 */
	private void unscheduleCancellation() {
		Bukkit.getServer().getScheduler().cancelTask(cancellerID);
	}

	public void decline() {
		unscheduleCancellation();
		
		initiator.sendMessage(ChatColor.RED + "Trade request declined.");
		target.close();
		
		SpoutTrade.requests.remove(initiator);
		SpoutTrade.requests.remove(target.toPlayer());
		
	}

	/**
	 * @param button
	 * @param player
	 */
	public void onButtonClick(Button button, Player player) {

		if (target.isAcceptButton(button)) {
			accept(player);
		} else if (target.isDeclineButton(button)) {
			decline();
		}
	}
}
