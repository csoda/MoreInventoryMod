package moreinventory.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

public class RecipeTorchHolder extends ShapedRecipes{


	
    public RecipeTorchHolder(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack) {
		super(par1, par2, par3ArrayOfItemStack, par4ItemStack);
	}

	public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting)
    {
    	ItemStack retitem = getRecipeOutput().copy();
		ItemStack item = par1InventoryCrafting.getStackInSlot(4);
		int dm = retitem.getMaxDamage() + item.getItemDamageForDisplay() - item.getMaxDamage();
		retitem.setItemDamage(dm);
        return retitem;
    }

}
