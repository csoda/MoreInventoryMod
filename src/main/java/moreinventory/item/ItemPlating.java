package moreinventory.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.MoreInventoryMod;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemPlating extends Item{
	
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	public static final byte[] typeIndex = {1, 2, 3, 4, 5, 6, 7, 10, 12};
	
	public ItemPlating(){
		super();
		this.setHasSubtypes(true);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		if(!world.isRemote){
			if(world.getBlock(par4, par5, par6).equals(MoreInventoryMod.StorageBox)){
				int k = typeIndex[itemstack.getItemDamage()];
				int t = world.getBlockMetadata(par4, par5, par6);
				int Tier1 = StorageBoxType.values()[k].Tier;
				int Tier2 = StorageBoxType.values()[t].Tier;
				if(k!=0&&(Tier1 == Tier2||Tier1 == Tier2 + 1)&&StorageBoxType.values()[k].invSize > StorageBoxType.values()[t].invSize){
					TileEntityStorageBox tile = (TileEntityStorageBox)world.getTileEntity(par4, par5, par6);
					world.setBlockMetadataWithNotify(par4, par5, par6, k, 2);
					world.setTileEntity(par4, par5, par6, tile.upgrade(k));
					if(!player.capabilities.isCreativeMode){
						itemstack.stackSize--;
						player.onUpdate();
					}
					return true;
				}
			}
			return false;
		}
		else
		{
			return false;
		}
    }
	
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return "painting:" + StorageBoxType.values()[ItemPlating.typeIndex[par1ItemStack.getItemDamage()]].name();
    }
	
	@SideOnly(Side.CLIENT)
    @Override
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[typeIndex.length];
		int k = typeIndex.length;
		for(int i = 0; i < k; i++){
				icons[i] = iconRegister.registerIcon("moreinv:plating_" + StorageBoxType.values()[typeIndex[i]].name());
		}
	}
	
	 @SideOnly(Side.CLIENT)
     @Override
	 public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	 {
		   for(int i=0;i<typeIndex.length;i++){
			   par3List.add(new ItemStack(this, 1, i));
		   }
	 }

	 @SideOnly(Side.CLIENT)
	 @Override
	 public IIcon getIconFromDamage(int par1)
	 {
	     return icons[par1];
	 }
	
}
