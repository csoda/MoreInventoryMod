package moreinventory.item.inventory;

import moreinventory.core.MoreInventoryMod;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class InventoryChestTransporter implements IInventory
{
	private final ItemStack usingItem;

	private ItemStack tileBlock;
	private ItemStack[] inventoryItems;

	public InventoryChestTransporter(ItemStack itemstack)
	{
		this.usingItem = itemstack;
		this.readFromNBT();
	}

	public boolean placeBlock(World world, EntityPlayer player, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (tileBlock == null)
		{
			return false;
		}

		boolean replaceable = false;
		Block block = Block.getBlockFromItem(tileBlock.getItem());

		if (block != null)
		{
			if (block.isReplaceable(world, x, y, z))
			{
				replaceable = true;
			}

			if (tileBlock.getItem().onItemUse(tileBlock, player, world, x, y, z, side, hitX, hitY, hitZ))
			{
				TileEntity tile;

				if (!replaceable)
				{
					int[] pos = MIMUtils.getSidePos(x, y, z, side);
					tile = world.getTileEntity(pos[0], pos[1], pos[2]);
				}
				else
				{
					tile = world.getTileEntity(x, y, z);
				}

				if (tile == null)
				{
					return false;
				}

				transferToBlock(tile);
				block.onBlockPlacedBy(world, tile.xCoord, tile.yCoord, tile.zCoord, player, tileBlock);

				return true;
			}
		}

		return false;
	}

	public boolean transferToPlayer(TileEntity tile)
	{
		if (checkMatryoshka((IInventory)tile))
		{
			NBTTagCompound nbt = usingItem.getTagCompound();

			if (nbt == null)
			{
				nbt = new NBTTagCompound();
			}

			Block block = tile.getWorldObj().getBlock(tile.xCoord, tile.yCoord, tile.zCoord);
			int meta = tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord);
			tileBlock = new ItemStack(block, 1, meta);

			tile.writeToNBT(nbt);

			IInventory inventory = (IInventory)tile;

			for (int i = 0; i < inventory.getSizeInventory(); i++)
			{
				inventory.setInventorySlotContents(i, null);
			}

			writeToNBT(nbt);

			return true;
		}

		return false;
	}

	public void transferToBlock(TileEntity tile)
	{
		NBTTagCompound nbt = usingItem.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		nbt.setInteger("x", tile.xCoord);
		nbt.setInteger("y", tile.yCoord);
		nbt.setInteger("z", tile.zCoord);
		tile.readFromNBT(nbt);
	}

	private boolean checkMatryoshka(IInventory inventory)
	{
		ItemStack itemstack;

		for (int i = 0; i < inventory.getSizeInventory(); i++)
		{
			itemstack = inventory.getStackInSlot(i);

			if (itemstack != null)
			{
				if (itemstack.getItem() == MoreInventoryMod.ChestTransporter && itemstack.getItemDamage() != 0 ||
					itemstack.getItem() == MoreInventoryMod.Pouch && !checkMatryoshka(new InventoryPouch(itemstack)))
				{
					return false;
				}
			}
		}

		return true;
	}

	public String getContentsItemName(ItemStack itemstack)
	{
		String name = "Empty";
		NBTTagCompound nbt = itemstack.getTagCompound();

		if (nbt != null)
		{
			ItemStack item = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Contents"));

			if (item != null)
			{
				name = item.getDisplayName();
			}
		}

		return name;
	}

	@Override
	public int getSizeInventory()
	{
		return inventoryItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventoryItems[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		inventoryItems[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName()
	{
		return "transporter";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack itemstack = getStackInSlot(slot);

		if (itemstack != null)
		{
			if (itemstack.stackSize <= amount)
			{
				setInventorySlotContents(slot, null);
			}
			else
			{
				itemstack = itemstack.splitStack(amount);

				if (itemstack.stackSize == 0)
				{
					setInventorySlotContents(slot, null);
				}
			}
		}

		return itemstack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack itemstack = getStackInSlot(slot);

		if (itemstack != null)
		{
			setInventorySlotContents(slot, null);
		}

		return itemstack;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	public void readFromNBT()
	{
		NBTTagCompound nbt = usingItem.getTagCompound();

		if (nbt == null)
		{
			return;
		}

		tileBlock = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("tileBlock"));
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound tag = new NBTTagCompound();

		if (tileBlock != null)
		{
			tileBlock.writeToNBT(tag);
		}

		nbt.setTag("tileBlock", tag);
		usingItem.setTagCompound(nbt);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		ItemStack itemstack = player.getCurrentEquippedItem();

		if (itemstack != usingItem)
		{
			return false;
		}

		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public void markDirty() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return false;
	}
}