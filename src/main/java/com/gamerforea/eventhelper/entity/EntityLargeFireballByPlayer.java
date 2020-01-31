package com.gamerforea.eventhelper.entity;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.gamerforea.eventhelper.fake.FakePlayerContainerEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@Deprecated
public class EntityLargeFireballByPlayer extends EntityLargeFireball
{
	public final FakePlayerContainer fake;

	public EntityLargeFireballByPlayer(@Nonnull FakePlayerContainer fake, @Nonnull World world)
	{
		super(world);
		this.fake = new FakePlayerContainerEntity(fake, this);
	}

	@SideOnly(Side.CLIENT)
	public EntityLargeFireballByPlayer(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world, double x, double y, double z, double accelX, double accelY, double accelZ)
	{
		super(world, x, y, z, accelX, accelY, accelZ);
		this.fake = new FakePlayerContainerEntity(fake, this);
	}

	public EntityLargeFireballByPlayer(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world, @Nonnull EntityLivingBase shooter, double accelX, double accelY, double accelZ)
	{
		super(world, shooter, accelX, accelY, accelZ);
		this.fake = new FakePlayerContainerEntity(fake, this);
	}

	@Override
	protected void onImpact(RayTraceResult result)
	{
		if (!this.world.isRemote)
		{
			if (result.entityHit != null && !this.fake.cantAttack(result.entityHit))
			{
				if (result.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 6))
					this.applyEnchantments(this.shootingEntity, result.entityHit);
			}

			boolean allowGriefing = ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
			this.fake.newExplosion(null, this.posX, this.posY, this.posZ, (float) this.explosionPower, allowGriefing, allowGriefing);
			this.setDead();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		this.fake.writeToNBT(compound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		this.fake.readFromNBT(compound);
	}
}
