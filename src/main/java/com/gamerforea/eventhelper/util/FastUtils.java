package com.gamerforea.eventhelper.util;

import java.io.File;
import java.util.List;

import com.gamerforea.eventhelper.EventHelper;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public final class FastUtils
{
	public static final Configuration getConfig(String cfgName)
	{
		Configuration cfg = new Configuration(new File(EventHelper.cfgDir, cfgName + ".cfg"));
		cfg.load();
		return cfg;
	}

	public static final boolean isOnline(EntityPlayer player)
	{
		if (player instanceof FakePlayer)
			return true;

		List<EntityPlayer> playersOnline = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList;
		for (EntityPlayer playerOnline : playersOnline)
			if (playerOnline.equals(player))
				return true;

		return false;
	}

	public static final FakePlayer getFake(World world, FakePlayer fake)
	{
		fake.worldObj = world;
		return fake;
	}

	public static final FakePlayer getFake(World world, GameProfile profile)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return getFake(world, FakePlayerFactory.get((WorldServer) (world == null ? server.getEntityWorld() : world), profile));
	}

	public static final EntityPlayer getThrowerPlayer(EntityThrowable entity, FakePlayer modFake)
	{
		EntityLivingBase thrower = entity.getThrower();
		return thrower instanceof EntityPlayer ? (EntityPlayer) thrower : getFake(entity.worldObj, modFake);
	}

	public static final EntityPlayer getThrowerPlayer(EntityThrowable entity, GameProfile modFakeProfile)
	{
		EntityLivingBase thrower = entity.getThrower();
		return thrower instanceof EntityPlayer ? (EntityPlayer) thrower : getFake(entity.worldObj, modFakeProfile);
	}

	public static final EntityLivingBase getThrower(EntityThrowable entity, FakePlayer modFake)
	{
		EntityLivingBase thrower = entity.getThrower();
		return thrower != null ? thrower : getFake(entity.worldObj, modFake);
	}

	public static final EntityLivingBase getThrower(EntityThrowable entity, GameProfile modFakeProfile)
	{
		EntityLivingBase thrower = entity.getThrower();
		return thrower != null ? thrower : getFake(entity.worldObj, modFakeProfile);
	}
}