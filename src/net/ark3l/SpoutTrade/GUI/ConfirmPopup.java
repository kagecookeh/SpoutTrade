package net.ark3l.SpoutTrade.GUI;


import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

class ConfirmPopup extends YesNoPopup {

    private GenericLabel label;

	public ConfirmPopup(SpoutPlayer sPlayer, ItemStack[] itemsTo,
			ItemStack[] itemsFrom) {
            super(sPlayer);



            label = new GenericLabel("Are you sure you want to trade");
            label.setAlign(WidgetAnchor.CENTER_CENTER);
            label.setAnchor(WidgetAnchor.CENTER_CENTER);

        sPlayer.getMainScreen().attachPopupScreen(popup);
	}

}
