package com.gamerforea.eventhelper.entity;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.gamerforea.eventhelper.fake.FakePlayerContainerEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;

public class EntitySmallFireballByPlayer extends EntitySmallFireball
{
	public final FakePlayerContainer fake;

	public EntitySmallFireballByPlayer(@Nonnull FakePlayerContainer fake, @Nonnull World world)
	{
		super(world);
		this.fake = new FakePlayerContainerEntity(fake, this);
	}

	public EntitySmallFireballByPlayer(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world, @Nonnull EntityLivingBase shooter, double accelX, double accelY, double accelZ)
	{
		super(world, shooter, accelX, accelY, accelZ);
		this.fake = new FakePlayerContainerEntity(fake, this);
	}

	public EntitySmallFireballByPlayer(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world, double x, double y, double z, double accelX, double accelY, double accelZ)
	{
		super(world, x, y, z, accelX, accelY, accelZ);
		this.fake = new FakePlayerContainerEntity(fake, this);
	}

	@Override
	protected void onImpact(RayTraceResult result)
	{
		if (!this.world.isRemote)
		{
			if (result.entityHit != null)
			{
				if (!result.entityHit.isImmuneToFire() && !this.fake.cantAttack(result.entityHit))
				{
					boolean attacked = result.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5);
					if (attacked)
					{
						this.applyEnchantments(this.shootingEntity, result.entityHit);
						result.entityHit.setFire(5);
					}
				}
			}
			else
			{
				boolean allowGriefing = true;

				if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving)
					allowGriefing = ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);

				if (allowGriefing)
				{
					BlockPos pos = result.getBlockPos().offset(result.sideHit);
					if (this.world.isAirBlock(pos) && !this.fake.cantPlace(pos, Blocks.FIRE.getDefaultState()))
						this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				}
			}

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
