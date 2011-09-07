package net.ark3l.SpoutTrade.GUI;

import net.ark3l.SpoutTrade.SpoutTrade;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.UUID;

/**
 * @author Oliver Brown (Arkel)
 *         Date: 05/09/11
 */
public abstract class YesNoPopup extends GenericPopup {

    private final UUID acceptID;
    private final UUID declineID;
    final Plugin st = SpoutTrade.getInstance();

    private final Container box;

    private final int widthScale;
    private final int heightScale;

    YesNoPopup(SpoutPlayer sPlayer) {

        box = new GenericContainer();

        widthScale = sPlayer.getMainScreen().getWidth() / 100;
        heightScale = sPlayer.getMainScreen().getHeight() / 100;

        GenericButton acceptButton = new GenericButton("Accept");
        acceptButton.setAlign(WidgetAnchor.CENTER_CENTER);
        acceptButton.setAnchor(WidgetAnchor.CENTER_CENTER);
        acceptButton.setHoverColor(new Color(0, 255, 0));

        GenericButton declineButton = new GenericButton("Decline");
        declineButton.setAlign(WidgetAnchor.CENTER_CENTER);
        declineButton.setAnchor(WidgetAnchor.CENTER_CENTER);
        declineButton.setHoverColor(new Color(0, 255, 0));

        box.addChildren(acceptButton, declineButton);
        box.setLayout(ContainerType.HORIZONTAL);
        box.setAnchor(WidgetAnchor.CENTER_CENTER);
        box.setWidth(widthScale * 35).setHeight(heightScale * 10);
        box.shiftYPos(20);
        box.shiftXPos(-acceptButton.getWidth());

        this.setTransparent(true);
        this.attachWidget(st, box);

        acceptID = acceptButton.getId();
        declineID = declineButton.getId();
    }

    public boolean isAccept(Button button) {
        return button.getId().equals(acceptID);
    }

    public boolean isDecline(Button button) {
        return button.getId().equals(declineID);
    }


}
