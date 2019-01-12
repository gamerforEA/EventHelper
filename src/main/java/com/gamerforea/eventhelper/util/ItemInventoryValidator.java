package com.gamerforea.eventhelper.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ItemInventoryValidator
{
	public static final String NBT_KEY_ID = "UID";
	private static final Random RANDOM = new Random();

	private final ItemStack stack;
	private final String nbtIdKey;
	private final Predicate<Item> itemValidator;
	private final Function<EntityPlayer, ItemStack> stackGetter;

	private boolean itemInHotbar = true;
	private int slotIndex = -1;
	private int slotNumber = -1;

	public ItemInventoryValidator(ItemStack stack)
	{
		this(stack, null, true);
	}

	public ItemInventoryValidator(ItemStack stack, Predicate<Item> itemValidator)
	{
		this(stack, null, true, itemValidator);
	}

	public ItemInventoryValidator(ItemStack stack, Function<EntityPlayer, ItemStack> stackGetter)
	{
		this(stack, null, true, stackGetter);
	}

	public ItemInventoryValidator(ItemStack stack, Predicate<Item> itemValidator, Function<EntityPlayer, ItemStack> stackGetter)
	{
		this(stack, null, true, itemValidator, stackGetter);
	}

	public ItemInventoryValidator(ItemStack stack, String nbtKeyId, boolean generateIdIfAbsent)
	{
		this(stack, nbtKeyId, generateIdIfAbsent, null, null);
	}

	public ItemInventoryValidator(ItemStack stack, String nbtKeyId, boolean generateIdIfAbsent, Predicate<Item> itemValidator)
	{
		this(stack, nbtKeyId, generateIdIfAbsent, itemValidator, null);
	}

	public ItemInventoryValidator(ItemStack stack, String nbtKeyId, boolean generateIdIfAbsent, Function<EntityPlayer, ItemStack> stackGetter)
	{
		this(stack, nbtKeyId, generateIdIfAbsent, null, stackGetter);
	}

	public ItemInventoryValidator(ItemStack stack, String nbtKeyId, boolean generateIdIfAbsent, Predicate<Item> itemValidator, Function<EntityPlayer, ItemStack> stackGetter)
	{
		this.stack = stack;
		this.nbtIdKey = nbtKeyId = StringUtils.defaultIfBlank(nbtKeyId, NBT_KEY_ID);
		this.itemValidator = itemValidator;
		this.stackGetter = stackGetter;

		if (generateIdIfAbsent && stack != null && (itemValidator == null || itemValidator.test(stack.getItem())))
		{
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt == null)
			{
				nbt = new NBTTagCompound();
				stack.setTagCompound(nbt);
			}
			if (!nbt.hasKey(nbtKeyId))
				nbt.setInteger(nbtKeyId, RANDOM.nextInt());
		}
	}

	public boolean isItemInHotbar()
	{
		return this.itemInHotbar;
	}

	public void setItemInHotbar(boolean itemInHotbar)
	{
		this.itemInHotbar = itemInHotbar;
	}

	public int getSlotIndex()
	{
		return this.slotIndex;
	}

	public void setSlotIndex(int slotIndex)
	{
		this.slotIndex = slotIndex;
	}

	public void setSlotIndex(int slotIndex, boolean itemInHotbar)
	{
		this.setSlotIndex(slotIndex);
		this.setItemInHotbar(itemInHotbar);
	}

	public int getSlotNumber()
	{
		return this.slotNumber;
	}

	public void setSlotNumber(int slotNumber)
	{
		this.slotNumber = slotNumber;
	}

	public boolean tryGetSlotNumberFromPlayerSlot(Slot slot)
	{
		if (this.slotIndex >= 0 && slot.inventory instanceof InventoryPlayer && slot.getSlotIndex() == this.slotIndex)
		{
			this.setSlotNumber(slot.slotNumber);
			return true;
		}
		return false;
	}

	public boolean canInteractWith(EntityPlayer player)
	{
		if (this.itemInHotbar && this.slotIndex >= 0 && this.slotIndex != player.inventory.currentItem)
			return false;
		if (this.stack != null)
		{
			ItemStack stackToCheck;
			if (this.stackGetter == null)
			{
				if (this.slotIndex < 0)
					return true;
				stackToCheck = player.inventory.getStackInSlot(this.slotIndex);
			}
			else
				stackToCheck = this.stackGetter.apply(player);
			return stackToCheck != null && (this.itemValidator == null || this.itemValidator.test(stackToCheck.getItem())) && this.isSameItemInventory(this.stack, stackToCheck);
		}
		return true;
	}

	public boolean canSlotClick(int slot, int button, int buttonType, EntityPlayer player)
	{
		if (this.slotNumber >= 0 && slot == this.slotNumber)
			return false;
		if (buttonType == 2 && this.itemInHotbar && this.slotIndex >= 0 && button == this.slotIndex)
			return false;
		return this.canInteractWith(player);
	}

	private boolean isSameItemInventory(ItemStack base, ItemStack comparison)
	{
		if (base == null || comparison == null)
			return false;

		if (base.getItem() != comparison.getItem())
			return false;

		if (!base.hasTagCompound() || !comparison.hasTagCompound())
			return false;

		String baseUID = base.getTagCompound().getString(this.nbtIdKey);
		String comparisonUID = comparison.getTagCompound().getString(this.nbtIdKey);
		return baseUID != null && baseUID.equals(comparisonUID);
	}
}
