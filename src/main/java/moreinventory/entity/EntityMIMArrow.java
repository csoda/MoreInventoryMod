package moreinventory.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

public class EntityMIMArrow extends EntityArrow
{
	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	private Block inTile;
	private int inData;
	private boolean inGround;
	private int ticksInGround;
	private int ticksInAir;
	private double damage = 2.0D;
	private int knockbackStrength;

	public EntityMIMArrow(World world)
	{
		super(world);
	}

	public EntityMIMArrow(World world, double posX, double posY, double posZ)
	{
		super(world, posX, posY, posZ);
	}

	public EntityMIMArrow(World world, EntityLivingBase living, EntityLivingBase target, float f1, float f2)
	{
		super(world, living, target, f1, f2);
	}

	public EntityMIMArrow(World world, EntityLivingBase living, float f1)
	{
		super(world, living, f1);
	}

	@Override
	public void setThrowableHeading(double x, double y, double z, float f1, float f2)
	{
		float f = MathHelper.sqrt_double(x * x + y * y + z * z);
		x /= (double)f;
		y /= (double)f;
		z /= (double)f;
		x += rand.nextGaussian() * (double)(rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)f2;
		y += rand.nextGaussian() * (double)(rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)f2;
		z += rand.nextGaussian() * (double)(rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)f2;
		x *= (double) f1;
		y *= (double) f1;
		z *= (double) f1;
		motionX = x;
		motionY = y;
		motionZ = z;
		f = MathHelper.sqrt_double(x * x + z * z);
		prevRotationYaw = rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		prevRotationPitch = rotationPitch = (float)(Math.atan2(y, (double)f) * 180.0D / Math.PI);
		ticksInGround = 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setVelocity(double x, double y, double z)
	{
		motionX = x;
		motionY = y;
		motionZ = z;

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double(x * x + z * z);
			prevRotationYaw = rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(y, (double)f) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch;
			prevRotationYaw = rotationYaw;
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
			ticksInGround = 0;
		}
	}

