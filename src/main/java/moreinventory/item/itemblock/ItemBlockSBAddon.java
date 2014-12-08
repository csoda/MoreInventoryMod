package moreinventory.item.itemblock;

import moreinventory.tileentity.storagebox.addon.EnumSBAddon;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockSBAddon extends ItemBlock
{

	public ItemBlockSBAddon(Block block)
	{
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damageValue)
	{
		return damageValue;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return EnumSBAddon.values()[itemstack.getItemDamage()].name();
	}

}
