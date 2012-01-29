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

package net.ark3l.SpoutTrade.GUI;


import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ConfirmPopup extends YesNoPopup {

    public ConfirmPopup(SpoutPlayer sPlayer, ItemStack[] itemsTo, ItemStack[] itemsFrom) {
        super(sPlayer);

        int widthScale = sPlayer.getMainScreen().getWidth() / 100;
        int heightScale = sPlayer.getMainScreen().getHeight() / 100;

        GenericContainer container = new GenericContainer();
        GenericListWidget itemsToList = new GenericListWidget();
        GenericListWidget itemsFromList = new GenericListWidget();

        container.addChildren(itemsToList, itemsFromList);
        container.setLayout(ContainerType.HORIZONTAL);
        container.setAnchor(WidgetAnchor.CENTER_CENTER);
        container.setWidth(widthScale * 35).setHeight(heightScale * 40);
        container.shiftYPos(40 - container.getHeight());
        container.shiftXPos(-itemsFromList.getWidth());


        for (ItemStack item : itemsTo) {
            if (item != null)
                itemsToList.addItem(new ListWidgetItem(item.getType().toString(), "x" + item.getAmount()));
        }

        for (ItemStack item : itemsFrom) {
            if (item != null)
                itemsFromList.addItem(new ListWidgetItem(item.getType().toString(), "x" + item.getAmount()));
        }

        Plugin spoutTrade = Bukkit.getServer().getPluginManager().getPlugin("SpoutTrade");
        attachWidget(spoutTrade, container);
        sPlayer.getMainScreen().attachPopupScreen(this);
    }

}
