package moreinventory.item;

import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPotionHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPotionHolder extends Item
{
	public ItemPotionHolder()
	{
		this.setTextureName("moreinv:Potionholder");
		this.setMaxStackSize(1);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (!player.isSneaking())
		{
			InventoryPotionHolder inventory = new InventoryPotionHolder(itemstack);
			ItemStack first = inventory.getFirstPotion();

			if (first != null)
			{
				if (!ItemPotion.isSplash(first.getItemDamage()))
				{
					player.setItemInUse(itemstack, first.getItem().getMaxItemUseDuration(first));
				}
				else
				{
					inventory.throwPotion(world, player);

					first = null;
				}
			}
		}
		else if (!world.isRemote)
		{
			player.openGui(MoreInventoryMod.instance, 3, world, 0, 0, 0);
		}

		return itemstack;
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player)
	{
		new InventoryPotionHolder(itemstack).drinkPotion(world, player);

		return itemstack;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack)
	{
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack)
	{
		return EnumAction.drink;
	}
}