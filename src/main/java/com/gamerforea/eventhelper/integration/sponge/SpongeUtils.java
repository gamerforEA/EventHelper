package com.gamerforea.eventhelper.integration.sponge;

import com.flowpowered.math.vector.Vector3i;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.Direction;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public final class SpongeUtils
{
	@Nonnull
	public static Cause getCause(@Nonnull EntityPlayer player)
	{
		return getCause(player, EventContext.empty());
	}

	@Nonnull
	public static Cause getCause(@Nonnull EntityPlayer player, @Nonnull EventContext context)
	{
		if (player instanceof Player)
			return Cause.of(context, player);
		Optional<User> user = getUser(player);
		return Cause.of(context, user.isPresent() ? user.get() : player);
	}

	@Nonnull
	public static Optional<User> getUser(@Nonnull EntityPlayer player)
	{
		if (player instanceof User)
			return Optional.of((User) player);
		return getUser(player.getGameProfile().getId());
	}

	@Nonnull
	public static Optional<User> getUser(@Nonnull UUID playerId)
	{
		Optional<UserStorageService> provider = Sponge.getServiceManager().getRegistration(UserStorageService.class).map(ProviderRegistration::getProvider);
		return provider.flatMap(service -> service.get(playerId));
	}

	@Nonnull
	public static Optional<User> getUser(@Nonnull String playerName)
	{
		Optional<UserStorageService> provider = Sponge.getServiceManager().getRegistration(UserStorageService.class).map(ProviderRegistration::getProvider);
		return provider.flatMap(service -> service.get(playerName));
	}

	@Nonnull
	public static BlockState getState(@Nonnull BlockType type)
	{
		return Sponge.getRegistry().createBuilder(BlockState.Builder.class).blockType(type).build();
	}

	@Nonnull
	public static BlockState getState(@Nonnull IBlockState blockState)
	{
		return unsafeCast(blockState);
	}

	@Nonnull
	public static BlockSnapshot getAirSnapshot(@Nonnull org.spongepowered.api.world.World world, @Nonnull BlockPos pos)
	{
		return getBlockSnapshot(world, pos, Blocks.AIR.getDefaultState());
	}

	@Nonnull
	public static BlockSnapshot getBlockSnapshot(
			@Nonnull org.spongepowered.api.world.World world, @Nonnull BlockPos pos, @Nonnull IBlockState blockState)
	{
		BlockSnapshot.Builder builder = Sponge.getRegistry().createBuilder(BlockSnapshot.Builder.class);
		builder.world(world.getProperties());
		builder.blockState(getState(blockState));
		builder.position(new Vector3i(pos.getX(), pos.getY(), pos.getZ()));
		return builder.build();
	}

	@Nonnull
	public static org.spongepowered.api.world.World getWorld(@Nonnull World world)
	{
		return unsafeCast(world);
	}

	@Nonnull
	public static Direction getDirection(@Nonnull EnumFacing side)
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

	@Nonnull
	public static HandType getHandType(@Nonnull EnumHand hand)
	{
		// return hand == EnumHand.MAIN_HAND ? HandTypes.MAIN_HAND : HandTypes.OFF_HAND;
		return unsafeCast(hand);
	}

	@SuppressWarnings("unchecked")
	private static <T> T unsafeCast(Object object)
	{
		return (T) object;
	}
}
