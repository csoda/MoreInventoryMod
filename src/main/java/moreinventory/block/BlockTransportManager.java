package moreinventory.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
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

import java.util.List;

public class BlockTransportManager extends BlockContainer
{
	public BlockTransportManager(Material material)
	{
		super(material);
		this.setHardness(1.0F);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
		this.setBlockBounds(0.075F, 0.075F, 0.075F, 0.925F, 0.925F, 0.925F);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			if (!player.isSneaking())
			{
				player.openGui(MoreInventoryMod.instance, 2, world, x, y, z);
			}
			else if (player.getCurrentEquippedItem() == null)
			{
				((TileEntityTransportManager)world.getTileEntity(x, y, z)).rotateBlock();
			}
		}

		return true;
	}

	@Override
	public int damageDropped(int metadata)
	{
		return metadata;
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

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection direction)
	{
		((TileEntityTransportManager)world.getTileEntity(x, y, z)).rotateBlock();

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon("anvil_base");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for (int meta = 0; meta < 2; ++meta)
		{
			list.add(new ItemStack(this, 1, meta));
		}
	}
}