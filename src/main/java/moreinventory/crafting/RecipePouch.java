package moreinventory.crafting;

import moreinventory.MoreInventoryMod;
import moreinventory.item.inventory.InvPouch;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipePouch extends ShapelessOreRecipe{

	public RecipePouch(ItemStack par1ItemStack, Object[] objects) {
		super(par1ItemStack, objects);
	}
	
    public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting)
    {
    	ItemStack retItem = getRecipeOutput().copy();
		ItemStack pouch = null;
		boolean flg1 = false;
		boolean flg2 = false;
		for(int i = 0; i < par1InventoryCrafting.getSizeInventory() ; i++){
			ItemStack itemstack = par1InventoryCrafting.getStackInSlot(i);
			if(itemstack != null){
				if(itemstack.getItem().equals(MoreInventoryMod.Pouch)){
					pouch = itemstack;
					flg1 = true;
				}
				else if(itemstack.getItem().equals(Items.ender_pearl)){
					flg2 = true;
				}
			}
		}
		if(flg1){
			int dm = pouch.getItemDamage();
			if(flg2&&dm - dm % 17 == 68)return null;
			dm = flg2 ? dm+17 : dm - dm % 17 + retItem.getItemDamage()%17;
			retItem.setItemDamage(dm);
			retItem.setStackDisplayName(pouch.getDisplayName());
			InvPouch po = new InvPouch(pouch);
			po.onCrafting(retItem);	
		}
    	
        return retItem;
    }

}
