package moreinventory.item.itemblock;

import moreinventory.tileentity.storagebox.StorageBoxType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockStorageBox extends ItemBlock {

	public ItemBlockStorageBox(Block block) {
		super(block);
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata (int damageValue) {
		return damageValue;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return "containerbox:" + StorageBoxType.values()[itemstack.getItemDamage()].name();
	}

}
