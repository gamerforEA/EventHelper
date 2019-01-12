package com.gamerforea.eventhelper.fake;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;

public final class FakePlayerContainerEntity extends FakePlayerContainer
{
	private final Entity entity;

	public FakePlayerContainerEntity(@Nonnull FakePlayer modFake, @Nonnull Entity entity)
	{
		super(modFake);
		this.entity = entity;
		this.setRealPlayer(entity);
	}

	public FakePlayerContainerEntity(@Nonnull FakePlayerContainer fake, @Nonnull Entity entity)
	{
		super(fake);
		this.entity = entity;
		if (entity instanceof EntityPlayer)
			this.setRealPlayer(entity);
	}

	public FakePlayerContainerEntity(@Nonnull GameProfile modFakeProfile, @Nonnull Entity entity)
	{
		super(modFakeProfile);
		this.entity = entity;
		this.setRealPlayer(entity);
	}

	@Override
	public final World getWorld()
	{
		return this.entity.worldObj;
	}
}
