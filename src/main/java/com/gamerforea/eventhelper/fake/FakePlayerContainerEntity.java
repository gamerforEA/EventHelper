package com.gamerforea.eventhelper.fake;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public final class FakePlayerContainerEntity extends FakePlayerContainer
{
	private final Entity entity;

	public FakePlayerContainerEntity(@Nonnull FakePlayerContainer fake, @Nonnull Entity entity)
	{
		super(fake);
		this.entity = entity;
		this.setRealPlayer(entity);
	}

	public FakePlayerContainerEntity(@Nonnull GameProfile modFakeProfile, @Nonnull Entity entity)
	{
		super(modFakeProfile);
		this.entity = entity;
		this.setRealPlayer(entity);
	}

	@Override
	@Nonnull
	public final World getWorld()
	{
		return this.entity.world;
	}
}