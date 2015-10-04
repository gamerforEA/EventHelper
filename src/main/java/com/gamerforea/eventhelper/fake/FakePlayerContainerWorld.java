package com.gamerforea.eventhelper.fake;

import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public final class FakePlayerContainerWorld extends FakePlayerContainer
{
	private final World world;

	public FakePlayerContainerWorld(FakePlayer modFake, World world)
	{
		super(modFake);
		this.world = world;
	}

	@Override
	public final World getWorld()
	{
		return this.world;
	}
}