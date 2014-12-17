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

			for (int i = 0; i < inventory.getSizeInventory(); i++)
			{
				if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getItem() == Items.arrow)
				{
					count += inventory.getStackInSlot(i).stackSize;
				}
			}

			while (itemstack.getItemDamage() != 0 && count != 0)
			{
				itemstack.setItemDamage(itemstack.getItemDamage() - 1);
				inventory.consumeInventoryItem(Items.arrow);

				--count;
			}

			player.worldObj.playSoundAtEntity(player, "random.pop", 1.0F, 0.35F);
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

	@Override
	public boolean isDamaged(ItemStack itemstack)
	{
		return false;
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemstack)
	{
		return itemstack.getItemDamage() > 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		switch (grade)
		{
			case 1:
				itemIcon = iconRegister.registerIcon("moreinv:arrowholder_gold");
				break;
			case 2:
				itemIcon = iconRegister.registerIcon("moreinv:arrowholder_diamond");
				break;
			default:
				itemIcon = iconRegister.registerIcon("moreinv:arrowholder_iron");
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