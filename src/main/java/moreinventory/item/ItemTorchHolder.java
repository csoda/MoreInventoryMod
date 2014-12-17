package moreinventory.item;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemTorchHolder extends Item
{
	@SideOnly(Side.CLIENT)
	private Map<String, IIcon> iconMap;

	public ItemTorchHolder()
	{
		this.setMaxStackSize(1);
		this.setNoRepair();
		this.setContainerItem(this);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		if (itemstack.getTagCompound() == null)
		{
			return super.getUnlocalizedName();
		}

		return super.getUnlocalizedName() + ":" + itemstack.getTagCompound().getString("Type");
	}

	@Override
	public int getMaxDamage(ItemStack itemstack)
	{
		if (itemstack.getTagCompound() == null)
		{
			return 256 + 2;
		}

		return TorchHolderType.getCapacity(itemstack.getTagCompound().getString("Type")) + 2;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		boolean result = false;

		if (itemstack.getItemDamage() < itemstack.getMaxDamage() - 2)
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

			for (int i = 0; i < inventory.getSizeInventory(); i++)
			{
				if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.torch))
				{
					count += inventory.getStackInSlot(i).stackSize;
				}
			}

			while (itemstack.getItemDamage() != 0 && count != 0)
			{
				itemstack.setItemDamage(itemstack.getItemDamage() - 1);
				inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.torch));

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
		iconMap = Maps.newHashMap();

		for (Entry<String, TorchHolderType> entry : TorchHolderType.types.entrySet())
		{
			String name = entry.getKey();
			TorchHolderType type = entry.getValue();

			iconMap.put(name, iconRegister.registerIcon(type.iconName));
			iconMap.put(name + ":empty", iconRegister.registerIcon(type.emptyIconName));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack itemstack, int pass)
	{
		return getIconIndex(itemstack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconIndex(ItemStack itemstack)
	{
		if (itemstack.getTagCompound() == null)
		{
			return iconMap.get("Iron");
		}

		String type = itemstack.getTagCompound().getString("Type");
		IIcon icon = null;

		if (itemstack.getMaxDamage() - itemstack.getItemDamage() - 2 <= 0)
		{
			icon = iconMap.get(type + ":empty");
		}

		if (icon == null)
		{
			icon = iconMap.get(type);
		}

		return icon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		list.add(I18n.format("item.torchholder.rest") + ": " + (itemstack.getMaxDamage() - itemstack.getItemDamage() - 2));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (String type : TorchHolderType.types.keySet())
		{
			list.add(TorchHolderType.createItemStack(type));
		}
	}
}