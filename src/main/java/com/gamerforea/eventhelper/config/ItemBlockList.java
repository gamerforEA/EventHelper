package com.gamerforea.eventhelper.config;

import com.gamerforea.eventhelper.EventHelperMod;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.IntSupplier;

public final class ItemBlockList
{
	private static final String[] DEFAULT_VALUES = { "minecraft:bedrock", "modid:block_name@meta" };
	private static final char SEPARATOR = '@';
	private static final int ALL_META = -1;

	private final Set<String> rawSet = new HashSet<>();
	private final Map<Item, IntSet> items = new HashMap<>();
	private final Map<Block, IntSet> blocks = new HashMap<>();
	private boolean loaded = true;

	public ItemBlockList()
	{
		this(false);
	}

	public ItemBlockList(boolean initWithDefaultValues)
	{
		if (initWithDefaultValues)
			this.addRaw(Arrays.asList(DEFAULT_VALUES));
	}

	public void clear()
	{
		this.loaded = true;
		this.items.clear();
		this.blocks.clear();
		this.rawSet.clear();
	}

	public Set<String> getRaw()
	{
		return Collections.unmodifiableSet(this.rawSet);
	}

	public void addRaw(@Nonnull Collection<String> strings)
	{
		this.loaded = false;
		this.items.clear();
		this.blocks.clear();
		this.rawSet.addAll(strings);
	}

	public boolean isEmpty()
	{
		return this.items.isEmpty() && this.blocks.isEmpty();
	}

	public boolean contains(@Nullable ItemStack stack)
	{
		return stack != null && this.contains(stack.getItem(), stack.getItemDamage());
	}

	public boolean contains(@Nonnull Item item, int meta)
	{
		this.load();
		return item instanceof ItemBlock && this.contains(((ItemBlock) item).getBlock(), meta) || contains(this.items, item, meta);
	}

	public boolean contains(@Nonnull IBlockState blockState)
	{
		this.load();
		Block block = blockState.getBlock();
		// Lazy meta getter for better performance
		return contains(this.blocks, block, () -> {
			int meta = 0;
			try
			{
				meta = block.getMetaFromState(blockState);
			}
			catch (Throwable ignored)
			{
			}
			return meta;
		});
	}

	public boolean contains(@Nonnull Block block, int meta)
	{
		this.load();
		return contains(this.blocks, block, meta);
	}

	private void load()
	{
		if (!this.loaded)
		{
			this.loaded = true;

			RegistryNamespaced<ResourceLocation, Item> itemRegistry = Item.REGISTRY;
			RegistryNamespacedDefaultedByKey<ResourceLocation, Block> blockRegistry = Block.REGISTRY;

			for (String s : this.rawSet)
			{
				s = s.trim();
				if (!s.isEmpty())
				{
					String[] parts = StringUtils.split(s, SEPARATOR);
					if (parts != null && parts.length > 0)
					{
						String name = parts[0];
						int meta = parts.length > 1 ? safeParseInt(parts[1]) : ALL_META;
						ResourceLocation resourceLocation = new ResourceLocation(name);
						Item item = itemRegistry.getObject(resourceLocation);
						if (item != null)
							put(this.items, item, meta);
						Block block = blockRegistry.getObject(resourceLocation);
						if (block != Blocks.AIR)
							put(this.blocks, block, meta);

						if (EventHelperMod.debug && item == null && block == Blocks.AIR)
							EventHelperMod.LOGGER.warn("Item/block {} not found", resourceLocation);
					}
				}
			}
		}
	}

	private static <K> boolean put(Map<K, IntSet> map, K key, int value)
	{
		IntSet set = map.get(key);
		if (set == null)
			map.put(key, set = new IntOpenHashSet());
		return set.add(value);
	}

	private static <K> boolean contains(Map<K, IntSet> map, K key, int value)
	{
		IntSet set = map.get(key);
		return set != null && (set.contains(ALL_META) || set.contains(value));
	}

	private static <K> boolean contains(Map<K, IntSet> map, K key, IntSupplier valueSupplier)
	{
		IntSet set = map.get(key);
		return set != null && (set.contains(ALL_META) || set.contains(valueSupplier.getAsInt()));
	}

	private static int safeParseInt(String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (Throwable throwable)
		{
			return ALL_META;
		}
	}
}
