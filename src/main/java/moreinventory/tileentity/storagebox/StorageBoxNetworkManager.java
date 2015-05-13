package moreinventory.tileentity.storagebox;

import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import moreinventory.util.MIMBoxList;
import moreinventory.util.MIMItemBoxList;
import moreinventory.util.MIMUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StorageBoxNetworkManager
{
	private final String ownerID;
	private final Map<String, MIMBoxList> networkMap = new HashMap();


	public StorageBoxNetworkManager(World world, int x, int y, int z, String uuid)
	{
		this.ownerID = uuid;
		this.createNetwork(new MIMBoxList(), world, x, y, z);
	}

	public MIMItemBoxList getBoxList()
	{
		return (MIMItemBoxList) getListFromID("StorageBox");
	}

	public MIMBoxList getListFromID(String id)
	{
		if(networkMap.containsKey(id))
		{
			return networkMap.get(id);
		}
		else
		{
			return null;
		}
	}

	public MIMBoxList getKnownList()
	{
		MIMBoxList list = new MIMBoxList();

		Set<Map.Entry <String, MIMBoxList>> networkSet = networkMap.entrySet();

		for (Map.Entry<String, MIMBoxList> set : networkSet)
		{
			list.addAllBox(set.getValue());
		}

		return list;
	}

	private void createNetwork(MIMBoxList list, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile != null && tile instanceof IStorageBoxNet)
		{
			IStorageBoxNet storage = (IStorageBoxNet)tile;

			if (list.addBox(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId))
			{
				if (!storage.isPrivate() || storage.getOwner().equals(MoreInventoryMod.defaultOwnerID) || storage.getOwner().equals(ownerID))
				{
					addToNetWork(tile);
					storage.setStorageBoxNetworkManager(this);
				}
			}

			for (int i = 0; i < 6; ++i)
			{
				int[] pos = MIMUtils.getSidePos(x, y, z, i);

				if (!list.isOnBoxList(pos[0], pos[1], pos[2], world.provider.dimensionId))
				{
					createNetwork(list, world, pos[0], pos[1], pos[2]);
				}
			}
		}
	}

	private void addToNetWork(TileEntity tile)
	{

		String id = ((IStorageBoxNet)tile).getSBNetID();

		if (!networkMap.containsKey(id))
		{
			if ("StorageBox".equals(id))
			{
				networkMap.put(id, new MIMItemBoxList());
			}
			else
			{
				networkMap.put(id, new MIMBoxList());
			}
		}

		if ("StorageBox".equals(id))
		{
			((MIMItemBoxList)networkMap.get(id)).addBox(tile.xCoord, tile.yCoord, tile.zCoord,
					tile.getWorldObj().provider.dimensionId, ((TileEntityStorageBox) tile).getContents());
		}
		else
		{
			networkMap.get(id).addBox(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId);
		}

	}


	public void addNetwork(World world, int x, int y, int z)
	{
		createNetwork(getKnownList(), world, x, y, z);
	}

	public void recreateNetwork()
	{
		MIMBoxList list = getKnownList();
		int count = 0;

		while (list.getListSize() > 0)
		{
			if (list.getTileBeyondDim(0) == null)
			{
				list.removeBox(0);
			}
			else
			{
				int[] pos = list.getBoxPos(0);
				StorageBoxNetworkManager manager = new StorageBoxNetworkManager(
						DimensionManager.getWorld(list.getDimensionID(0)), pos[0], pos[1], pos[2], ownerID);
				list = list.getDifference(manager.getKnownList());
			}

			if (++count > 100)
			{
				break;
			}
		}
	}

	public MIMBoxList getMatchingList(ItemStack itemstack)
	{
		MIMBoxList list = new MIMBoxList();

		if (itemstack != null)
		{
			for (int i = 0; i < getBoxList().getListSize(); i++)
			{
				if (MIMUtils.compareStacksWithDamage(itemstack, getBoxList().getItem(i)))
				{
					TileEntityStorageBox tile = (TileEntityStorageBox)getBoxList().getTileBeyondDim(i);

					if (tile != null)
					{
						list.addBox(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId);
					}
				}
			}
		}

		return list;
	}

	public void linkedCollect(IInventory inventory)
	{
		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null)
			{
				if (item.getItem() == MoreInventoryMod.pouch)
				{
					InventoryPouch pouch = new InventoryPouch(item);

					if (pouch.isCollectedByBox)
					{
						pouch.linkedPutIn(this);
					}
				}
				else
				{
					linkedPutIn(item, null, false);
				}
			}
		}
	}

	public boolean linkedPutIn(ItemStack itemstack, TileEntityStorageBox storageBox, boolean register)
	{
		MIMBoxList list = getMatchingList(itemstack);

		for (int i = 0; i < list.getListSize(); i++)
		{
			TileEntityStorageBox tile = (TileEntityStorageBox)list.getTileBeyondDim(i);

			if (tile != storageBox && !tile.isFull())
			{
				tile.tryPutIn(itemstack);

				if (itemstack == null || itemstack.stackSize == 0)
				{
					return true;
				}
			}
		}

		if (register)
		{
			for (int i = 0; i < getBoxList().getListSize(); ++i)
			{
				if (getBoxList().getItem(i) == null)
				{
					TileEntityStorageBox tile = (TileEntityStorageBox)getBoxList().getTileBeyondDim(i);

					if (!StorageBoxType.compareTypes(tile, "Glass") && !StorageBoxType.compareTypes(tile, "Ender") && tile.getContents() == null)
					{
						tile.tryPutIn(itemstack);

						if (itemstack == null)
						{
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected boolean canLinkedImport(ItemStack itemstack, TileEntityStorageBox storageBox)
	{
		for (int i = 0; i < getBoxList().getListSize(); i++)
		{
			if (MIMUtils.compareStacksWithDamage(itemstack, getBoxList().getItem(i)))
			{
				TileEntityStorageBox tile = (TileEntityStorageBox)getBoxList().getTileBeyondDim(i);

				if (tile != null && tile != storageBox && tile.canMergeItemStack(itemstack))
				{
					return true;
				}
			}
		}

		return false;
	}
}