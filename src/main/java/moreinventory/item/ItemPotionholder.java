package moreinventory.item;

import moreinventory.MoreInventoryMod;
import moreinventory.item.inventory.InventoryPotionholder;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPotionholder extends Item
{
	public ItemPotionholder()
	{
		super();
		setMaxStackSize(1);
		setCreativeTab(MoreInventoryMod.customTab);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (!player.isSneaking())
		{
			InventoryPotionholder inv = new InventoryPotionholder(itemstack);
			ItemStack usingItem = inv.getFirstPotion();
			if (usingItem != null)
			{
				if (!ItemPotion.isSplash(usingItem.getItemDamage()))
				{
					player.setItemInUse(itemstack, usingItem.getItem().getMaxItemUseDuration(usingItem));
				}
				else
				{
					inv.throwPotion(world, player);
					usingItem = null;
				}
			}
		}
		else
		{
			player.openGui(MoreInventoryMod.instance, 3, world, 0, 0, 0);
		}
		return itemstack;
	}

	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		InventoryPotionholder inv = new InventoryPotionholder(par1ItemStack);
		inv.drinkPotion(par2World, par3EntityPlayer);
		return par1ItemStack;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
			int par5, int par6, int par7, float par8, float par9, float par10)
	{
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.drink;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon("moreinv:Potionholder");
	}
}