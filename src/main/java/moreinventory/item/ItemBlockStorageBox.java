package moreinventory.item;

import moreinventory.tileentity.storagebox.StorageBoxType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockStorageBox extends ItemBlock
{
	public ItemBlockStorageBox(Block block)
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
		return "containerbox:" + StorageBoxType.values()[itemstack.getItemDamage()].name();
	}
}