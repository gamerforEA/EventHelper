package com.gamerforea.eventhelper.integration.bukkit;

import com.gamerforea.eventhelper.EventHelperMod;
import com.gamerforea.eventhelper.integration.IIntegration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import static com.gamerforea.eventhelper.integration.bukkit.BukkitUtils.*;

public final class BukkitIntegration
{
	private static final IIntegration INTEGRATION;

	private BukkitIntegration()
	{
	}

	public static boolean isBukkitPresent()
	{
		return INTEGRATION != null;
	}

	@Nullable
	public static IIntegration getIntegration()
	{
		return INTEGRATION;
	}

	static
	{
		IIntegration integration = null;

		try
		{
			Class.forName("org.bukkit.Server");
			integration = new BukkitIntegration0();
		}
		catch (NoClassDefFoundError | ClassNotFoundException e)
		{
			if (EventHelperMod.debug)
				EventHelperMod.LOGGER.info("BukkitAPI not found", e);
			else
				EventHelperMod.LOGGER.info("BukkitAPI not found");
		}

		INTEGRATION = integration;
	}

	private static final class BukkitIntegration0 implements IIntegration
	{
		private BukkitIntegration0()
		{
		}

		@Override
		public boolean cantBreak(@Nonnull EntityPlayer player, @Nonnull BlockPos pos)
		{
			Player bukkitPlayer = getPlayer(player);
			Block block = bukkitPlayer.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
			BlockBreakEvent event = new BlockBreakEvent(block, bukkitPlayer);
			Bukkit.getPluginManager().callEvent(event);
			return event.isCancelled();
		}

		@Override
		public boolean cantAttack(@Nonnull EntityPlayer player, @Nonnull Entity victim)
		{
			Player bukkitPlayer = getPlayer(player);
			org.bukkit.entity.Entity bukkitVictim = getEntity(victim);
			EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(bukkitPlayer, bukkitVictim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0);
			Bukkit.getPluginManager().callEvent(event);
			return event.isCancelled();
		}

		@Override
		public boolean cantInteract(
				@Nonnull EntityPlayer player,
				@Nonnull EnumHand hand,
				@Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide)
		{
			Player bukkitPlayer = getPlayer(player);
			PlayerInventory inventory = bukkitPlayer.getInventory();
			ItemStack stack = (ItemStack) inventory;
			Block block = bukkitPlayer.getWorld().getBlockAt(targetPos.getX(), targetPos.getY(), targetPos.getZ());
			PlayerInteractEvent event = new PlayerInteractEvent(bukkitPlayer, Action.RIGHT_CLICK_BLOCK, stack, block, getBlockFace(targetSide));
			Bukkit.getPluginManager().callEvent(event);
			return event.isCancelled();
		}

		@Override
		public boolean hasPermission(@Nonnull EntityPlayer player, @Nonnull String permission)
		{
			return getPlayer(player).hasPermission(permission);
		}

		@Override
		public boolean hasPermission(@Nonnull UUID playerId, @Nonnull String permission)
		{
			Player player = Bukkit.getPlayer(playerId);
			return player != null && player.hasPermission(permission);
		}

		@Override
		public boolean hasPermission(@Nonnull String playerName, @Nonnull String permission)
		{
			Player player = Bukkit.getPlayerExact(playerName);
			return player != null && player.hasPermission(permission);
		}
	}
}
