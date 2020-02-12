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
import java.util.Objects;
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
		return this.cantInteract(player, new BlockInteractParams(hand, targetPos, targetSide));
	}

	default boolean cantInteract(
			@Nonnull EntityPlayer player,
			@Nonnull EnumHand hand,
			@Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide)
	{
		return this.cantInteract(player, new BlockInteractParams(hand, targetPos, targetSide).setInteractionPos(interactionPos));
	}

	boolean cantInteract(@Nonnull EntityPlayer player, @Nonnull BlockInteractParams params);

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

	final class BlockInteractParams
	{
		@Nonnull
		private final EnumHand hand;
		@Nonnull
		private final BlockPos targetPos;
		@Nonnull
		private final EnumFacing targetSide;

		@Nonnull
		private BlockPos interactionPos;
		@Nonnull
		private BlockInteractAction action = BlockInteractAction.RIGHT_CLICK;

		public BlockInteractParams(@Nonnull EnumHand hand, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide)
		{
			this.hand = hand;
			this.targetPos = targetPos;
			this.interactionPos = targetPos;
			this.targetSide = targetSide;
		}

		@Nonnull
		public EnumHand getHand()
		{
			return this.hand;
		}

		@Nonnull
		public BlockPos getTargetPos()
		{
			return this.targetPos;
		}

		@Nonnull
		public EnumFacing getTargetSide()
		{
			return this.targetSide;
		}

		@Nonnull
		public BlockPos getInteractionPos()
		{
			return this.interactionPos;
		}

		public BlockInteractParams setInteractionPos(@Nonnull BlockPos interactionPos)
		{
			this.interactionPos = interactionPos;
			return this;
		}

		@Nonnull
		public BlockInteractAction getAction()
		{
			return this.action;
		}

		public BlockInteractParams setAction(@Nonnull BlockInteractAction action)
		{
			this.action = action;
			return this;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || this.getClass() != o.getClass())
				return false;
			BlockInteractParams that = (BlockInteractParams) o;
			return this.hand == that.hand && this.targetPos.equals(that.targetPos) && this.targetSide == that.targetSide && this.interactionPos.equals(that.interactionPos) && this.action == that.action;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(this.hand, this.targetPos, this.targetSide, this.interactionPos, this.action);
		}

		@Override
		public String toString()
		{
			return "BlockInteractParams{hand=" + this.hand + ", targetPos=" + this.targetPos + ", targetSide=" + this.targetSide + ", interactionPos=" + this.interactionPos + ", action=" + this.action + '}';
		}
	}

	enum BlockInteractAction
	{
		RIGHT_CLICK,
		LEFT_CLICK
	}
}
