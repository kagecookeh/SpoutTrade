/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ark3l.SpoutTrade.Inventory;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.TileEntityChest;
import org.bukkit.craftbukkit.entity.CraftItem;

/**
 *
 * @author Oliver
 */
public class TradeInventory implements IInventory {

    private String name;
    private IInventory upperChest;
    private IInventory lowerChest;

    public TradeInventory(String s) {
        name = s;
        upperChest = new TileEntityChest();
        lowerChest = new TileEntityChest();
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
        throw new UnsupportedOperationException("Not yet implemented");
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
    
    public ItemStack[] getUpperContents() {
    return upperChest.getContents();    
    }
    
    public ItemStack[] getLowerContents() {
        return lowerChest.getContents();
    }

    public boolean a_(EntityHuman eh) {
        return true;
    }

}
