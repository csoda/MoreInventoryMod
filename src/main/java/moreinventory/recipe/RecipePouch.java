package moreinventory.recipe;

import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipePouch extends ShapelessOreRecipe
{
	public RecipePouch(ItemStack itemstack, Object... objects)
	{
		super(itemstack, objects);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting)
	{
		ItemStack result = getRecipeOutput().copy();
		ItemStack pouch = null;
		boolean flag1 = false;
		boolean flag2 = false;

		for (int i = 0; i < crafting.getSizeInventory(); i++)
		{
			ItemStack itemstack = crafting.getStackInSlot(i);

			if (itemstack != null)
			{
				if (itemstack.getItem() == MoreInventoryMod.Pouch)
				{
					pouch = itemstack;
					flag1 = true;
				}
				else if (itemstack.getItem() == Items.ender_pearl)
				{
					flag2 = true;
				}
			}
		}

		if (flag1)
		{
			int damage = pouch.getItemDamage();

			if (flag2 && damage - damage % 17 == 68)
			{
				return null;
			}

			damage = flag2 ? damage + 17 : damage - damage % 17 + result.getItemDamage() % 17;
			result.setItemDamage(damage);
			result.setStackDisplayName(pouch.getDisplayName());

			new InventoryPouch(pouch).onCrafting(result);
		}

		return result;
	}
}