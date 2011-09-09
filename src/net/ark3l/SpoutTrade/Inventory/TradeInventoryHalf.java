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

package net.ark3l.SpoutTrade.Inventory;


import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.TileEntityChest;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Oliver Brown (Arkel)
 *         Date: 04/09/11
 */
class TradeInventoryHalf extends TileEntityChest implements IInventory {
    private String name = "Chest";
    private Queue<Integer> emptyCases;

    TradeInventoryHalf() {
        super();
        initEmptyCases();
    }

    private void initEmptyCases() {
        emptyCases = new ArrayDeque<Integer>(getSize());
        for (int i = 0; i < getSize(); i++)
            emptyCases.add(i);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return if the chest is full
     *
     * @return whether the chest is full
     */
    public boolean isFull() {
        return emptyCases.isEmpty();
    }

    /**
     * Return if the chest is empty
     *
     * @return whether the chest is empty
     */
    public boolean isEmpty() {
        return emptyCases.size() == getSize();
    }

    /**
     * return the number of emptyCases
     *
     * @return the number of empty cases
     */
    public int emptyCasesLeft() {
        return emptyCases.size();
    }

    /**
     * Alias to q_()
     *
     * @return the size
     */
    public int size() {
        return getSize();
    }

    /**
     * Look for the first empty case in the chest to add the stack.
     *
     * @param itemstack the itemstack to add
     * @return whether the addition was successful
     */
    public boolean addItemStack(ItemStack itemstack) {
        Integer i = emptyCases.poll();
        if (i == null)
            return false;
        else {
            super.setItem(i, itemstack);
            return true;
        }
    }

    public int firstFree() {
        Integer firstFree = emptyCases.poll();
        return firstFree == null ? -1 : firstFree;
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        emptyCases.remove(i);
        super.setItem(i, itemstack);
    }

    public void emptyChest() {
        for (int i = 0; i < this.getContents().length; i++)
            this.getContents()[i] = null;
        initEmptyCases();
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        ItemStack toReturn = super.splitStack(i, j);
        if (toReturn != null) {
            ItemStack afterSuper[] = this.getContents();
            if (afterSuper[i] == null)
                emptyCases.add(i);
        }

        return toReturn;
    }

    /**
     * @param i
     * @param j
     * @deprecated
     */
    public ItemStack a(int i, int j) {
        if (this.getContents()[i] != null) {
            ItemStack itemstack;

            if (this.getContents()[i].count <= j) {
                itemstack = this.getContents()[i];
                this.getContents()[i] = null;
                emptyCases.add(i);
                this.update();
                return itemstack;
            } else {
                itemstack = this.getContents()[i].a(j);
                if (this.getContents()[i].count == 0) {
                    this.getContents()[i] = null;
                    emptyCases.add(i);
                }
                this.update();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    public void removeItemStack(int i) {
        if (i >= 0 && i <= getSize()) {
            super.setItem(i, null);
            emptyCases.add(i);
        }
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public boolean a_(EntityHuman entityhuman) {
/*
* For this proof of concept, we ALWAYS validate the chest. This
* behavior has not been thoroughly tested, and may cause unexpected
* results depending on the state of the player.
*
* Depending on your purposes, you might want to change this. It would
* likely be preferable to enforce your business logic outside of this
* file instead, however.
*/
        return true;
    }

}
