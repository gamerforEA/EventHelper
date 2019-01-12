package com.gamerforea.eventhelper.nexus;

import com.gamerforea.eventhelper.fake.FakePlayerContainerEntity;
import com.gamerforea.eventhelper.fake.FakePlayerContainerTileEntity;
import com.gamerforea.eventhelper.fake.FakePlayerContainerWorld;
import com.gamerforea.eventhelper.util.FastUtils;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;

public final class ModNexusFactory
{
	private final GameProfile modFakeProfile;

	public ModNexusFactory(@Nonnull GameProfile modFakeProfile)
	{
		Preconditions.checkArgument(modFakeProfile.isComplete(), "modFakeProfile is incomplete");
		this.modFakeProfile = modFakeProfile;
	}

	@Nonnull
	public GameProfile getProfile()
	{
		return this.modFakeProfile;
	}

	@Nonnull
	public FakePlayer getFake(@Nonnull World world)
	{
		return FastUtils.getFake(world, this.modFakeProfile);
	}

	@Nonnull
	public FakePlayerContainerEntity wrapFake(@Nonnull Entity entity)
	{
		return new FakePlayerContainerEntity(this.modFakeProfile, entity);
	}

	@Nonnull
	public FakePlayerContainerTileEntity wrapFake(@Nonnull TileEntity tile)
	{
		return new FakePlayerContainerTileEntity(this.modFakeProfile, tile);
	}

	@Nonnull
	public FakePlayerContainerWorld wrapFake(@Nonnull World world)
	{
		return new FakePlayerContainerWorld(this.modFakeProfile, world);
	}
}
