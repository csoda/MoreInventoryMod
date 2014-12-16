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
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemArrowHolder extends Item
{
	private final int grade;

	@SideOnly(Side.CLIENT)
	private IIcon[] holderIcon;

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
	public boolean hasEffect(ItemStack itemstack, int pass)
	{
		refreshHolderIcon(itemstack);

		return super.hasEffect(itemstack, pass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		holderIcon = new IIcon[2];

		switch (grade)
		{
			case 0:
				holderIcon[0] = iconRegister.registerIcon("moreinv:arrowholder_iron");
				holderIcon[1] = iconRegister.registerIcon("moreinv:emptyholder_iron");
				break;
			case 1:
				holderIcon[0] = iconRegister.registerIcon("moreinv:arrowholder_gold");
				holderIcon[1] = iconRegister.registerIcon("moreinv:emptyholder_gold");
				break;
			case 2:
				holderIcon[0] = iconRegister.registerIcon("moreinv:arrowholder_diamond");
				holderIcon[1] = iconRegister.registerIcon("moreinv:emptyholder_diamond");
				break;
		}

		itemIcon = holderIcon[1];
	}

	@SideOnly(Side.CLIENT)
	public void refreshHolderIcon(ItemStack itemstack)
	{
		if (holderIcon == null)
		{
			return;
		}

		if (itemstack.getMaxDamage() - itemstack.getItemDamage() - 2 <= 0)
		{
			itemIcon = holderIcon[1];
		}
		else
		{
			itemIcon = holderIcon[0];
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		list.add(I18n.format("item.arrowholder.rest") + ": " + (itemstack.getMaxDamage() - itemstack.getItemDamage() - 2));
	}
}