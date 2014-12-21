package moreinventory.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

import java.util.List;

public class RecipeHolderEject extends ShapelessRecipes
{
	public RecipeHolderEject(ItemStack itemstack, List list)
	{
		super(itemstack, list);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting)
	{
		ItemStack itemstack = null;

		for (int i = 0; i < crafting.getSizeInventory(); ++i)
		{
			ItemStack item = crafting.getStackInSlot(i);

			if (item != null && item.getItem() == ((ItemStack)recipeItems.get(0)).getItem() && item.getItemDamage() < item.getMaxDamage() - 2)
			{
				itemstack = getRecipeOutput().copy();
			}
		}

		return itemstack;
	}
}