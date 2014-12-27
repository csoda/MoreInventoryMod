package moreinventory.plugin.appeng;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import net.minecraft.item.ItemStack;

public class MEInventoryEnderStorageBox extends MEInventoryStorageBox
{
	public MEInventoryEnderStorageBox(TileEntityEnderStorageBox tile)
	{
		super(tile);
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

		ItemStack itemstack = flag ? storage.getStackInSlot(1) : ItemStack.copyItemStack(storage.getStackInSlot(1));

		if (itemstack != null && request.isSameType(itemstack))
		{
			ItemStack retrieved;

			do
			{
				retrieved = flag ? storage.decrStackSize(1, req.stackSize) : ItemStack.copyItemStack(storage.getStackInSlot(1));

				if (retrieved != null)
				{
					gathered.stackSize += retrieved.stackSize;
					req.stackSize -= retrieved.stackSize;

					if (retrieved.stackSize <= 0)
					{
						retrieved = null;
					}
				}

				if (size == gathered.stackSize)
				{
					return AEApi.instance().storage().createItemStack(gathered);
				}
			}
			while (retrieved != null);
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
		ItemStack itemstack = storage.getStackInSlot(1);

		if (itemstack != null)
		{
			itemstack = itemstack.copy();
			itemstack.stackSize = storage.getContentItemCount();

			out.addStorage(AEApi.instance().storage().createItemStack(itemstack));
		}

		return out;
	}
}