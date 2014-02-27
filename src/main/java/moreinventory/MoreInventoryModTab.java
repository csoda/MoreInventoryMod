package moreinventory;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class MoreInventoryModTab extends CreativeTabs {

	public MoreInventoryModTab(String label){
		super(label);
	}

	@Override
	public Item getTabIconItem() {
	    return MoreInventoryMod.Torchholder[0];
	}

}
