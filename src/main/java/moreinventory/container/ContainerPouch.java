package moreinventory.container;

import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;

import java.util.List;
import java.util.Map;

import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import moreinventory.slot.SlotPouch;
import moreinventory.slot.SlotPouch2;
import moreinventory.slot.SlotPouchConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@ChestContainer
public class ContainerPouch extends Container
{
	private final InventoryPouch pouchInventory;

	public ContainerPouch(InventoryPlayer inventory, InventoryPouch pouchInventory)
	{
		this.pouchInventory = pouchInventory;

		int k = pouchInventory.getGrade() + 2;

		for (int i = 0; i < k; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				this.addSlotToContainer(new SlotPouchConfig(pouchInventory, j + i * 3, 182 + j * 18, 24 + i * 18));
			}
		}

		for (int i = k; i < 6; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				this.addSlotToContainer(new SlotPouchConfig(pouchInventory, j + i * 3, -2000, -2000));
			}
		}

		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new SlotPouch2(pouchInventory, j + i * 9 + 18, 8 + j * 18, 18 + i * 18));
			}
		}

		this.bindPlayerInventory(inventory);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return pouchInventory != null && pouchInventory.isUseableByPlayer(player);
	}

	protected void bindPlayerInventory(InventoryPlayer inventory)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			if (i != inventory.currentItem)
			{
				addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 194 + 4));
			}
			else
			{
				addSlotToContainer(new SlotPouch(inventory, i, 8 + i * 18, 194 + 4));
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)inventorySlots.get(i);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (itemstack1 != null && itemstack1.getItem() == MoreInventoryMod.Pouch)
			{
				return null;
			}

			if (18 <= i && i < 72)
			{
				if (!mergeItemStack(itemstack1, 72, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!mergeItemStack(itemstack1, 18, 72, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public ItemStack slotClick(int index, int button, int modifiers, EntityPlayer player)
	{
		if (0 <= index && index < 18)
		{
			SlotPouchConfig slot = (SlotPouchConfig)inventorySlots.get(index);

			if (button == 0)
			{
				slot.putStack(player.inventory.getItemStack());
			}
			else
			{
				slot.removeItem();
			}
		}
		else return super.slotClick(index, button, modifiers, player);

		return null;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		pouchInventory.closeInventory();
	}

	@ChestContainer.RowSizeCallback
	private int getRowSize()
	{
		return 54;
	}

	@ContainerSectionCallback
	public Map<ContainerSection, List<Slot>> getSlot()
	{
		Map<ContainerSection, List<Slot>> slotMap = Maps.newHashMap();
		List<Slot> slotList = Lists.newArrayList();

		for (int i = 0; i < 54; i++)
		{
			slotList.add((Slot)inventorySlots.get(i + 18));
		}

		slotMap.put(ContainerSection.CHEST, slotList);

		return slotMap;
	}
}