package moreinventory.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabMoreInventoryMod extends CreativeTabs
{
	public CreativeTabMoreInventoryMod()
	{
		super("MoreInventoryMod");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getTranslatedTabLabel()
	{
		return getTabLabel();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getTabIconItem()
	{
		return MoreInventoryMod.torchHolder[0];
	}
}