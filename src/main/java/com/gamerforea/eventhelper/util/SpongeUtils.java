package com.gamerforea.eventhelper.util;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.Direction;

import com.flowpowered.math.vector.Vector3i;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class SpongeUtils
{
	public static final Cause getCause(EntityPlayer player)
	{
		if (player instanceof Player)
			return Cause.of(NamedCause.simulated((Player) player));

		Optional<ProviderRegistration<UserStorageService>> provider = Sponge.getServiceManager().getRegistration(UserStorageService.class);
		if (provider.isPresent())
		{
			UserStorageService service = provider.get().getProvider();
			Optional<User> user = service.get(player.getGameProfile().getId());
			if (user.isPresent())
				return Cause.of(NamedCause.simulated(user.get()));
		}

		return Cause.of(NamedCause.of("EntityPlayer", player));
	}

	public static final BlockState getState(BlockType type)
	{
		BlockState.Builder builder = Sponge.getRegistry().createBuilder(BlockState.Builder.class);
		builder.blockType(type);
		return builder.build();
	}

	public static final BlockSnapshot getAirSnapshot(org.spongepowered.api.world.World world, BlockPos pos)
	{
		BlockSnapshot.Builder builder = Sponge.getRegistry().createBuilder(BlockSnapshot.Builder.class);
		builder.world(world.getProperties());
		builder.blockState(getState(BlockTypes.AIR));
		builder.position(new Vector3i(pos.getX(), pos.getY(), pos.getZ()));
		return builder.build();
	}

	public static final org.spongepowered.api.world.World getWorld(World world)
	{
		return (org.spongepowered.api.world.World) world;
	}

	public static final Direction getDirection(EnumFacing side)
	{
		switch (side)
		{
			case DOWN:
				return Direction.DOWN;
			case EAST:
				return Direction.EAST;
			case NORTH:
				return Direction.NORTH;
			case SOUTH:
				return Direction.SOUTH;
			case UP:
				return Direction.UP;
			case WEST:
				return Direction.WEST;
			default:
				return Direction.NONE;
		}
	}

	public static final HandType getHandType(EnumHand hand)
	{
		switch (hand)
		{
			case MAIN_HAND:
				return HandTypes.MAIN_HAND;
			case OFF_HAND:
				return HandTypes.OFF_HAND;
			default:
				return HandTypes.MAIN_HAND;
		}
	}
}
