package moreinventory.recipe;

import moreinventory.item.ItemArrowHolder;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

import java.util.List;

public class RecipeArrowHolder extends ShapelessRecipes
{
	public RecipeArrowHolder(ItemStack itemstack, List list)
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

			if (item != null && item.getItem() instanceof ItemArrowHolder && item.getItemDamage() < item.getMaxDamage() - 2)
			{
				itemstack = getRecipeOutput().copy();
			}
		}

		return itemstack;
	}
}