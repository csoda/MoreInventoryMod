package moreinventory.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import moreinventory.container.*;
import moreinventory.item.inventory.InvPotionholder;
import moreinventory.item.inventory.InvPouch;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world,int x, int y, int z) {
    	TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(id==0)
		{
			return new ContainerCatchall(player.inventory, (TileEntityCatchall) tileEntity);
		}
		else if(id==1)
		{
            InvPouch inv = new InvPouch(player,player.getCurrentEquippedItem());
            inv.sendPacketToClient();
			return new ContainerPouch(player.inventory,inv);
		}
		else if(id==2)
		{
			return new ContainerTransportManager(player.inventory, (TileEntityTransportManager) tileEntity);
		}
		else if(id==3)
		{
			return new ContainerPotionholder(player.inventory,new InvPotionholder(player.getCurrentEquippedItem()));
		}
		else if(id==4)
		{
			return new ContainerStorageBox(player.inventory,(TileEntityStorageBox) tileEntity);
		}
		else if(id==5)
		{
			return new ContainerTeleporter(player.inventory, (TileEntityTeleporter) tileEntity);
		}

        return null;
    }

    //returns an instance of the Gui you made earlier
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world,int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(id==0)
		{
			return new GuiCatchall(player.inventory, (TileEntityCatchall) tileEntity);
		}
		else if(id==1)
		{
			return new GuiPouch(player.inventory,new InvPouch(player,player.getCurrentEquippedItem()));
		}
		else if(id==2)
		{
			return new GuiTransportManager(player.inventory, (TileEntityTransportManager) tileEntity);
		}
		else if(id==3)
		{
			return new GuiPotionholder(player.inventory,new InvPotionholder(player.getCurrentEquippedItem()));
		}
		else if(id==4){
			return new GuiStorageBox(player.inventory,(TileEntityStorageBox)tileEntity);
		}
		else if(id==5){
			return new GuiTeleporter(player.inventory,(TileEntityTeleporter)tileEntity);
		}

        return null;

        }
}