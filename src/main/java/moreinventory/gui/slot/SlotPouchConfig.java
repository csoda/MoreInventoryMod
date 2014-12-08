package moreinventory.gui.slot;

import moreinventory.item.inventory.InventoryPouch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPouchConfig extends Slot
{
	private InventoryPouch invpouch;

	public SlotPouchConfig(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
		invpouch = (InventoryPouch) this.inventory;

	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer)
	{
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		return false;
	}

	@Override
	public void putStack(ItemStack par1ItemStack)
	{
		ItemStack item = null;
		if (par1ItemStack != null)
		{
			item = par1ItemStack.copy();
			item.stackSize = 0;
		}
		invpouch.setConfigItem(this.getSlotIndex(), item);
		this.onSlotChanged();

	}

	public void removeItem()
	{
		invpouch.setInventorySlotContents(this.getSlotIndex(), (ItemStack) null);
	}
}