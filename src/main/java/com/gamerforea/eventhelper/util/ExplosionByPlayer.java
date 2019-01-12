package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExplosionByPlayer extends Explosion
{
	private final EntityPlayer player;

	public ExplosionByPlayer(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world, @Nullable
					Entity exploder, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain)
	{
		this(fake.getPlayer(), world, exploder, x, y, z, size, flaming, damagesTerrain);
	}

	public ExplosionByPlayer(
			@Nonnull EntityPlayer player,
			@Nonnull World world, @Nullable
					Entity exploder, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain)
	{
		super(world, exploder, x, y, z, size, flaming, damagesTerrain);
		this.player = player;
		ExplosionHandler.init();
	}

	@Nonnull
	public static ExplosionByPlayer createExplosion(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world,
			@Nullable Entity exploder, double x, double y, double z, float strength, boolean isSmoking)
	{
		return newExplosion(fake, world, exploder, x, y, z, strength, false, isSmoking);
	}

	@Nonnull
	public static ExplosionByPlayer createExplosion(
			@Nonnull EntityPlayer player,
			@Nonnull World world,
			@Nullable Entity exploder, double x, double y, double z, float strength, boolean isSmoking)
	{
		return newExplosion(player, world, exploder, x, y, z, strength, false, isSmoking);
	}

	@Nonnull
	public static ExplosionByPlayer newExplosion(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world, @Nullable
					Entity exploder, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
	{
		return newExplosion(new ExplosionByPlayer(fake, world, exploder, x, y, z, strength, isFlaming, isSmoking), world, x, y, z, strength, isSmoking);
	}

	@Nonnull
	public static ExplosionByPlayer newExplosion(
			@Nonnull EntityPlayer player,
			@Nonnull World world, @Nullable
					Entity exploder, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
	{
		return newExplosion(new ExplosionByPlayer(player, world, exploder, x, y, z, strength, isFlaming, isSmoking), world, x, y, z, strength, isSmoking);
	}

	@Nonnull
	private static ExplosionByPlayer newExplosion(
			@Nonnull ExplosionByPlayer explosion,
			@Nonnull World world, double x, double y, double z, float strength, boolean isSmoking)
	{
		if (ForgeEventFactory.onExplosionStart(world, explosion))
			return explosion;

		boolean isServerWorld = world instanceof WorldServer;
		explosion.doExplosionA();
		explosion.doExplosionB(!isServerWorld);

		if (isServerWorld)
		{
			if (!isSmoking)
				explosion.clearAffectedBlockPositions();

			for (EntityPlayer player : world.playerEntities)
			{
				if (player.getDistanceSq(x, y, z) < 4096)
					((EntityPlayerMP) player).connection.sendPacket(new SPacketExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(player)));
			}
		}

		return explosion;
	}

	private static final class ExplosionHandler
	{
		private static boolean initialized;

		public static void init()
		{
			if (!initialized)
			{
				initialized = true;
				MinecraftForge.EVENT_BUS.register(new ExplosionHandler());
			}
		}

		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void onDetonate(ExplosionEvent.Detonate event)
		{
			Explosion explosion = event.getExplosion();
			if (explosion instanceof ExplosionByPlayer)
			{
				ExplosionByPlayer explosionByPlayer = (ExplosionByPlayer) explosion;
				EntityPlayer player = explosionByPlayer.player;
				event.getAffectedBlocks().removeIf(pos -> EventUtils.cantBreak(player, pos));
				event.getAffectedEntities().removeIf(entity -> EventUtils.cantAttack(player, entity));
			}
		}
	}
}
