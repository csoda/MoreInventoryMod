package moreinventory.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemTorchHolder extends Item
{
	public static final int[] maxDamage = {258, 1026, 4098};

	private final int grade;

	public ItemTorchHolder(int grade)
	{
		this.grade = grade;
		this.setMaxStackSize(1);
		this.setMaxDamage(maxDamage[grade]);
		this.setNoRepair();
		this.setContainerItem(this);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		boolean result = false;

		if (itemstack.getItemDamage() < maxDamage[grade] - 2)
		{
			if (Item.getItemFromBlock(Blocks.torch).onItemUse(new ItemStack(Blocks.torch, 1), player, world, x, y, z, side, hitX, hitY, hitZ))
			{
				itemstack.damageItem(1, player);
				result = true;
			}
		}

		if (player.isSneaking() && !result)
		{
			rechargeTorches(itemstack, player);
		}

		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (player.isSneaking())
		{
			rechargeTorches(itemstack, player);
		}

		return itemstack;
	}

	public void rechargeTorches(ItemStack itemstack, EntityPlayer player)
	{
		InventoryPlayer inventory = player.inventory;

		if (itemstack != null && itemstack.getItem() instanceof ItemTorchHolder && itemstack.getItemDamage() != 0 && !player.capabilities.isCreativeMode)
		{
			int count = 0;

			if (inventory != null)
			{
				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.torch))
					{
						count += inventory.getStackInSlot(i).stackSize;
					}
				}
			}

			while (itemstack.getItemDamage() != 0 && count != 0)
			{
				itemstack.setItemDamage(itemstack.getItemDamage() - 1);
				inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.torch));

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
				itemIcon = iconRegister.registerIcon("moreinv:Torchholder_iron");
				break;
			case 1:
				itemIcon = iconRegister.registerIcon("moreinv:Torchholder_gold");
				break;
			case 2:
				itemIcon = iconRegister.registerIcon("moreinv:Torchholder_diamond");
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		list.add(itemstack.getMaxDamage() - itemstack.getItemDamage() - 2 + " uses");
	}
}
