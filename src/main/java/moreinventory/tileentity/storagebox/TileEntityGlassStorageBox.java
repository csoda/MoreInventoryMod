package moreinventory.tileentity.storagebox;

import moreinventory.util.CSUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntityGlassStorageBox extends TileEntityStorageBox
{
	public TileEntityGlassStorageBox()
	{
		super(StorageBoxType.Glass);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		if (stack != null)
		{
			getStorageBoxNetworkManager().linkedPutIn(stack, this, false);
			CSUtil.dropItem(worldObj, stack, xCoord, yCoord, zCoord);

			if (stack.stackSize > getInventoryStackLimit())
			{
				stack.stackSize = getInventoryStackLimit();
			}
		}
		else
		{
			inv[slot] = null;
		}

		markDirty();
	}

	@Override
	public boolean rightClickEvent(World world, EntityPlayer player, int x, int y, int z)
	{
		clickCount++;
		if (clickTime == 0)
			clickTime = 10;
		if (clickCount == 3)
		{
			getStorageBoxNetworkManager().linkedCollect(player.inventory);
			player.onUpdate();
			clickTime = 0;
			clickCount = 0;
			return true;
		}
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
	{
		return getStorageBoxNetworkManager().canLinkedImport(par2ItemStack, this);
	}

	@Override
	public void sendPacket()
	{
	}

	@Override
	public void sendContents()
	{
	}

}
