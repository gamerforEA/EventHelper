package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public final class FastUtils
{
	public static void stopPotionEffect(@Nonnull EntityLivingBase entity, @Nonnull Potion potion)
	{
		stopPotionEffect(entity.getActivePotionEffect(potion));
	}

	public static void stopPotionEffect(@Nullable PotionEffect potionEffect)
	{
		if (potionEffect != null && potionEffect.getDuration() > 0)
			ReflectionHelper.setPrivateValue(PotionEffect.class, potionEffect, 0, "field_76460_b", "duration");
	}

	public static <T extends TileEntity> boolean setProfile(
			@Nonnull World world,
			@Nonnull BlockPos pos, @Nonnull Entity entity, Class<T> tileClass, Function<T, FakePlayerContainer> mapper)
	{
		if (world.isBlockLoaded(pos))
		{
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tileClass.isInstance(tile))
			{
				FakePlayerContainer fake = mapper.apply((T) tile);
				return fake.setProfile(entity);
			}
		}
		return false;
	}

	@Deprecated
	public static boolean isOnline(@Nonnull EntityPlayer player)
	{
		if (player instanceof FakePlayer)
			return true;

		for (EntityPlayer playerOnline : getServer().getPlayerList().getPlayers())
		{
			if (playerOnline.equals(player))
				return true;
		}

		return false;
	}

	public static boolean isValidRealPlayer(@Nullable EntityPlayer player)
	{
		return isValidRealPlayer(player, true);
	}

	public static boolean isValidRealPlayer(@Nullable EntityPlayer player, boolean checkAlive)
	{
		if (player == null || player instanceof FakePlayer)
			return false;

		if (player instanceof EntityPlayerMP)
		{
			NetHandlerPlayServer connection = ((EntityPlayerMP) player).connection;
			if (connection == null || !connection.netManager.isChannelOpen())
				return false;
		}

		return !checkAlive || player.isEntityAlive();
	}

	@Nonnull
	public static FakePlayer getFake(@Nullable World world, @Nonnull FakePlayer fake)
	{
		fake.world = world == null ? getEntityWorld() : world;
		return fake;
	}

	@Nonnull
	public static FakePlayer getFake(@Nullable World world, @Nonnull GameProfile profile)
	{
		return getFake(world, FakePlayerFactory.get((WorldServer) (world == null ? getEntityWorld() : world), profile));
	}

	@Nonnull
	public static EntityPlayer getLivingPlayer(@Nullable EntityLivingBase entity, @Nonnull FakePlayer modFake)
	{
		return entity instanceof EntityPlayer ? (EntityPlayer) entity : getFake(entity == null ? null : entity.world, modFake);
	}

	@Nonnull
	public static EntityPlayer getLivingPlayer(@Nullable EntityLivingBase entity, @Nonnull GameProfile modFakeProfile)
	{
		return entity instanceof EntityPlayer ? (EntityPlayer) entity : getFake(entity == null ? null : entity.world, modFakeProfile);
	}

	@Nonnull
	public static EntityPlayer getThrowerPlayer(@Nullable EntityThrowable entity, @Nonnull FakePlayer modFake)
	{
		return getLivingPlayer(entity == null ? null : entity.getThrower(), modFake);
	}

	@Nonnull
	public static EntityPlayer getThrowerPlayer(@Nullable EntityThrowable entity, @Nonnull GameProfile modFakeProfile)
	{
		return getLivingPlayer(entity == null ? null : entity.getThrower(), modFakeProfile);
	}

	@Nonnull
	public static EntityLivingBase getThrower(@Nullable EntityThrowable entity, @Nonnull FakePlayer modFake)
	{
		EntityLivingBase thrower = entity == null ? null : entity.getThrower();
		return thrower == null ? getFake(entity == null ? null : entity.world, modFake) : thrower;
	}

	@Nonnull
	public static EntityLivingBase getThrower(@Nullable EntityThrowable entity, @Nonnull GameProfile modFakeProfile)
	{
		EntityLivingBase thrower = entity == null ? null : entity.getThrower();
		return thrower == null ? getFake(entity == null ? null : entity.world, modFakeProfile) : thrower;
	}

	@Nonnull
	private static MinecraftServer getServer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}

	@Nonnull
	private static World getEntityWorld()
	{
		return getServer().getEntityWorld();
	}
}