	public void onUpdate()
	{
		super.onUpdate();

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			prevRotationYaw = rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(motionY, (double)f) * 180.0D / Math.PI);
		}

		Block block = worldObj.getBlock(blockX, blockY, blockZ);

		if (block.getMaterial() != Material.air)
		{
			block.setBlockBoundsBasedOnState(worldObj, blockX, blockY, blockZ);
			AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(worldObj, blockX, blockY, blockZ);

			if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(posX, posY, posZ)))
			{
				inGround = true;
			}
		}

		if (arrowShake > 0)
		{
			--arrowShake;
		}

		if (inGround)
		{
			int j = worldObj.getBlockMetadata(blockX, blockY, blockZ);

			if (block == inTile && j == inData)
			{
				++ticksInGround;

				if (ticksInGround == 1200)
				{
					setDead();
				}
			}
			else
			{
				inGround = false;
				motionX *= (double)(rand.nextFloat() * 0.2F);
				motionY *= (double)(rand.nextFloat() * 0.2F);
				motionZ *= (double)(rand.nextFloat() * 0.2F);
				ticksInGround = 0;
				ticksInAir = 0;
			}
		}
		else
		{
			++ticksInAir;
			Vec3 vec31 = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 vec3 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			MovingObjectPosition moving = worldObj.func_147447_a(vec31, vec3, false, true, false);
			vec31 = Vec3.createVectorHelper(posX, posY, posZ);
			vec3 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);

			if (moving != null)
			{
				vec3 = Vec3.createVectorHelper(moving.hitVec.xCoord, moving.hitVec.yCoord, moving.hitVec.zCoord);
			}

			Entity entity = null;
			List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			int i;
			float f1;

			for (i = 0; i < list.size(); ++i)
			{
				Entity entity1 = (Entity)list.get(i);

				if (entity1.canBeCollidedWith() && (entity1 != shootingEntity || ticksInAir >= 5))
				{
					f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double)f1, (double)f1, (double)f1);
					MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

					if (movingobjectposition1 != null)
					{
						double d1 = vec31.distanceTo(movingobjectposition1.hitVec);

						if (d1 < d0 || d0 == 0.0D)
						{
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null)
			{
				moving = new MovingObjectPosition(entity);
			}

			if (moving != null && moving.entityHit != null && moving.entityHit instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) moving.entityHit;

				if (player.capabilities.disableDamage || shootingEntity instanceof EntityPlayer && !((EntityPlayer)shootingEntity).canAttackPlayer(player))
				{
					moving = null;
				}
			}

			float f2;
			float f4;

			if (moving != null)
			{
				if (moving.entityHit != null)
				{
					f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
					int k = MathHelper.ceiling_double_int((double)f2 * damage);

					if (getIsCritical())
					{
						k += rand.nextInt(k / 2 + 2);
					}

					DamageSource source;

					if (shootingEntity == null)
					{
						source = DamageSource.causeArrowDamage(this, this);
					}
					else
					{
						source = DamageSource.causeArrowDamage(this, shootingEntity);
					}

					if (isBurning() && !(moving.entityHit instanceof EntityEnderman))
					{
						moving.entityHit.setFire(5);
					}

					if (moving.entityHit.attackEntityFrom(source, (float)k))
					{
						if (moving.entityHit instanceof EntityLivingBase)
						{
							EntityLivingBase entitylivingbase = (EntityLivingBase) moving.entityHit;

							if (!worldObj.isRemote)
							{
								entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
							}

							if (knockbackStrength > 0)
							{
								f4 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

								if (f4 > 0.0F)
								{
									moving.entityHit.addVelocity(motionX * (double)knockbackStrength * 0.6000000238418579D / (double)f4, 0.1D, motionZ * (double)knockbackStrength * 0.6000000238418579D / (double)f4);
								}
							}

							if (shootingEntity != null && shootingEntity instanceof EntityLivingBase)
							{
								EnchantmentHelper.func_151384_a(entitylivingbase, shootingEntity);
								EnchantmentHelper.func_151385_b((EntityLivingBase)shootingEntity, entitylivingbase);
							}

							if (shootingEntity != null && moving.entityHit != shootingEntity && moving.entityHit instanceof EntityPlayer && shootingEntity instanceof EntityPlayerMP)
							{
								((EntityPlayerMP)shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
							}
						}

						playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));

						if (!(moving.entityHit instanceof EntityEnderman))
						{
							setDead();
						}
					}
					else
					{
						motionX *= -0.10000000149011612D;
						motionY *= -0.10000000149011612D;
						motionZ *= -0.10000000149011612D;
						rotationYaw += 180.0F;
						prevRotationYaw += 180.0F;
						ticksInAir = 0;
					}
				}
				else
				{
					blockX = moving.blockX;
					blockY = moving.blockY;
					blockZ = moving.blockZ;
					inTile = worldObj.getBlock(blockX, blockY, blockZ);
					inData = worldObj.getBlockMetadata(blockX, blockY, blockZ);
					motionX = (double)((float)(moving.hitVec.xCoord - posX));
					motionY = (double)((float)(moving.hitVec.yCoord - posY));
					motionZ = (double)((float)(moving.hitVec.zCoord - posZ));
					f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
					posX -= motionX / (double)f2 * 0.05000000074505806D;
					posY -= motionY / (double)f2 * 0.05000000074505806D;
					posZ -= motionZ / (double)f2 * 0.05000000074505806D;
					playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
					inGround = true;
					arrowShake = 7;
					setIsCritical(false);

					if (inTile.getMaterial() != Material.air)
					{
						inTile.onEntityCollidedWithBlock(worldObj, blockX, blockY, blockZ, this);
					}
				}
			}

			if (getIsCritical())
			{
				for (i = 0; i < 4; ++i)
				{
					worldObj.spawnParticle("crit", posX + motionX * (double)i / 4.0D, posY + motionY * (double)i / 4.0D, posZ + motionZ * (double)i / 4.0D, -motionX, -motionY + 0.2D, -motionZ);
				}
			}

			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			f2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
			rotationPitch = (float)(Math.atan2(motionY, (double)f2) * 180.0D / Math.PI);

			while (rotationPitch - prevRotationPitch < -180.0F)
			{
				prevRotationPitch -= 360.0F;
			}

			while (rotationPitch - prevRotationPitch >= 180.0F)
			{
				prevRotationPitch += 360.0F;
			}

			while (rotationYaw - prevRotationYaw < -180.0F)
			{
				prevRotationYaw -= 360.0F;
			}

			while (rotationYaw - prevRotationYaw >= 180.0F)
			{
				prevRotationYaw += 360.0F;
			}

			rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
			rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
			float f3 = 0.99F;
			f1 = 0.05F;

			if (isInWater())
			{
				for (int l = 0; l < 4; ++l)
				{
					f4 = 0.25F;
					worldObj.spawnParticle("bubble", posX - motionX * (double)f4, posY - motionY * (double)f4, posZ - motionZ * (double)f4, motionX, motionY, motionZ);
				}

				f3 = 0.8F;
			}

			if (isWet())
			{
				extinguish();
			}

			motionX *= (double)f3;
			motionY *= (double)f3;
			motionZ *= (double)f3;
			motionY -= (double)f1;
			setPosition(posX, posY, posZ);
			func_145775_I();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setShort("xTile", (short)blockX);
		nbt.setShort("yTile", (short)blockY);
		nbt.setShort("zTile", (short)blockZ);
		nbt.setShort("life", (short)ticksInGround);
		nbt.setByte("inTile", (byte)Block.getIdFromBlock(inTile));
		nbt.setByte("inData", (byte)inData);
		nbt.setByte("shake", (byte)arrowShake);
		nbt.setByte("inGround", (byte)(inGround ? 1 : 0));
		nbt.setByte("pickup", (byte)canBePickedUp);
		nbt.setDouble("damage", damage);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		blockX = nbt.getShort("xTile");
		blockY = nbt.getShort("yTile");
		blockZ = nbt.getShort("zTile");
		ticksInGround = nbt.getShort("life");
		inTile = Block.getBlockById(nbt.getByte("inTile") & 255);
		inData = nbt.getByte("inData") & 255;
		arrowShake = nbt.getByte("shake") & 255;
		inGround = nbt.getByte("inGround") == 1;

		if (nbt.hasKey("damage", 99))
		{
			damage = nbt.getDouble("damage");
		}

		if (nbt.hasKey("pickup", 99))
		{
			canBePickedUp = nbt.getByte("pickup");
		}
		else if (nbt.hasKey("player", 99))
		{
			canBePickedUp = nbt.getBoolean("player") ? 1 : 0;
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player)
	{
		if (!worldObj.isRemote && inGround && arrowShake <= 0)
		{
			boolean flag = canBePickedUp == 1 || canBePickedUp == 2 && player.capabilities.isCreativeMode;
			boolean holder = false;

			if (flag && player.inventory.hasItem(MoreInventoryMod.arrowHolder))
			{
				for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
				{
					ItemStack itemstack = player.inventory.getStackInSlot(i);

					if (itemstack != null && itemstack.getItem() == MoreInventoryMod.arrowHolder)
					{
						int damage = itemstack.getItemDamage();

						if (damage >= 1)
						{
							itemstack.setItemDamage(damage - 1);
							holder = true;
							break;
						}
					}
				}
			}

			if (!holder && canBePickedUp == 1 && !player.inventory.addItemStackToInventory(new ItemStack(Items.arrow, 1)))
			{
				flag = false;
			}

			if (flag)
			{
				playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);

				if (!holder)
				{
					player.onItemPickup(this, 1);
				}

				setDead();
			}
		}
	}

	@Override
	public void setDamage(double amount)
	{
		damage = amount;
	}

	@Override
	public double getDamage()
	{
		return damage;
	}

	@Override
	public void setKnockbackStrength(int amount)
	{
		knockbackStrength = amount;
	}
}