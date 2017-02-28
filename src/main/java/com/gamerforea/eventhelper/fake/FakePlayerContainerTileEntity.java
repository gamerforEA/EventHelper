package com.gamerforea.eventhelper.fake;

import com.mojang.authlib.GameProfile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public final class FakePlayerContainerTileEntity extends FakePlayerContainer
{
	private final TileEntity tile;

	public FakePlayerContainerTileEntity(FakePlayer modFake, TileEntity tile)
	{
		super(modFake);
		this.tile = tile;
	}

	public FakePlayerContainerTileEntity(GameProfile modFakeProfile, TileEntity tile)
	{
		super(modFakeProfile);
		this.tile = tile;
	}

	@Override
	public final World getWorld()
	{
		return this.tile.getWorldObj();
	}
}
