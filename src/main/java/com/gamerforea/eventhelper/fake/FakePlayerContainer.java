package com.gamerforea.eventhelper.fake;

import java.lang.ref.WeakReference;
import java.util.UUID;

import com.gamerforea.eventhelper.util.EventUtils;
import com.gamerforea.eventhelper.util.FastUtils;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public abstract class FakePlayerContainer
{
	private final GameProfile modFakeProfile;
	private FakePlayer modFake;

	public GameProfile profile;
	private FakePlayer player;

	private WeakReference<EntityPlayer> realPlayer;

	protected FakePlayerContainer(FakePlayer modFake)
	{
		this.modFakeProfile = modFake.getGameProfile();
		this.modFake = modFake;
	}

	protected FakePlayerContainer(GameProfile modFakeProfile)
	{
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
		else if (this.profile != null)
			return this.player = FastUtils.getFake(this.getWorld(), this.profile);
		else if (this.modFake != null)
			return FastUtils.getFake(this.getWorld(), this.modFake);
		else
			return this.modFake = FastUtils.getFake(this.getWorld(), this.modFakeProfile);
	}

	public final void setRealPlayer(EntityPlayer player)
	{
		this.reset();
		if (player != null)
		{
			this.profile = player.getGameProfile();
			if (!(player instanceof FakePlayer))
				this.realPlayer = new WeakReference<EntityPlayer>(player);
		}
	}

	public final void setParent(FakePlayerContainer container)
	{
		this.reset();
		if (container.profile != null)
		{
			this.profile = container.profile;
			this.player = container.player;
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

	public final boolean cantBreak(int x, int y, int z)
	{
		return EventUtils.cantBreak(this.get(), x, y, z);
	}

	public final boolean cantDamage(Entity target)
	{
		return EventUtils.cantDamage(this.get(), target);
	}

	private final void reset()
	{
		this.profile = null;
		this.player = null;
		this.realPlayer = null;
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
		if (this.profile == null)
			this.profile = readProfile(nbt, "ownerName", "ownerUUID");
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
