package moreinventory.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moreinventory.MoreInventoryMod;
import moreinventory.item.inventory.InvPouch;
import moreinventory.util.CSutil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class EventItemPickup {

    @SubscribeEvent
	public void autoCollectEvent(EntityItemPickupEvent event)
	{
        EntityPlayer player = event.entityPlayer;
        ItemStack enItem = event.item.getEntityItem();
        IInventory invp= player.inventory;

        ItemStack itemstack;
		for (int i = 0;i<invp.getSizeInventory();i++)
		{
			itemstack = invp.getStackInSlot(i);
			if(itemstack!=null)
			{
				if(itemstack.getItem() == MoreInventoryMod.Pouch)
				{
					InvPouch pouch = new InvPouch(itemstack);
					if(pouch.canAutoCollect(enItem))
					{
						CSutil.mergeItemStack(enItem,pouch);
					}
					if(MoreInventoryMod.isFullAutoCollectPouch){
						pouch.collectAllItemStack(invp, false);
					}
				}

				if(enItem.isItemEqual(new ItemStack(Blocks.torch))&&MoreInventoryMod.isCollectTorch){
					for(int t = 0; t<3; t++){
						if(itemstack.getItem() == MoreInventoryMod.Torchholder[t]){
							 int damage = itemstack.getItemDamage();
							 int torchCount = enItem.stackSize;
							 if(damage>=torchCount){
								 itemstack.setItemDamage(damage-torchCount);
								 enItem.stackSize = 0;
							 }
							 else
							 {
								 itemstack.setItemDamage(0);
								 enItem.stackSize -= damage;
							 }
						}
					}
				}
			}
		}

	}

}
