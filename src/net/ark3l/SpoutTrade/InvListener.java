/*   SpoutTrade - In game GUI trading for Bukkit with Spout
    Copyright (C) 2011  Oliver Brown
    
    TileEntityVirtualChest and VirtualChest classes attributed to the authors
    of GiftPost

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.ark3l.SpoutTrade;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

public class InvListener extends InventoryListener{
	
    public static SpoutTrade plugin;
    
    public InvListener(SpoutTrade instance) {
        plugin = instance;
    }
    
    
    
    public void onInventoryClick(InventoryClickEvent event) {
    	
    	
        Player player = event.getPlayer();
        
    	if(!SpoutTrade.getTraders().containsKey(player))
    	return;
    	
    	SpoutTradeTrade trade = SpoutTrade.getTraders().get(player);
    	
        Inventory inventory = event.getInventory();
        ItemStack item = event.getItem();
        
        if(item == null)
        	return;
        
        if(!trade.onClick(player, inventory, item))
        	event.setCancelled(true);

    }
    
    public void onInventoryClose(InventoryCloseEvent event) {
    	
        Player player = event.getPlayer();
        
    	if(!SpoutTrade.getTraders().containsKey(player))
    	return;
    	
        SpoutTrade.getTraders().get(player).onClose(player);
    }
    
}
