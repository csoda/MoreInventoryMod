package moreinventory.plugin.appeng;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.item.ItemStack;

public class MEInventoryStorageBox implements IMEInventory<IAEItemStack>
{
	protected final TileEntityStorageBox storage;

	public MEInventoryStorageBox(TileEntityStorageBox tile)
	{
		this.storage = tile;
	}

	@Override
	public IAEItemStack injectItems(IAEItemStack input, Actionable type, BaseActionSource src)
	{
		boolean flag = type == Actionable.MODULATE;
		ItemStack out = flag ? input.getItemStack() : input.getItemStack().copy();
		int max = storage.getSizeInventory();

		for (int i = 0; i < max; ++i)
		{
			if (storage.isItemValidForSlot(i, out))
			{
				ItemStack itemstack = storage.getStackInSlot(i);

				if (itemstack == null)
				{
					int size = out.stackSize;

					if (size > storage.getInventoryStackLimit())
					{
						size = storage.getInventoryStackLimit();
					}

					out.stackSize -= size;

					if (out.stackSize <= 0)
					{
						return null;
					}
				}
				else if (input.isSameType(itemstack))
				{
					int original = itemstack.stackSize;
					int size = original + out.stackSize;

					if (size > storage.getInventoryStackLimit())
					{
						size = storage.getInventoryStackLimit();
					}

					if (size > itemstack.getMaxStackSize())
					{
						size = itemstack.getMaxStackSize();
					}

					out.stackSize -= size - original;

					if (out.stackSize <= 0)
					{
						return null;
					}
				}
			}
		}

		return AEApi.instance().storage().createItemStack(out);
	}

	@Override
	public IAEItemStack extractItems(IAEItemStack request, Actionable type, BaseActionSource src)
	{
		boolean flag = type == Actionable.MODULATE;
		ItemStack gathered, req = flag ? request.getItemStack() : request.getItemStack().copy();

		int size = req.stackSize;

		if (size > req.getMaxDamage())
		{
			size = req.getMaxStackSize();
		}

		req.stackSize = size;

		gathered = flag ? request.getItemStack() : request.getItemStack().copy();
		gathered.stackSize = 0;

		int max = storage.getSizeInventory();

		for (int i = 0; i < max; ++i)
		{
			ItemStack itemstack = flag ? storage.getStackInSlot(i) : ItemStack.copyItemStack(storage.getStackInSlot(i));

			if (itemstack != null && request.isSameType(itemstack))
			{
				ItemStack retrieved;

				if (itemstack.stackSize < req.stackSize)
				{
					retrieved = itemstack.copy();
					itemstack.stackSize = 0;
				}
				else
				{
					retrieved = itemstack.splitStack(req.stackSize);
				}

				if (flag)
				{
					if (itemstack.stackSize <= 0)
					{
						storage.setInventorySlotContents(i, null);
					}
					else
					{
						storage.setInventorySlotContents(i, itemstack);
					}
				}

				gathered.stackSize += retrieved.stackSize;
				req.stackSize -= retrieved.stackSize;

				if (size == gathered.stackSize)
				{
					return AEApi.instance().storage().createItemStack(gathered);
				}
			}
		}

		if (gathered.stackSize <= 0)
		{
			return null;
		}

		return AEApi.instance().storage().createItemStack(gathered);
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out)
	{
		for (int i = 0; i < storage.getSizeInventory(); ++i)
		{
			out.addStorage(AEApi.instance().storage().createItemStack(storage.getStackInSlot(i)));
		}

		return out;
	}

	@Override
	public StorageChannel getChannel()
	{
		return StorageChannel.ITEMS;
	}
}