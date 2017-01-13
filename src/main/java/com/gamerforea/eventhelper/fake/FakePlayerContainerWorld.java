package com.gamerforea.eventhelper.fake;

import com.mojang.authlib.GameProfile;

import net.minecraft.world.World;

public final class FakePlayerContainerWorld extends FakePlayerContainer
{
	private final World world;

	public FakePlayerContainerWorld(GameProfile modFakeProfile, World world)
	{
		super(modFakeProfile);
		this.world = world;
	}

	@Override
	public final World getWorld()
	{
		return this.world;
	}
}