package com.gamerforea.eventhelper.fake;

import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.eventhelper.util.ExplosionByPlayer;
import com.gamerforea.eventhelper.util.FastUtils;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public abstract class FakePlayerContainer
{
	private static final String NBT_OWNER_NAME = "eventhelper_fakeName";
	private static final String NBT_OWNER_UUID = "eventhelper_fakeUUID";
	private final GameProfile modFakeProfile;
	private FakePlayer modFake;

	@Nullable
	public GameProfile profile;
	private FakePlayer player;

	private WeakReference<EntityPlayer> realPlayer;

	protected FakePlayerContainer(@Nonnull FakePlayer modFake)
	{
		this(modFake.getGameProfile());
		this.modFake = modFake;
	}

	protected FakePlayerContainer(@Nonnull FakePlayerContainer fake)
	{
		this(fake.modFakeProfile);
		this.modFake = fake.modFake;
		this.setParent(fake);
	}

	protected FakePlayerContainer(@Nonnull GameProfile modFakeProfile)
	{
		Preconditions.checkArgument(modFakeProfile.isComplete(), "modFakeProfile is incomplete");
		this.modFakeProfile = modFakeProfile;
	}

	public abstract World getWorld();

	public final EntityPlayer get()
	{
		if (this.realPlayer != null)
		{
			EntityPlayer p = this.realPlayer.get();
			if (p == null || p instanceof EntityPlayerMP && ((EntityPlayerMP) p).playerNetServerHandler == null)
				this.realPlayer = null;
			else
				return p;
		}

		return this.getPlayer();
	}

	public final FakePlayer getPlayer()
	{
		if (this.player != null)
			return FastUtils.getFake(this.getWorld(), this.player);
		if (this.profile != null)
			return this.player = FastUtils.getFake(this.getWorld(), this.profile);
		if (this.modFake != null)
			return FastUtils.getFake(this.getWorld(), this.modFake);
		return this.modFake = FastUtils.getFake(this.getWorld(), this.modFakeProfile);
	}

	public final void setRealPlayer(@Nullable Entity entity)
	{
		this.setRealPlayer(entity instanceof EntityPlayer ? (EntityPlayer) entity : null);
	}

	public final void setRealPlayer(@Nullable EntityPlayer player)
	{
		this.reset();
		if (player != null)
		{
			this.setProfile(player);
			if (this.profile != null && !(player instanceof FakePlayer))
				this.realPlayer = new WeakReference<>(player);
		}
	}

	public final void setParent(@Nullable FakePlayerContainer container)
	{
		this.reset();
		if (container != null && container.profile != null)
		{
			this.profile = container.profile;
			this.player = container.player;
			this.realPlayer = container.realPlayer;
		}
	}

	@Nullable
	public final GameProfile getProfile()
	{
		return this.profile;
	}

	public final void setProfile(@Nullable Entity entity)
	{
		this.setProfile(entity instanceof EntityPlayer ? (EntityPlayer) entity : null);
	}

	public final void setProfile(@Nullable EntityPlayer player)
	{
		this.setProfile(player == null ? null : player.getGameProfile());
	}

	public final void setProfile(@Nullable GameProfile profile)
	{
		this.reset();
		this.profile = profile == null || !profile.isComplete() ? null : profile;
	}

	public final boolean cantBreak(int x, int y, int z)
	{
		return EventUtils.cantBreak(this.get(), x, y, z);
	}

	public final boolean cantBreak(double x, double y, double z)
	{
		return EventUtils.cantBreak(this.get(), x, y, z);
	}

	public final boolean cantDamage(@Nonnull Entity target)
	{
		return EventUtils.cantDamage(this.get(), target);
	}

	@Nonnull
	public final ExplosionByPlayer createExplosion(
			@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isSmoking)
	{
		return ExplosionByPlayer.createExplosion(this, this.getWorld(), entityIn, x, y, z, strength, isSmoking);
	}

	@Nonnull
	public final ExplosionByPlayer newExplosion(@Nullable
														Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
	{
		return ExplosionByPlayer.newExplosion(this, this.getWorld(), entityIn, x, y, z, strength, isFlaming, isSmoking);
	}

	private void reset()
	{
		this.profile = null;
		this.player = null;
		this.realPlayer = null;
	}

	public final void writeToNBT(NBTTagCompound nbt)
	{
		if (this.profile != null)
		{
			nbt.setString(NBT_OWNER_NAME, this.profile.getName());
			nbt.setString(NBT_OWNER_UUID, this.profile.getId().toString());
		}
	}

	public final void readFromNBT(NBTTagCompound nbt)
	{
		this.profile = readProfile(nbt, NBT_OWNER_NAME, NBT_OWNER_UUID);
		if (this.profile == null)
			this.profile = readProfile(nbt, "ownerName", "ownerUUID");
	}

	private static GameProfile readProfile(NBTTagCompound nbt, String nameKey, String uuidKey)
	{
		String name = nbt.getString(nameKey);
		if (!name.isEmpty())
		{
			String uuid = nbt.getString(uuidKey);
			if (!uuid.isEmpty())
				return new GameProfile(UUID.fromString(uuid), name);
		}

		return null;
	}
}
