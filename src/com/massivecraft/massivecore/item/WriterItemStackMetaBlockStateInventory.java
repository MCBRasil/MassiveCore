package com.massivecraft.massivecore.item;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

// NOTE: We take ItemStack[] (inventory contents) instead of Inventories to accommodate the different sizes of inventories better.
// It is not possible to create an inventory with less then 9 slots, but hoppers or brewing stands only have 5.
// By using arrays, we avoid huge efforts to retain inventory order, because that happens relatively naturally.
public class WriterItemStackMetaBlockStateInventory extends WriterAbstractItemStackMetaField<BlockStateMeta, Map<Integer, DataItemStack>, ItemStack[]>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static final WriterItemStackMetaBlockStateInventory i = new WriterItemStackMetaBlockStateInventory();
	public static WriterItemStackMetaBlockStateInventory get() { return i; }
	public WriterItemStackMetaBlockStateInventory()
	{
		super(BlockStateMeta.class);
		this.setMaterial(Material.CHEST);
		this.setConverterTo(ConverterToInventoryContents.get());
		this.setConverterFrom(ConverterFromInventoryContents.get());
	}
	
	// -------------------------------------------- //
	// ACCESS
	// -------------------------------------------- //

	@Override
	public Map<Integer, DataItemStack> getA(DataItemStack ca, ItemStack d)
	{
		return ca.getBlockStateInventory();
	}

	@Override
	public void setA(DataItemStack ca, Map<Integer, DataItemStack> fa, ItemStack d)
	{
		ca.setBlockStateInventory(fa);
	}

	@Override
	public ItemStack[] getB(BlockStateMeta cb, ItemStack d)
	{
		// Null
		if (cb == null) return null;
		
		// Creative
		if (!cb.hasBlockState()) return null;
		
		// Try
		try
		{
			BlockState ret = cb.getBlockState();
			if (!(ret instanceof InventoryHolder)) return null;
			return ((InventoryHolder) ret).getInventory().getContents();
		}
		catch (Exception e)
		{
			// Catch errors such as: throw new IllegalStateException("Missing blockState for " + material);
			return null;
		}
	}

	@Override
	public void setB(BlockStateMeta blockStateMeta, ItemStack[] storedInventory, ItemStack d)
	{
		// Null
		if (blockStateMeta == null) return;
		if (storedInventory == null || storedInventory.length == 0) return;
		
		// Try
		BlockState ret;
		Inventory inventory;
		
		try
		{
			ret = blockStateMeta.getBlockState();
			if (!(ret instanceof InventoryHolder)) return;
			inventory = ((InventoryHolder)ret).getInventory();
		}
		catch (Exception e)
		{
			// Catch errors such as: throw new IllegalStateException("Missing blockState for " + material);
			return;
		}
		
		// Apply
		inventory.setContents(storedInventory);
		
		// Set
		blockStateMeta.setBlockState(ret);
	}
	
}
