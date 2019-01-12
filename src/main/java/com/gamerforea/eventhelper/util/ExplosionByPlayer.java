package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.EventHelper;
import com.gamerforea.eventhelper.fake.FakePlayerContainer;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class ExplosionByPlayer extends Explosion
{
	private final EntityPlayer player;
	private final World world;
	private final Map<EntityPlayer, Vec3> playerKnockbackMap = new HashMap<>();

	public ExplosionByPlayer(
			@Nonnull GameProfile modFakeProfile,
			@Nullable EntityPlayer player,
			@Nonnull World world, @Nullable Entity exploder, double x, double y, double z, float size)
	{
		this(player == null ? exploder instanceof EntityPlayer ? (EntityPlayer) exploder : FastUtils.getFake(world, modFakeProfile) : player, world, exploder, x, y, z, size);
	}

	public ExplosionByPlayer(
			@Nonnull FakePlayer modFake,
			@Nullable EntityPlayer player,
			@Nonnull World world, @Nullable Entity exploder, double x, double y, double z, float size)
	{
		this(player == null ? exploder instanceof EntityPlayer ? (EntityPlayer) exploder : modFake : player, world, exploder, x, y, z, size);
	}

	public ExplosionByPlayer(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world, @Nullable Entity exploder, double x, double y, double z, float size)
	{
		this(fake.get(), world, exploder, x, y, z, size);
	}

	public ExplosionByPlayer(
			@Nonnull EntityPlayer player,
			@Nonnull World world, @Nullable Entity exploder, double x, double y, double z, float size)
	{
		super(world, exploder, x, y, z, size);
		this.world = world;
		this.player = player;
	}

	@Override
	public final void doExplosionA()
	{
		if (!EventHelper.explosions)
			return;

		this.affectedBlockPositions.addAll(this.getPositions());
		float size = this.explosionSize;
		this.explosionSize *= 2;
		int minX = MathHelper.floor_double(this.explosionX - this.explosionSize - 1);
		int maxX = MathHelper.floor_double(this.explosionX + this.explosionSize + 1);
		int minY = MathHelper.floor_double(this.explosionY - this.explosionSize - 1);
		int maxY = MathHelper.floor_double(this.explosionY + this.explosionSize + 1);
		int minZ = MathHelper.floor_double(this.explosionZ - this.explosionSize - 1);
		int maxZ = MathHelper.floor_double(this.explosionZ + this.explosionSize + 1);
		List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ));
		Vec3 vec3 = Vec3.createVectorHelper(this.explosionX, this.explosionY, this.explosionZ);

		for (Entity entity : entities)
		{
			double distance = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / this.explosionSize;

			if (distance <= 1)
			{
				double distanceX = entity.posX - this.explosionX;
				double distanceY = entity.posY + entity.getEyeHeight() - this.explosionY;
				double distanceZ = entity.posZ - this.explosionZ;
				double distance1 = MathHelper.sqrt_double(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);

				if (distance1 != 0)
				{
					if (EventUtils.cantDamage(this.player, entity))
						continue;

					distanceX /= distance1;
					distanceY /= distance1;
					distanceZ /= distance1;
					double density = this.world.getBlockDensity(vec3, entity.boundingBox);
					double d5 = (1 - distance) * density;
					entity.attackEntityFrom(DamageSource.setExplosionSource(this), (int) ((d5 * d5 + d5) / 2 * 8 * this.explosionSize + 1));
					double d6 = EnchantmentProtection.func_92092_a(entity, d5);
					entity.motionX += distanceX * d6;
					entity.motionY += distanceY * d6;
					entity.motionZ += distanceZ * d6;

					if (entity instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) entity;
						this.playerKnockbackMap.put(player, Vec3.createVectorHelper(distanceX * d5, distanceY * d5, distanceZ * d5));
					}
				}
			}
		}

		this.explosionSize = size;
	}

	private Set<ChunkPosition> getPositions()
	{
		Set<ChunkPosition> set = new HashSet<>();
		for (int i = 0; i < 16; ++i)
		{
			for (int j = 0; j < 16; ++j)
			{
				for (int k = 0; k < 16; ++k)
				{
					if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15)
					{
						double distanceX = i / 30F - 1F;
						double distanceY = j / 30F - 1F;
						double distanceZ = k / 30F - 1F;
						double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
						distanceX /= distance;
						distanceY /= distance;
						distanceZ /= distance;
						float size = this.explosionSize * (0.7F + this.world.rand.nextFloat() * 0.6F);
						double dX = this.explosionX;
						double dY = this.explosionY;
						double dZ = this.explosionZ;

						for (float f = 0.3F; size > 0F; size -= f * 0.75F)
						{
							int x = MathHelper.floor_double(dX);
							int y = MathHelper.floor_double(dY);
							int z = MathHelper.floor_double(dZ);
							Block block = this.world.getBlock(x, y, z);

							if (block.getMaterial() != Material.air)
							{
								float resistance = this.exploder != null ? this.exploder.func_145772_a(this, this.world, x, y, z, block) : block.getExplosionResistance(this.exploder, this.world, x, y, z, this.explosionX, this.explosionY, this.explosionZ);
								size -= (resistance + 0.3F) * f;
							}

							if (size > 0 && (this.exploder == null || this.exploder.func_145774_a(this, this.world, x, y, z, block, size)))
								if (!EventUtils.cantBreak(this.player, x, y, z))
									set.add(new ChunkPosition(x, y, z));

							dX += distanceX * f;
							dY += distanceY * f;
							dZ += distanceZ * f;
						}
					}
				}
			}
		}
		return set;
	}

	@Override
	public final Map func_77277_b()
	{
		return this.playerKnockbackMap;
	}

	public static ExplosionByPlayer createExplosion(
			@Nonnull GameProfile modFakeProfile,
			@Nullable EntityPlayer player,
			@Nonnull World world, @Nullable Entity exploder, double x, double y, double z, float size, boolean smoke)
	{
		return newExplosion(modFakeProfile, player, world, exploder, x, y, z, size, false, smoke);
	}

	public static ExplosionByPlayer createExplosion(
			@Nonnull FakePlayer modFake,
			@Nullable EntityPlayer player,
			@Nonnull World world, @Nullable Entity exploder, double x, double y, double z, float size, boolean smoke)
	{
		return newExplosion(modFake, player, world, exploder, x, y, z, size, false, smoke);
	}

	@Nonnull
	public static ExplosionByPlayer createExplosion(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world, @Nullable Entity exploder, double x, double y, double z, float size, boolean smoke)
	{
		return newExplosion(fake, world, exploder, x, y, z, size, false, smoke);
	}

	@Nonnull
	public static ExplosionByPlayer createExplosion(
			@Nonnull EntityPlayer player,
			@Nonnull World world, @Nullable Entity exploder, double x, double y, double z, float size, boolean smoke)
	{
		return newExplosion(player, world, exploder, x, y, z, size, false, smoke);
	}

	public static ExplosionByPlayer newExplosion(
			@Nonnull GameProfile modFakeProfile,
			@Nullable EntityPlayer player,
			@Nonnull World world,
			@Nullable Entity exploder, double x, double y, double z, float size, boolean flame, boolean smoke)
	{
		ExplosionByPlayer explosion = new ExplosionByPlayer(modFakeProfile, player, world, exploder, x, y, z, size);
		return newExplosion(explosion, world, x, y, z, size, flame, smoke);
	}

	public static ExplosionByPlayer newExplosion(
			@Nonnull FakePlayer modFake,
			@Nullable EntityPlayer player,
			@Nonnull World world,
			@Nullable Entity exploder, double x, double y, double z, float size, boolean flame, boolean smoke)
	{
		ExplosionByPlayer explosion = new ExplosionByPlayer(modFake, player, world, exploder, x, y, z, size);
		return newExplosion(explosion, world, x, y, z, size, flame, smoke);
	}

	@Nonnull
	public static ExplosionByPlayer newExplosion(
			@Nonnull FakePlayerContainer fake,
			@Nonnull World world,
			@Nullable Entity exploder, double x, double y, double z, float size, boolean flame, boolean smoke)
	{
		ExplosionByPlayer explosion = new ExplosionByPlayer(fake, world, exploder, x, y, z, size);
		return newExplosion(explosion, world, x, y, z, size, flame, smoke);
	}

	@Nonnull
	public static ExplosionByPlayer newExplosion(
			@Nonnull EntityPlayer player,
			@Nonnull World world,
			@Nullable Entity exploder, double x, double y, double z, float size, boolean flame, boolean smoke)
	{
		ExplosionByPlayer explosion = new ExplosionByPlayer(player, world, exploder, x, y, z, size);
		return newExplosion(explosion, world, x, y, z, size, flame, smoke);
	}

	private static ExplosionByPlayer newExplosion(
			@Nonnull ExplosionByPlayer explosion,
			@Nonnull World world, double x, double y, double z, float size, boolean flame, boolean smoke)
	{
		explosion.isFlaming = flame;
		explosion.isSmoking = smoke;

		if (ForgeEventFactory.onExplosionStart(world, explosion))
			return explosion;

		boolean isServerWorld = world instanceof WorldServer;
		explosion.doExplosionA();
		explosion.doExplosionB(!isServerWorld);

		if (isServerWorld)
		{
			if (!smoke)
				explosion.affectedBlockPositions.clear();

			for (EntityPlayer target : (Iterable<EntityPlayer>) world.playerEntities)
			{
				if (target.getDistanceSq(x, y, z) < 4096)
					((EntityPlayerMP) target).playerNetServerHandler.sendPacket(new S27PacketExplosion(x, y, z, size, explosion.affectedBlockPositions, (Vec3) explosion.func_77277_b().get(target)));
			}
		}

		return explosion;
	}
}
