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
		if(i >= upperChest.getSize()) {
			return lowerChest.getItem(i - upperChest.getSize());
		} else {
			return upperChest.getItem(i);
		}
	}

	public ItemStack splitStack(int i, int j) {
		return null;
	}

	public void setItem(int i, ItemStack itemstack) {
		if(i >= upperChest.getSize()) {
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

	public org.bukkit.inventory.ItemStack[] getUpperContents() {
		return toBukkitItemStack(upperChest.getContents());
	}

	private org.bukkit.inventory.ItemStack[] toBukkitItemStack(ItemStack[] contents) {
		org.bukkit.inventory.ItemStack[] bukkitContents = new org.bukkit.inventory.ItemStack[contents.length];
		for(int i = 0; i < contents.length; i++) {
			if(contents[i] == null) {
				bukkitContents[i] = null;
			} else {
				bukkitContents[i] = new org.bukkit.inventory.ItemStack(contents[i].id, contents[i].count);
			}
		}
		return bukkitContents;
	}

	public org.bukkit.inventory.ItemStack[] getLowerContents() {
		return toBukkitItemStack(lowerChest.getContents());
	}

	public boolean a_(EntityHuman eh) {
		return true;
	}

	public int count() {
		ItemStack[] contents = getContents();
		int count = 0;
		for(ItemStack content : contents) {
			if(content != null) {
				if(content.count == 0) {
					count++;
				} else {
					count += content.count;
				}
			}
		}
		System.out.print("countItemsChest " + count);
		return count;
	}

}
