package moreinventory.item;

import moreinventory.tileentity.storagebox.addon.EnumSBAddon;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockSBAddon extends ItemBlockWithMetadata
{
	public ItemBlockSBAddon(Block block)
	{
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return EnumSBAddon.values()[itemstack.getItemDamage()].name();
	}
}