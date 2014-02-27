package moreinventory.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.MoreInventoryMod;
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

public class BlockStorageBoxAddon extends BlockContainer{
	
	@SideOnly(Side.CLIENT)
	Map<String,IIcon> iconMap;
	
	public BlockStorageBoxAddon(Material material){
		super(material);
		setHardness(2.0F);
		setCreativeTab(MoreInventoryMod.customTab);
	}

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int idk, float what, float these, float are)
    {
    	int meta = world.getBlockMetadata(x, y, z);
        if (!world.isRemote&&!player.isSneaking()){
            	player.openGui(MoreInventoryMod.instance, EnumSBAddon.values()[meta].guiID, world, x, y, z);
        }
            return true;
    }
	
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack par6ItemStack)
    {
        if(!world.isRemote){
            TileEntitySBAddonBase tileEntity = (TileEntitySBAddonBase)world.getTileEntity(x, y, z);
	    	tileEntity.onPlaced(entity);
        }
    }

	@Override
	public TileEntity createNewTileEntity(World world,int metadata) {
		return EnumSBAddon.makeEntity(metadata);
	}
	
	@SideOnly(Side.CLIENT)
    @Override
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		iconMap = EnumSBAddon.registerIcon(par1IconRegister);
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
	public void getSubBlocks(Item unknown, CreativeTabs tab, List subItems)
	{
		for (int ix = 0; ix < EnumSBAddon.values().length; ix++) {
			subItems.add(new ItemStack(this, 1, ix));
		}
	}
}
