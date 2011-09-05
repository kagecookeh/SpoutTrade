/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ark3l.SpoutTrade.Inventory;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.ItemStack;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Oliver
 */
public class TradeInventory implements IInventory {

    private final String name;
    private final TradeInventoryHalf upperChest;
    private final TradeInventoryHalf lowerChest;

    public TradeInventory(String s) {
         name = s;
        upperChest = new TradeInventoryHalf();
        lowerChest = new TradeInventoryHalf();
    }

    public int getSize() {
        return upperChest.getSize() + lowerChest.getSize();
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem(int i) {
        if (i >= upperChest.getSize()) {
            return lowerChest.getItem(i - upperChest.getSize());
        } else {
            return upperChest.getItem(i);
        }
    }

    public ItemStack splitStack(int i, int j) {
        return null;
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i >= upperChest.getSize()) {
            lowerChest.setItem(i - upperChest.getSize(), itemstack);
        } else {
            upperChest.setItem(i, itemstack);
        }
    }

    public int getMaxStackSize() {
        return upperChest.getMaxStackSize();
    }

    public void update() {
        upperChest.update();
        lowerChest.update();
    }

    public ItemStack[] getContents() {
        ItemStack[] A = upperChest.getContents();
        ItemStack[] B = lowerChest.getContents();

        ItemStack[] contents = new ItemStack[A.length + B.length];
        System.arraycopy(A, 0, contents, 0, A.length);
        System.arraycopy(B, 0, contents, A.length, B.length);

        return contents;
    }

    public List<ItemStack> getUpperContents() {
        return Arrays.asList(upperChest.getContents());
    }

    public List<ItemStack> getLowerContents() {
        return Arrays.asList(lowerChest.getContents());
    }

    public boolean a_(EntityHuman eh) {
        return true;
    }

    public int count() {
        ItemStack[] contents = getContents();
        int count = 0;
        for (ItemStack content : contents) {
            if (content != null) {
                if(content.count == 0) {
                    count ++;
                } else {
                count += content.count;
                }
            }
        }
        System.out.print("countItemsChest " + count);
        return count;
    }

    public org.bukkit.inventory.ItemStack[] getExistingItems() {
        ItemStack[] contents = getContents();

        List<org.bukkit.inventory.ItemStack> existingItems;
        existingItems = new ArrayList<org.bukkit.inventory.ItemStack>();
        for (ItemStack content : contents) {
            if (content != null) {
                existingItems.add(new org.bukkit.inventory.ItemStack(content.id, content.count));
            }
        }

        return (org.bukkit.inventory.ItemStack[]) existingItems.toArray();
    }

    public boolean addTradeItem(boolean upper, org.bukkit.inventory.ItemStack item) {
        if (upper) {
            upperChest.addItemStack(new ItemStack(item.getTypeId(), item.getAmount(), item.getDurability()));
        } else {
            lowerChest.addItemStack(new ItemStack(item.getTypeId(), item.getAmount(), item.getDurability()));
        }
        return false;
    }
}
