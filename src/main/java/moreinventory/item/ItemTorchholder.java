package moreinventory.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.MoreInventoryMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemTorchholder extends Item
{

	public int grade;
	public static final int[] maxDamage = { 258, 1026, 4098};


	public ItemTorchholder(int arggrade)
	{
		super();
		setMaxStackSize(1);
		grade = arggrade;
		setMaxDamage(maxDamage[grade]);
		this.canRepair = false;
        setContainerItem(this);
		setCreativeTab(MoreInventoryMod.customTab);
	}


    @Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool)
	{
		list.add((itemstack.getMaxDamage() - itemstack.getItemDamage()-2) + " uses");
	}


    @Override
	 public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
 		boolean torchSetFlg = false;
	 	if(par1ItemStack.getItemDamage()<maxDamage[grade]-2)
		{
	 		if((new ItemStack(Blocks.torch).getItem()).onItemUse(new ItemStack(Blocks.torch,1), par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10))
	 		{
	 			par1ItemStack.damageItem(1, par2EntityPlayer);
	 			torchSetFlg = true;
	 		}
	 	}

		if(par2EntityPlayer.isSneaking()&&!torchSetFlg)
		{
			rechageTorchs(par1ItemStack,par3World,par2EntityPlayer);
		}
		return true;
	 }

    @Override
	 public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	 {
		if(par3EntityPlayer.isSneaking())
		{
			rechageTorchs(par1ItemStack,par2World,par3EntityPlayer);
 		}
	    return par1ItemStack;
	 }


	 //Called when player sneaks and can't place torch.
	 public void rechageTorchs(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	 {
	    InventoryPlayer inventory = par3EntityPlayer.inventory;
	    if(par1ItemStack.getItem() instanceof ItemTorchholder&&par1ItemStack.getItemDamage()!=0&&!par3EntityPlayer.capabilities.isCreativeMode)
	    {
	    	int TorchCount = 0;
	    	if (par1ItemStack != null && inventory != null)
	    	{
	    		int k = inventory.getSizeInventory();
	    		for (int i = 0; i < k; i++)
	    		{
	    			if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).isItemEqual(new ItemStack(Blocks.torch)))
	    			{
	    				TorchCount += inventory.getStackInSlot(i).stackSize;
	    			}
	    		}
	    	}
	    	while(par1ItemStack.getItemDamage()!=0&&TorchCount!=0)
	    	{
	    		par1ItemStack.setItemDamage(par1ItemStack.getItemDamage() - 1);
				inventory.consumeInventoryItem(new ItemStack(Blocks.torch).getItem());
				TorchCount--;
	    	}
	    }
	 }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack)
    {
        return false;
    }

    public ItemStack getContainerItem(ItemStack itemStack)
    {
        if (!hasContainerItem(itemStack))
        {
            return null;
        }
        itemStack.setItemDamage(itemStack.getItemDamage() + 1);
        return itemStack;
    }

	 @SideOnly(Side.CLIENT)
     @Override
	 public void registerIcons(IIconRegister par1IconRegister)
	 {
	 	if (grade==0)
	 	{
	 		this.itemIcon = par1IconRegister.registerIcon("moreinv:Torchholder_iron");
	 	}
	 	else if(grade==1)
	 	{
	 		this.itemIcon = par1IconRegister.registerIcon("moreinv:Torchholder_gold");
	 	}
	 	else if(grade==2)
	 	{
	 		this.itemIcon = par1IconRegister.registerIcon("moreinv:Torchholder_diamond");
	 	}
	 }


}
