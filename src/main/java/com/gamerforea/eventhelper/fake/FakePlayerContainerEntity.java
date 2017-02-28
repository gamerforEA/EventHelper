package com.gamerforea.eventhelper.fake;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public final class FakePlayerContainerEntity extends FakePlayerContainer
{
	private final Entity entity;

	public FakePlayerContainerEntity(FakePlayer modFake, Entity entity)
	{
		super(modFake);
		this.entity = entity;
	}

	public FakePlayerContainerEntity(GameProfile modFakeProfile, Entity entity)
	{
		super(modFakeProfile);
		this.entity = entity;
	}

	@Override
	public final World getWorld()
	{
		return this.entity.worldObj;
	}
}
