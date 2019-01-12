package com.gamerforea.eventhelper.fake;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public final class FakePlayerContainerWorld extends FakePlayerContainer
{
	private final World world;

	public FakePlayerContainerWorld(@Nonnull FakePlayerContainer fake, @Nonnull World world)
	{
		super(fake);
		this.world = world;
	}

	public FakePlayerContainerWorld(@Nonnull GameProfile modFakeProfile, @Nonnull World world)
	{
		super(modFakeProfile);
		this.world = world;
	}

	@Override
	@Nonnull
	public final World getWorld()
	{
		return this.world;
	}
}