package moreinventory.container;

import moreinventory.gui.slot.SlotConfig;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class ContainerStorageBox  extends Container {

    protected TileEntityStorageBox tile;

    public ContainerStorageBox (InventoryPlayer inventoryPlayer, TileEntityStorageBox te){
            tile = te;
            
    		addSlotToContainer(new SlotConfig(tile, tile.getFirstItemIndex(), 30 , 16 ));
            
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
            return tile.isUseableByPlayer(player);
    }


    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        return null;
    }
}