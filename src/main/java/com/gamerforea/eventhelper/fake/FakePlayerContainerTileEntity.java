package com.gamerforea.eventhelper.fake;

import com.mojang.authlib.GameProfile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public final class FakePlayerContainerTileEntity extends FakePlayerContainer
{
	private final TileEntity tile;

	public FakePlayerContainerTileEntity(@Nonnull FakePlayerContainer fake, @Nonnull TileEntity tile)
	{
		super(fake);
		this.tile = tile;
	}

	public FakePlayerContainerTileEntity(@Nonnull GameProfile modFakeProfile, @Nonnull TileEntity tile)
	{
		super(modFakeProfile);
		this.tile = tile;
	}

	@Override
	@Nonnull
	public final World getWorld()
	{
		return this.tile.getWorld();
	}
}