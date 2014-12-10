package moreinventory.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import moreinventory.tileentity.storagebox.addon.EnumSBAddon;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class BlockStorageBoxAddon extends BlockContainer
{
	@SideOnly(Side.CLIENT)
	private Map<String, IIcon> iconMap;

	public BlockStorageBoxAddon(Material material)
	{
		super(material);
		this.setHardness(2.0F);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote && !player.isSneaking())
		{
			player.openGui(MoreInventoryMod.instance, EnumSBAddon.values()[world.getBlockMetadata(x, y, z)].guiID, world, x, y, z);
		}

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
	{
		if (!world.isRemote)
		{
			((TileEntitySBAddonBase)world.getTileEntity(x, y, z)).onPlaced(entity);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return EnumSBAddon.makeEntity(metadata);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		iconMap = EnumSBAddon.registerIcon(iconRegister);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		return EnumSBAddon.getIcon(iconMap, side, metadata);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		return EnumSBAddon.getBlockTexture(iconMap, world, x, y, z, side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for (int meta = 0; meta < EnumSBAddon.values().length; meta++)
		{
			list.add(new ItemStack(this, 1, meta));
		}
	}
}
