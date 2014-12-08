package moreinventory.block;

import java.util.List;

import moreinventory.MoreInventoryMod;
import moreinventory.tileentity.TileEntityExporter;
import moreinventory.tileentity.TileEntityImporter;
import moreinventory.tileentity.TileEntityTransportManager;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTransportManager extends BlockContainer
{
	public BlockTransportManager(Material material)
	{
		super(material);
		setHardness(1.0F);
		setCreativeTab(MoreInventoryMod.customTab);
		setBlockBounds(0.075F, 0.075F, 0.075F, 0.925F, 0.925F, 0.925F);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote && !player.isSneaking())
		{
			player.openGui(MoreInventoryMod.instance, 2, world, x, y, z);
		}

		return true;
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return metadata == 0 ? new TileEntityImporter() : new TileEntityExporter();
	}

	@Override
	public int getRenderType()
	{
		return -2;
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

	/*
	 * @Override private boolean isIndirectlyPowered(World par1World, int par2, int par3, int par4, int par5) { return par5 != 0 && par1World.getIndirectPowerOutput(par2, par3 - 1, par4, 0) ? true : (par5 != 1 && par1World.getIndirectPowerOutput(par2, par3 + 1, par4, 1) ? true : (par5 != 2 && par1World.getIndirectPowerOutput(par2, par3, par4 - 1, 2) ? true : (par5 != 3 && par1World.getIndirectPowerOutput(par2, par3, par4 + 1, 3) ? true : (par5 != 5 && par1World.getIndirectPowerOutput(par2 + 1, par3, par4, 5) ? true : (par5 != 4 && par1World.getIndirectPowerOutput(par2 - 1, par3, par4, 4) ? true : (par1World.getIndirectPowerOutput(par2, par3, par4, 0) ? true : (par1World.getIndirectPowerOutput(par2, par3 + 2, par4, 1) ? true : (par1World.getIndirectPowerOutput(par2, par3 + 1, par4 - 1, 2) ? true : (par1World.getIndirectPowerOutput(par2, par3 + 1, par4 + 1, 3) ? true : (par1World.getIndirectPowerOutput(par2 - 1, par3 + 1, par4, 4) ? true : par1World.getIndirectPowerOutput(par2 + 1, par3
	 * + 1, par4, 5))))))))))); }
	 */

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axe)
	{
		TileEntityTransportManager tile = (TileEntityTransportManager) world.getTileEntity(x, y, z);
		tile.rotateBlock();
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon("anvil_base");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item unknown, CreativeTabs tab, List subItems)
	{
		for (int ix = 0; ix < 2; ix++)
		{
			subItems.add(new ItemStack(this, 1, ix));
		}
	}
}