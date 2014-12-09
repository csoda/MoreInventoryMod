package moreinventory.handler;

import moreinventory.container.ContainerCatchall;
import moreinventory.container.ContainerPotionHolder;
import moreinventory.container.ContainerPouch;
import moreinventory.container.ContainerStorageBox;
import moreinventory.container.ContainerTeleporter;
import moreinventory.container.ContainerTransportManager;
import moreinventory.gui.GuiCatchall;
import moreinventory.gui.GuiPotionHolder;
import moreinventory.gui.GuiPouch;
import moreinventory.gui.GuiStorageBox;
import moreinventory.gui.GuiTeleporter;
import moreinventory.gui.GuiTransportManager;
import moreinventory.item.inventory.InventoryPotionHolder;
import moreinventory.item.inventory.InventoryPouch;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MIMGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (id)
		{
			case 0:
				return new ContainerCatchall(player.inventory, (TileEntityCatchall)world.getTileEntity(x, y, z));
			case 1:
				InventoryPouch inventory = new InventoryPouch(player, player.getCurrentEquippedItem());
				inventory.sendPacket();

				return new ContainerPouch(player.inventory, inventory);
			case 2:
				return new ContainerTransportManager(player.inventory, (TileEntityTransportManager)world.getTileEntity(x, y, z));
			case 3:
				return new ContainerPotionHolder(player.inventory, new InventoryPotionHolder(player.getCurrentEquippedItem()));
			case 4:
				return new ContainerStorageBox(player.inventory, (TileEntityStorageBox)world.getTileEntity(x, y, z));
			case 5:
				return new ContainerTeleporter(player.inventory, (TileEntityTeleporter)world.getTileEntity(x, y, z));
			default:
				return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (id)
		{
			case 0:
				return new GuiCatchall(player.inventory, (TileEntityCatchall)world.getTileEntity(x, y, z));
			case 1:
				return new GuiPouch(player.inventory, new InventoryPouch(player, player.getCurrentEquippedItem()));
			case 2:
				return new GuiTransportManager(player.inventory, (TileEntityTransportManager)world.getTileEntity(x, y, z));
			case 3:
				return new GuiPotionHolder(player.inventory, new InventoryPotionHolder(player.getCurrentEquippedItem()));
			case 4:
				return new GuiStorageBox(player.inventory, (TileEntityStorageBox)world.getTileEntity(x, y, z));
			case 5:
				return new GuiTeleporter(player.inventory, (TileEntityTeleporter)world.getTileEntity(x, y, z));
			default:
				return null;
		}
	}
}