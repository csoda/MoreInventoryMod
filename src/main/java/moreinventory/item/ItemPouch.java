package moreinventory.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemPouch extends Item
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemPouch()
	{
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote && world.getBlock(x, y, z) == Blocks.cauldron)
		{
			int meta = world.getBlockMetadata(x, y, z);
			int damage = itemstack.getItemDamage();
			int i = damage % 17;

			if (meta > 0 && i > 0)
			{
				itemstack.setItemDamage(damage - i);
				world.setBlockMetadataWithNotify(x, y, z, meta - 1, 2);

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (player.isSneaking())
		{
			TileEntity tile = world.getTileEntity(x, y, z);
			InventoryPouch inventory = new InventoryPouch(player.getCurrentEquippedItem());

			if (tile == null)
			{
				inventory.collectAllItemStack(player.inventory, true);
			}
			else if (tile instanceof IInventory)
			{
				inventory.transferToChest((IInventory) tile);
			}

			return true;
		}

		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			player.openGui(MoreInventoryMod.instance, 1, world, 0, 0, 0);
		}

		return itemstack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[17];
		icons[0] = iconRegister.registerIcon("moreinv:pouch");

		for (int i = 0; i < icons.length - 1; i++)
		{
			icons[i + 1] = iconRegister.registerIcon("moreinv:pouch_" + MoreInventoryMod.COLORNAME[i]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return icons[damage % 17];
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return "pouch" + itemstack.getItemDamage() % 17;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < 17; i++)
		{
			list.add(new ItemStack(this, 1, i));
		}
	}
}