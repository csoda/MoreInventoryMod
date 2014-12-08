package moreinventory.crafting;

import java.util.List;

import moreinventory.item.ItemTorchHolder;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

public class RecipeTorchHolder extends ShapelessRecipes
{
	public RecipeTorchHolder(ItemStack itemstack, List list)
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

			if (item != null && item.getItem() instanceof ItemTorchHolder && item.getItemDamage() < item.getMaxDamage() - 2)
			{
				itemstack = getRecipeOutput().copy();
			}
		}

		return itemstack;
	}
}