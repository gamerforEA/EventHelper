package com.gamerforea.eventhelper.fake;

import java.lang.ref.WeakReference;
import java.util.UUID;

import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.eventhelper.util.FastUtils;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public abstract class FakePlayerContainer
{
	private final GameProfile modFakeProfile;
	private FakePlayer modFake;

	private GameProfile profile;
	private FakePlayer fakePlayer;

	private WeakReference<EntityPlayer> realPlayer;

	protected FakePlayerContainer(GameProfile modFakeProfile)
	{
		this.modFakeProfile = modFakeProfile;
	}

	public abstract World getWorld();

	public final EntityPlayer getPlayer()
	{
		if (this.realPlayer != null)
		{
			EntityPlayer p = this.realPlayer.get();
			if (p == null)
				this.realPlayer = null;
			else
				return p;
		}

		return this.getFakePlayer();
	}

	public final FakePlayer getFakePlayer()
	{
		if (this.fakePlayer != null)
			return FastUtils.getFake(this.getWorld(), this.fakePlayer);
		else if (this.profile != null)
			return this.fakePlayer = FastUtils.getFake(this.getWorld(), this.profile);
		else if (this.modFake != null)
			return FastUtils.getFake(this.getWorld(), this.modFake);
		else
			return this.modFake = FastUtils.getFake(this.getWorld(), this.modFakeProfile);
	}

	public final void setRealPlayer(EntityPlayer player)
	{
		if (player != null)
		{
			this.reset();
			this.profile = player.getGameProfile();
			this.realPlayer = new WeakReference<EntityPlayer>(player);
		}
	}

	public final void setParent(FakePlayerContainer container)
	{
		if (container.profile != null)
		{
			this.reset();
			this.profile = container.profile;
			this.realPlayer = container.realPlayer;
		}
	}

	public final GameProfile getProfile()
	{
		return this.profile;
	}

	public final void setProfile(GameProfile profile)
	{
		this.reset();
		this.profile = profile;
	}

	public final boolean cantBreak(BlockPos pos)
	{
		return EventUtils.cantBreak(this.getPlayer(), pos);
	}

	public final boolean cantDamage(Entity target)
	{
		return EventUtils.cantDamage(this.getPlayer(), target);
	}

	public final void writeToNBT(NBTTagCompound nbt)
	{
		if (this.profile != null)
		{
			nbt.setString("eventhelper_fakeName", this.profile.getName());
			nbt.setString("eventhelper_fakeUUID", this.profile.getId().toString());
		}
	}

	public final void readFromNBT(NBTTagCompound nbt)
	{
		this.profile = readProfile(nbt, "eventhelper_fakeName", "eventhelper_fakeUUID");
	}

	private final void reset()
	{
		this.profile = null;
		this.fakePlayer = null;
		this.realPlayer = null;
	}

	private static final GameProfile readProfile(NBTTagCompound nbt, String nameKey, String uuidKey)
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