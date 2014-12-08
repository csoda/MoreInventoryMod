package moreinventory.item;

import java.util.List;

import moreinventory.MoreInventoryMod;
import moreinventory.item.inventory.InventoryPouch;
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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPouch extends Item
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemPouch()
	{
		super();
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(MoreInventoryMod.customTab);
		hasSubtypes = true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		if (!world.isRemote && world.getBlock(par4, par5, par6).equals(Blocks.cauldron))
		{
			int meta = world.getBlockMetadata(par4, par5, par6);
			int dm = itemstack.getItemDamage();
			int k = dm % 17;
			if (meta > 0 && k > 0)
			{
				itemstack.setItemDamage(dm - k);
				world.setBlockMetadataWithNotify(par4, par5, par6, meta - 1, 2);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack peritemstack, EntityPlayer player, World world, int par4, int par5, int par6,
			int par7, float par8, float par9, float par10)
	{
		if (player.isSneaking())
		{
			TileEntity tileEntity = world.getTileEntity(par4, par5, par6);
			InventoryPouch pouch = new InventoryPouch(player.getCurrentEquippedItem());
			if (tileEntity == null)
			{
				pouch.collectAllItemStack(player.inventory, true);
			}
			else if (tileEntity instanceof IInventory)
			{
				pouch.transferToChest((IInventory) tileEntity);
			}

			return true;
		}

		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack peritemstack, World world, EntityPlayer player)
	{
		player.openGui(MoreInventoryMod.instance, 1, world, 0, 0, 0);
		return peritemstack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[17];
		icons[0] = iconRegister.registerIcon("moreinv:pouch");
		for (int i = 0; i < 16; i++)
		{
			icons[i + 1] = iconRegister.registerIcon("moreinv:pouch_" + MoreInventoryMod.COLORNAME[i]);
		}

	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int par1)
	{
		return icons[par1 % 17];
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return "pouch" + itemstack.getItemDamage() % 17;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 17; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}
