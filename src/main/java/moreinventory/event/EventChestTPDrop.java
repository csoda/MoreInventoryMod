package moreinventory.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moreinventory.MoreInventoryMod;
import moreinventory.util.CSutil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Created by c_soda on 14/02/26.
 */

public class EventChestTPDrop {

    @SubscribeEvent
    public void ChestTPDropEvent(LivingHurtEvent event)
    {
        EntityLivingBase entity = event.entityLiving;
        if(entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            int hungry = player.getFoodStats().getFoodLevel();
            ItemStack currentItem = player.inventory.getCurrentItem();
            float damage = event.ammount;
            if(damage >= 3){
                for(int i = 0; i < player.inventory.getSizeInventory(); i++)
                {
                    ItemStack item = player.inventory.getStackInSlot(i);
                    if(CSutil.compareItems(item, MoreInventoryMod.ChestTransporter)&&item.getItemDamage() != 0){
                        if(item != currentItem){
                            if(hungry < 10 || i > 9)
                            {
                                CSutil.dropItem(player.worldObj, item, player.posX, player.posY+2, player.posZ);
                                player.inventory.setInventorySlotContents(i, null);
                            }
                        }
                    }
                }

            }
        }
    }
}
