package moreinventory.item;

import moreinventory.tileentity.storagebox.addon.EnumSBAddon;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockSBAddon extends ItemBlock
{
	public ItemBlockSBAddon(Block block)
	{
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return EnumSBAddon.values()[itemstack.getItemDamage()].name();
	}
}