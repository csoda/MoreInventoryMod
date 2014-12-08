package moreinventory.item;

import java.util.List;

import moreinventory.MoreInventoryMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNoFunction extends Item
{
	public IIcon[] MetaItemTexture = new IIcon[MoreInventoryMod.MATERIALNAME.length];

	public ItemNoFunction()
	{
		super();
		setMaxStackSize(64);
		setCreativeTab(MoreInventoryMod.customTab);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack)
	{
		return "itemnofunction" + par1ItemStack.getItemDamage();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < MoreInventoryMod.MATERIALNAME.length; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		MetaItemTexture[0] = par1IconRegister.registerIcon("moreinv:leatherpack");
		MetaItemTexture[1] = par1IconRegister.registerIcon("moreinv:brush");
		MetaItemTexture[2] = par1IconRegister.registerIcon("moreinv:dimension_core");
		MetaItemTexture[3] = par1IconRegister.registerIcon("moreinv:clipboard");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int par1)
	{
		return MetaItemTexture[par1];
	}
}