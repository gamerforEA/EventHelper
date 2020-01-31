package com.gamerforea.eventhelper.entity;

import com.gamerforea.eventhelper.ModConstants;
import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

@Deprecated
@Mod.EventBusSubscriber(modid = ModConstants.MODID)
public class EntityLightningBoltByPlayer extends EntityLightningBolt
{
	public final FakePlayerContainer fake;

	public EntityLightningBoltByPlayer(
			@Nonnull FakePlayerContainer fake, @Nonnull World world, double x, double y, double z, boolean effectOnly)
	{
		super(world, x, y, z, true);
		this.fake = fake;

		BlockPos pos = new BlockPos(this);
		if (!effectOnly && !world.isRemote && world.getGameRules().getBoolean("doFireTick") && (world.getDifficulty() == EnumDifficulty.NORMAL || world.getDifficulty() == EnumDifficulty.HARD) && world.isAreaLoaded(pos, 10))
		{
			IBlockState fireState = Blocks.FIRE.getDefaultState();
			if (world.getBlockState(pos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(world, pos) && !fake.cantPlace(pos, fireState))
				world.setBlockState(pos, fireState);

			for (int i = 0; i < 4; ++i)
			{
				BlockPos neighborPos = pos.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);
				if (world.getBlockState(neighborPos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(world, neighborPos) && !fake.cantPlace(neighborPos, fireState))
					world.setBlockState(neighborPos, fireState);
			}
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

	@Deprecated
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityStruckByLightning(EntityStruckByLightningEvent event)
	{
		if (event.getLightning() instanceof EntityLightningBoltByPlayer)
		{
			EntityLightningBoltByPlayer lightning = (EntityLightningBoltByPlayer) event.getLightning();
			if (lightning.fake.cantAttack(event.getEntity()))
				event.setCanceled(true);
		}
	}
}
