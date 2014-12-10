package moreinventory.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.Config;
import moreinventory.core.MoreInventoryMod;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCatchall extends BlockContainer
{
	public BlockCatchall(Material material)
	{
		super(material);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
		this.setStepSound(Block.soundTypeWood);
		this.setHardness(1.0F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		this.setTickRandomly(true);
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
			else if (!Config.leftClickCatchall)
			{
				player.openGui(MoreInventoryMod.instance, 0, world, x, y, z);
			}
		}

		return true;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
	{
		if (!world.isRemote && Config.leftClickCatchall && !player.isSneaking())
		{
			player.openGui(MoreInventoryMod.instance, 0, world, x, y, z);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
	{
		dropItems(world, x, y, z);

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

		for (int i = 0; i < inventory.getSizeInventory(); i++)
		{
			MIMUtils.dropItem(world, inventory.getStackInSlot(i), x, y, z);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityCatchall();
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

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		return Blocks.planks.getIcon(1, 0);
	}
}