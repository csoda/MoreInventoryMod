package moreinventory.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class ItemNoFunction extends Item
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemNoFunction()
	{
		this.setMaxStackSize(64);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return "itemnofunction" + itemstack.getItemDamage();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < MoreInventoryMod.MATERIALNAME.length; i++)
		{
			list.add(new ItemStack(this, 1, i));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[MoreInventoryMod.MATERIALNAME.length];
		icons[0] = iconRegister.registerIcon("moreinv:leatherpack");
		icons[1] = iconRegister.registerIcon("moreinv:brush");
		icons[2] = iconRegister.registerIcon("moreinv:dimension_core");
		icons[3] = iconRegister.registerIcon("moreinv:clipboard");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return icons[damage];
	}
}