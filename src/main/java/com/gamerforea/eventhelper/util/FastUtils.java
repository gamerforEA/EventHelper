package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.config.ConfigUtils;
import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public final class FastUtils
{
	@Deprecated
	@Nonnull
	public static Configuration getConfig(@Nonnull String cfgName)
	{
		return ConfigUtils.getConfig(cfgName);
	}

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
			@Nonnull World world, int x, int y, int z,
			@Nonnull Entity entity, Class<T> tileClass, Function<T, FakePlayerContainer> mapper)
	{
		if (entity instanceof EntityPlayer && world.blockExists(x, y, z))
		{
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile != null && tileClass.isInstance(tile))
			{
				FakePlayerContainer fake = mapper.apply((T) tile);
				fake.setProfile(entity);
				return true;
			}
		}
		return false;
	}

	public static boolean isOnline(@Nonnull EntityPlayer player)
	{
		if (player instanceof FakePlayer)
			return true;

		for (EntityPlayer playerOnline : (Iterable<EntityPlayer>) getServer().getConfigurationManager().playerEntityList)
		{
			if (playerOnline.equals(player))
				return true;
		}

		return false;
	}

	@Nonnull
	public static FakePlayer getFake(@Nullable World world, @Nonnull FakePlayer fake)
	{
		fake.worldObj = world == null ? getEntityWorld() : world;
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
		return entity instanceof EntityPlayer ? (EntityPlayer) entity : getFake(entity == null ? null : entity.worldObj, modFake);
	}

	@Nonnull
	public static EntityPlayer getLivingPlayer(@Nullable EntityLivingBase entity, @Nonnull GameProfile modFakeProfile)
	{
		return entity instanceof EntityPlayer ? (EntityPlayer) entity : getFake(entity == null ? null : entity.worldObj, modFakeProfile);
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
		if (entity == null)
			return getFake(getEntityWorld(), modFake);
		EntityLivingBase thrower = entity.getThrower();
		return thrower == null ? getFake(entity.worldObj, modFake) : thrower;
	}

	@Nonnull
	public static EntityLivingBase getThrower(@Nullable EntityThrowable entity, @Nonnull GameProfile modFakeProfile)
	{
		if (entity == null)
			return getFake(getEntityWorld(), modFakeProfile);
		EntityLivingBase thrower = entity.getThrower();
		return thrower == null ? getFake(entity.worldObj, modFakeProfile) : thrower;
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
