package moreinventory.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.MoreInventoryMod;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.CSutil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public class BlockStorageBox extends BlockContainer{

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	@SideOnly(Side.CLIENT)
	private IIcon[] icons_top;
	@SideOnly(Side.CLIENT)
	private IIcon[] icons_face;
	@SideOnly(Side.CLIENT)
	private IIcon[] icons_glass;
	@SideOnly(Side.CLIENT)
	private IIcon icon_glass_air;
	private byte[][] glassIndex = {{2,5,3,4},{2,5,3,4},{1,4,0,5},{1,5,0,4},{1,3,0,2},{1,2,0,3}}; 

	public BlockStorageBox (Material material){
		super(material);
		setHardness(2.0F);
		setCreativeTab(MoreInventoryMod.customTab);
	}

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int idk, float what, float these, float are)
    {
        if (world.isRemote)
        {
        	TileEntityStorageBox tileEntity = (TileEntityStorageBox)world.getTileEntity(x, y, z);
        	ItemStack item = tileEntity.getStackInSlot(0);
            return true;
        }
        else
        {
            TileEntityStorageBox tileEntity = (TileEntityStorageBox)world.getTileEntity(x, y, z);
            ItemStack itemstack = player.getCurrentEquippedItem();
            if(itemstack!=null&&itemstack.getItem() == MoreInventoryMod.NoFunctionItems && itemstack.getItemDamage() == 3&&tileEntity.getStorageBoxType() != StorageBoxType.Glass){
            		tileEntity.sendGUIPacketToClient();
            		player.openGui(MoreInventoryMod.instance, 4, world, x, y, z);
                    return true;
            }
            else
            {	
            	return tileEntity.rightClickEvent(world, player, x, y, z);
            }
        }
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
    	if(!world.isRemote){
	    	TileEntityStorageBox tileEntity = (TileEntityStorageBox)world.getTileEntity(x, y, z);
	 	    tileEntity.leftClickEvent(player);
    	}
    }

    @Override
    public void onNeighborBlockChange(World world,int x,int y,int z,Block block){
    		TileEntityStorageBox tile = (TileEntityStorageBox)world.getTileEntity(x, y, z);
    		if(block.equals(this))tile.onNeighborRemoved();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
            dropItems(world, x, y, z);
            super.breakBlock(world, x, y, z, par5, par6);
    }

    private void dropItems(World world, int x, int y, int z){
            Random rand = new Random();

            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (!(tileEntity instanceof IInventory) || tileEntity instanceof TileEntityEnderStorageBox) {
                    return;
            }
            IInventory inventory = (IInventory) tileEntity;

            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    ItemStack item = inventory.getStackInSlot(i);
                    CSutil.dropItem(world, item, x, y, z);
            }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack par6ItemStack)
    {
        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        TileEntityStorageBox tileEntity = (TileEntityStorageBox)world.getTileEntity(x, y, z);
		tileEntity.onPlaced(entity);
        if (l == 0)
        {
        	tileEntity.face = 2;
        }

        if (l == 1)
        {
        	tileEntity.face = 5;
        }

        if (l == 2)
        {
        	tileEntity.face = 3;
        }

        if (l == 3)
        {
        	tileEntity.face = 4;
        }
    }

	@SideOnly(Side.CLIENT)
    @Override
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		icons= new IIcon[StorageBoxType.values().length];
		icons_top= new IIcon[StorageBoxType.values().length];
		icons_face= new IIcon[StorageBoxType.values().length];
		icons_glass = new IIcon[16];
		for(int i = 0;i<StorageBoxType.values().length;i++){
			if(i !=  8){
				this.icons[i] = par1IconRegister.registerIcon("moreinv:Box_"+StorageBoxType.values()[i].name() + "_side");
				this.icons_top[i] = par1IconRegister.registerIcon("moreinv:Box_"+StorageBoxType.values()[i].name() + "_top");
				this.icons_face[i] = par1IconRegister.registerIcon("moreinv:Box_"+StorageBoxType.values()[i].name() + "_face");
			}
			else
			{
				this.icons[i] = par1IconRegister.registerIcon("moreinv:Box_"+StorageBoxType.values()[i].name() + "0");
				this.icons_top[i] = icons[i];
				this.icons_face[i] = icons[i];
			}
		}
		for(int i = 0; i < 16; i++){
			icons_glass[i] = par1IconRegister.registerIcon("moreinv:Box_"+StorageBoxType.values()[8].name() + i);
		}
		icon_glass_air = par1IconRegister.registerIcon("moreinv:Box_Glass_Air");
	}

	@SideOnly(Side.CLIENT)
    @Override
	public IIcon getIcon(int side, int metadata)
	{
		return side == 0 ||side == 1 ? icons_top[metadata] : icons[metadata];
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
    	TileEntityStorageBox tile = (TileEntityStorageBox)world.getTileEntity(x, y, z);
    	int metadata = world.getBlockMetadata(x, y, z);
    	int face = tile.face;
    	if(metadata == 8){
    		int pos[] = CSutil.getSidePos(x, y, z, side);
    		if(world.getBlock(pos[0], pos[1], pos[2]).equals(this)||world.getBlock(pos[0], pos[1], pos[2]).isNormalCube()){
    			return icon_glass_air;
    		}
    		else if(MoreInventoryMod.clearGlassBox)
    		{
    			return this.getGlassIcon(world, x, y, z, side);
    		}	
    	}
	    	
	    if(MoreInventoryMod.StorageBoxsideTexture){
	        return  side==0||side ==1 ? icons_top[metadata] : side == face ? icons_face[metadata] :icons[metadata];
    	}
    	
    	return getIcon(side, metadata);
    }
    
	@SideOnly(Side.CLIENT)
	private IIcon getGlassIcon(IBlockAccess world, int x, int y, int z, int side){
		byte index = 0;
		for(int i = 0; i < 4; i++){
			int pos[] = CSutil.getSidePos(x, y, z, glassIndex[side][i]);
			if(world.getBlock(pos[0], pos[1], pos[2]).equals(this)){
				index |= 1<<i;
			}
		}
		return this.icons_glass[index];
	}
    
    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axe){
    	TileEntityStorageBox tile = (TileEntityStorageBox) world.getTileEntity(x, y, z);
    	tile.rotateBlock();
    	return true;
    }

	@Override
	public TileEntity createNewTileEntity(World world,int metadata) {
		return StorageBoxType.makeEntity(metadata);
	}

    @Override
	public int getRenderType()
	{
		return 0;
	}

	@Override
	public boolean isOpaqueCube()
	{
	return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

    @Override
    public int damageDropped(int par1)
    {
        return par1;
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side)
    {
    	 TileEntity tile = world.getTileEntity(x, y, z);
    	 return tile instanceof TileEntityStorageBox ? TileEntityStorageBox.getPowerOutput((TileEntityStorageBox) tile) : 0;
    }

    
	@SideOnly(Side.CLIENT)
    @Override
	public void getSubBlocks(Item unknown, CreativeTabs tab, List subItems)
	{
		for (int ix = 0; ix < StorageBoxType.values().length; ix++) {
			subItems.add(new ItemStack(this, 1, ix));
		}
	}
}
