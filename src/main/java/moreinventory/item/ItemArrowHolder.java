package moreinventory.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemArrowHolder extends Item
{
	private final int grade;

	public ItemArrowHolder(int grade)
	{
		this.grade = grade;
		this.setMaxStackSize(1);
		this.setMaxDamage(ItemTorchHolder.maxDamage[grade]);
		this.setNoRepair();
		this.setContainerItem(this);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (player.isSneaking())
		{
			rechargeArrows(itemstack, player);
		}

		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (player.isSneaking())
		{
			rechargeArrows(itemstack, player);
		}

		return itemstack;
	}

	public void rechargeArrows(ItemStack itemstack, EntityPlayer player)
	{
		InventoryPlayer inventory = player.inventory;

		if (itemstack != null && itemstack.getItem() instanceof ItemArrowHolder && itemstack.getItemDamage() != 0 && !player.capabilities.isCreativeMode)
		{
			int count = 0;

			if (inventory != null)
			{
				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getItem() == Items.arrow)
					{
						count += inventory.getStackInSlot(i).stackSize;
					}
				}
			}

			while (itemstack.getItemDamage() != 0 && count != 0)
			{
				itemstack.setItemDamage(itemstack.getItemDamage() - 1);
				inventory.consumeInventoryItem(Items.arrow);

				--count;
			}
		}
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemstack)
	{
		return false;
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemstack)
	{
		if (!hasContainerItem(itemstack))
		{
			return null;
		}

		itemstack.setItemDamage(itemstack.getItemDamage() + 1);

		return itemstack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		switch (grade)
		{
			case 0:
				itemIcon = iconRegister.registerIcon("moreinv:Arrowholder_iron");
				break;
			case 1:
				itemIcon = iconRegister.registerIcon("moreinv:Arrowholder_gold");
				break;
			case 2:
				itemIcon = iconRegister.registerIcon("moreinv:Arrowholder_diamond");
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		list.add(I18n.format("item.arrowholder.rest") + ": " + (itemstack.getMaxDamage() - itemstack.getItemDamage() - 2));
	}
}