package moreinventory.handler;

import com.google.common.collect.Lists;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.Config;
import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import moreinventory.item.ItemTorchHolder;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import moreinventory.util.MIMItemBoxList;
import moreinventory.util.MIMItemInvList;
import moreinventory.util.MIMUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;

public class MIMEventHooks
{
	public static final MIMEventHooks instance = new MIMEventHooks();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event)
	{
		if (event.modID.equals(MoreInventoryMod.MODID))
		{
			Config.syncConfig();
		}
	}

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event)
	{
		if (event.entityLiving instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;

			if (event.ammount >= 3)
			{
				for (int i = 0; i < player.inventory.getSizeInventory(); i++)
				{
					ItemStack itemstack = player.inventory.getStackInSlot(i);

					if (MIMUtils.compareItems(itemstack, MoreInventoryMod.transporter) && itemstack.getItemDamage() != 0 && itemstack != player.inventory.getCurrentItem())
					{
						if (player.getFoodStats().getFoodLevel() < 10 || i > 9)
						{
							MIMUtils.dropItem(player.worldObj, itemstack, player.posX, player.posY + 1.5D, player.posZ);

							player.inventory.setInventorySlotContents(i, null);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP)
		{
			ItemStack item = event.item.getEntityItem();
			InventoryPlayer inventory = event.entityPlayer.inventory;

			for (int i = 0; i < inventory.getSizeInventory(); i++)
			{
				ItemStack itemstack = inventory.getStackInSlot(i);

				if (itemstack != null)
				{
					if (itemstack.getItem() == MoreInventoryMod.pouch)
					{
						InventoryPouch pouch = new InventoryPouch(itemstack);

						if (pouch.canAutoCollect(item))
						{
							MIMUtils.mergeItemStack(item, pouch);
						}

						if (Config.isFullAutoCollectPouch)
						{
							pouch.collectAllItemStack(inventory, false);
						}
					}

					if (Config.isCollectTorch && item.getItem() == Item.getItemFromBlock(Blocks.torch) && itemstack.getItem() instanceof ItemTorchHolder)
					{
						int damage = itemstack.getItemDamage();
						int count = item.stackSize;

						if (damage >= count)
						{
							itemstack.setItemDamage(damage - count);
							item.stackSize = 0;
						}
						else
						{
							itemstack.setItemDamage(0);
							item.stackSize -= damage;
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if (!event.world.isRemote)
		{
			if (MoreInventoryMod.saveHelper == null || !MoreInventoryMod.saveHelper.world.getSaveHandler().getWorldDirectoryName().equals(event.world.getSaveHandler().getWorldDirectoryName()))
			{
				TileEntityEnderStorageBox.itemList = new MIMItemInvList("EnderStorageBoxInv");
				TileEntityEnderStorageBox.enderBoxList = new MIMItemBoxList("EnderStorageBox");
				TileEntityTeleporter.teleporterList = new MIMItemBoxList("Teleporter");
				MoreInventoryMod.saveHelper = new MIMWorldSaveHelper(event.world, "MoreInvData", Lists.newArrayList(TileEntityEnderStorageBox.itemList, TileEntityEnderStorageBox.enderBoxList, TileEntityTeleporter.teleporterList));
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		if (!event.world.isRemote && event.world.provider.dimensionId == 0)
		{
			if (MoreInventoryMod.saveHelper != null)
			{
				MoreInventoryMod.saveHelper.saveData();
			}
		}
	}
}