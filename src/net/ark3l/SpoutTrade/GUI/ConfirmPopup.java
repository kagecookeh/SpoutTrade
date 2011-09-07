package net.ark3l.SpoutTrade.GUI;


import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.List;

public class ConfirmPopup extends YesNoPopup {

    private GenericLabel label;

    public ConfirmPopup(SpoutPlayer sPlayer, String itemsTo,
                        String itemsFrom) {
        super(sPlayer);

        // TODO - implement item widget GUI

        sPlayer.getMainScreen().attachPopupScreen(this);
    }

}
