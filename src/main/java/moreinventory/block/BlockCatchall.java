package moreinventory.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.Config;
import moreinventory.core.MoreInventoryMod;
import moreinventory.item.ItemBlockCatchall;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockCatchall extends BlockContainer
{
	public BlockCatchall(Material material)
	{
		super(material);
		this.setStepSound(soundTypeWood);
		this.setHardness(1.0F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		this.setHarvestLevel("axe", 0);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
	{
		world.setBlockMetadataWithNotify(x, y, z, MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3, 2);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int idk, float what, float these, float are)
	{
		if (!world.isRemote)
		{
			if (!player.isSneaking())
			{
				((TileEntityCatchall)world.getTileEntity(x, y, z)).transferTo(player);
			}
			else if (!Config.leftClickCatchall.contains(player.getUniqueID().toString()))
			{
				player.openGui(MoreInventoryMod.instance, 0, world, x, y, z);
			}
		}

		return true;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
	{
		if (!world.isRemote && Config.leftClickCatchall.contains(player.getUniqueID().toString()) && !player.isSneaking())
		{
			player.openGui(MoreInventoryMod.instance, 0, world, x, y, z);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
	{
		if (!world.isRemote)
		{
			dropItems(world, x, y, z);
		}

		super.breakBlock(world, x, y, z, par5, par6);
	}

	private void dropItems(World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile == null || !(tile instanceof IInventory))
		{
			return;
		}

		IInventory inventory = (IInventory)tile;

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			MIMUtils.dropItem(world, inventory.getStackInSlot(i), x, y, z);
		}
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	public Item getItemDropped(int damage, Random random, int fortune)
	{
		return null;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityCatchall))
		{
			return super.getBlockHardness(world, x, y, z);
		}

		return CatchallType.getHardness(((TileEntityCatchall)tile).getTypeName());
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityCatchall))
		{
			return super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
		}

		return CatchallType.getResistance(((TileEntityCatchall)tile).getTypeName()) / 5.0F;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityCatchall().setTypeName("Oak");
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return -2;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
	{
		if (!world.isRemote && !player.capabilities.isCreativeMode && willHarvest)
		{
			TileEntity tile = world.getTileEntity(x, y, z);

			if (tile != null && tile instanceof TileEntityCatchall)
			{
				EntityItem item = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, CatchallType.createItemStack(((TileEntityCatchall)tile).getTypeName()));
				item.delayBeforeCanPickup = 20;

				world.spawnEntityInWorld(item);
			}
		}

		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return side != ForgeDirection.UP;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityCatchall))
		{
			return super.getPickBlock(target, world, x, y, z);
		}

		return CatchallType.createItemStack(((TileEntityCatchall)tile).getTypeName());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		int side = target.sideHit;
		Block block = world.getBlock(x, y, z);
		float scale = 0.1F;
		double effectX = (double)x + world.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double)(scale * 2.0F)) + (double)scale + block.getBlockBoundsMinX();
		double effectY = (double)y + world.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double)(scale * 2.0F)) + (double)scale + block.getBlockBoundsMinY();
		double effectZ = (double)z + world.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double)(scale * 2.0F)) + (double)scale + block.getBlockBoundsMinZ();

		switch (side)
		{
			case 0:
				effectY = (double)y + block.getBlockBoundsMinY() - (double)scale;
				break;
			case 1:
				effectY = (double)y + block.getBlockBoundsMaxY() + (double)scale;
				break;
			case 2:
				effectZ = (double)z + block.getBlockBoundsMinZ() - (double)scale;
				break;
			case 3:
				effectZ = (double)z + block.getBlockBoundsMaxZ() + (double)scale;
				break;
			case 4:
				effectX = (double)x + block.getBlockBoundsMinX() - (double)scale;
				break;
			case 5:
				effectX = (double)x + block.getBlockBoundsMaxX() + (double)scale;
				break;
		}

		EntityDiggingFX effect = new EntityDiggingFX(world, effectX, effectY, effectZ, 0.0D, 0.0D, 0.0D, block, world.getBlockMetadata(x, y, z));
		effect.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
		effect.setParticleIcon(block.getIcon(world, x, y, z, side));

		effectRenderer.addEffect(effect);

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
	{
		Block block = world.getBlock(x, y, z);
		byte scale = 4;

		for (int effectX = 0; effectX < scale; ++effectX)
		{
			for (int effectY = 0; effectY < scale; ++effectY)
			{
				for (int effectZ = 0; effectZ < scale; ++effectZ)
				{
					double renderX = (double)x + ((double)effectX + 0.5D) / (double)scale;
					double renderY = (double)y + ((double)effectY + 0.5D) / (double)scale;
					double renderZ = (double)z + ((double)effectZ + 0.5D) / (double)scale;
					int side = world.rand.nextInt(6);
					EntityDiggingFX effect = new EntityDiggingFX(world, renderX, renderY, renderZ, renderX - (double)x - 0.5D, renderY - (double)y - 0.5D, renderZ - (double)z - 0.5D, block, meta, side);
					effect.setParticleIcon(block.getIcon(world, x, y, z, side));

					effectRenderer.addEffect(effect);
				}
			}
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		return Blocks.planks.getIcon(1, 0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityCatchall))
		{
			return super.getIcon(world, x, y, z, side);
		}

		return ItemBlockCatchall.iconMap.get(((TileEntityCatchall)tile).getTypeName());
	}
}