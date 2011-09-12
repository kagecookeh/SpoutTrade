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
import net.ark3l.SpoutTrade.SpoutTrade;
import net.ark3l.SpoutTrade.Util.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TradeRequest {

	private final SpoutPlayer initiator;
	private final RequestPlayer target;
	private int cancellerID;
	private final SpoutTrade st;
	private final LanguageManager lang;

	public TradeRequest(Player player, Player target) {
		st = SpoutTrade.getInstance();
		lang = st.getLang();
		// Request sent
		player.sendMessage(ChatColor.GREEN + lang.getString(LanguageManager.Strings.SENT));

		this.initiator = (SpoutPlayer) player;

		this.target = new RequestPlayer((SpoutPlayer) target);
		this.target.request(player);

		st.requests.put(this.initiator, this);
		st.requests.put(this.target.getPlayer(), this);

		Log.trade(player.getName() + " requested to trade with " + target.getName());

		scheduleCancellation();
	}

	private void scheduleCancellation() {

		cancellerID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(st, new Runnable() {

			public void run() {
				target.close();

				st.requests.remove(target.getPlayer());
				st.requests.remove(initiator);

				initiator.sendMessage(ChatColor.RED + lang.getString(LanguageManager.Strings.TIMED));
				Log.trade("The trade request " + initiator.getName() + " and " + target.getName() + " timed out");
			}
		}, 300L);

	}

	/**
	 * Ensures the sender is the target of the trade then creates a new trade instance
	 *
	 * @param sender - the player who sent the accept command
	 */
	public void accept(Player sender) {
		if(sender != target.getPlayer()) {
			return;
		}

		unscheduleCancellation();

		target.close();

		new TradeManager(initiator, target.getPlayer());

		st.requests.remove(initiator);
		st.requests.remove(target.getPlayer());

	}

	private void unscheduleCancellation() {
		Bukkit.getServer().getScheduler().cancelTask(cancellerID);
	}

	/**
	 * Declines this instance of trade request
	 */
	public void decline() {
		unscheduleCancellation();

		// request declined
		initiator.sendMessage(ChatColor.RED + lang.getString(LanguageManager.Strings.DECLINED));
		target.close();

		st.requests.remove(initiator);
		st.requests.remove(target.getPlayer());

	}

	/**
	 * Determines if the button is accept or decline and calls the appropriate method
	 *
	 * @param button - the button pressed
	 * @param player - the player who pressed it
	 */
	public void onButtonClick(Button button, Player player) {

		if(target.isAcceptButton(button)) {
			accept(player);
		} else if(target.isDeclineButton(button)) {
			decline();
		}
	}
}
