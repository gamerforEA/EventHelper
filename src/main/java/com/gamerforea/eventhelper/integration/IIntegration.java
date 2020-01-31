package com.gamerforea.eventhelper.integration;

import com.gamerforea.eventhelper.cause.ICauseStackManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.server.FMLServerHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface IIntegration
{
	boolean cantBreak(@Nonnull EntityPlayer player, @Nonnull BlockPos pos);

	boolean cantPlace(@Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState blockState);

	boolean cantReplace(@Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState blockState);

	boolean cantAttack(@Nonnull EntityPlayer player, @Nonnull Entity victim);

	default boolean cantInteract(
			@Nonnull EntityPlayer player,
			@Nonnull EnumHand hand, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide)
	{
		return this.cantInteract(player, hand, targetPos, targetPos, targetSide);
	}

	boolean cantInteract(
			@Nonnull EntityPlayer player,
			@Nonnull EnumHand hand,
			@Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide);

	boolean hasPermission(@Nonnull EntityPlayer player, @Nonnull String permission);

	default boolean hasPermission(@Nonnull UUID playerId, @Nonnull String permission)
	{
		MinecraftServer server = FMLServerHandler.instance().getServer();
		if (server == null)
			return false;
		PlayerList playerList = server.getPlayerList();
		if (playerList == null)
			return false;
		EntityPlayer player = playerList.getPlayerByUUID(playerId);
		return player != null && this.hasPermission(player, permission);
	}

	default boolean hasPermission(@Nonnull String playerName, @Nonnull String permission)
	{
		MinecraftServer server = FMLServerHandler.instance().getServer();
		if (server == null)
			return false;
		PlayerList playerList = server.getPlayerList();
		if (playerList == null)
			return false;
		EntityPlayer player = playerList.getPlayerByUsername(playerName);
		return player != null && this.hasPermission(player, permission);
	}

	@Nullable
	default ICauseStackManager getSpecificCauseStackManager()
	{
		return null;
	}
}
