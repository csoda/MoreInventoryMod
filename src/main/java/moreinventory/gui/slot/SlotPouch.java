package moreinventory.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotPouch extends Slot{

	public SlotPouch(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player){
		return false;
	}

}
