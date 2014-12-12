package moreinventory.handler;

import com.google.common.collect.Lists;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.Config;
import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import moreinventory.item.ItemTorchHolder;
import moreinventory.network.ConfigSyncMessage;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import moreinventory.util.MIMItemBoxList;
import moreinventory.util.MIMItemInvList;
import moreinventory.util.MIMUtils;
import moreinventory.util.Version;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Random;

public class MIMEventHooks
{
	public static final MIMEventHooks instance = new MIMEventHooks();

	protected static final Random eventRand = new Random();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event)
	{
		if (event.modID.equals(MoreInventoryMod.MODID))
		{
			Config.syncConfig();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event)
	{
		if (Version.getStatus() == Status.PENDING || Version.getStatus() == Status.FAILED)
		{
			Version.versionCheck();
		}
		else if (Version.isDev() || Config.versionNotify && Version.isOutdated())
		{
			IChatComponent component = new ChatComponentTranslation("moreinv.version.message", EnumChatFormatting.GREEN + "MoreInventoryMod" + EnumChatFormatting.RESET);
			component.appendText(" : " + EnumChatFormatting.YELLOW + Version.getLatest());
			component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, MoreInventoryMod.metadata.url));

			FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().printChatMessage(component);
		}

		event.manager.scheduleOutboundPacket(MoreInventoryMod.network.getPacketFrom(new ConfigSyncMessage(Config.isCollectTorch.contains("client"), Config.isFullAutoCollectPouch.contains("client"), Config.leftClickCatchall.contains("client"))));
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
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			ItemStack item = event.item.getEntityItem();
			InventoryPlayer inventory = player.inventory;

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

						if (Config.isFullAutoCollectPouch.contains(player.getUniqueID().toString()))
						{
							pouch.collectAllItemStack(inventory, false);
						}
					}

					if (Config.isCollectTorch.contains(player.getUniqueID().toString()) && item.getItem() == Item.getItemFromBlock(Blocks.torch) && itemstack.getItem() instanceof ItemTorchHolder)
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
	public void onArrowLoose(ArrowLooseEvent event)
	{
		if (event.bow != null && event.bow.getItem() == Items.bow)
		{
			EntityPlayer player = event.entityPlayer;
			World world = player.worldObj;
			ItemStack bow = event.bow;
			boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) > 0;

			for (Item holder : MoreInventoryMod.arrowHolder)
			{
				if (flag || player.inventory.hasItem(holder))
				{
					float f = (float)event.charge / 20.0F;
					f = (f * f + f * 2.0F) / 3.0F;

					if ((double)f < 0.1D)
					{
						return;
					}

					if (f > 1.0F)
					{
						f = 1.0F;
					}

					EntityArrow entity = new EntityArrow(world, player, f * 2.0F);

					if (f == 1.0F)
					{
						entity.setIsCritical(true);
					}

					int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bow);

					if (k > 0)
					{
						entity.setDamage(entity.getDamage() + (double)k * 0.5D + 0.5D);
					}

					int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, bow);

					if (l > 0)
					{
						entity.setKnockbackStrength(l);
					}

					if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, bow) > 0)
					{
						entity.setFire(100);
					}

					bow.damageItem(1, player);
					world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (eventRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

					if (flag)
					{
						entity.canBePickedUp = 2;
					}
					else
					{
						player.inventory.mainInventory[MIMUtils.getFirstSlot(player.inventory.mainInventory, holder)].damageItem(1, player);
					}

					if (!world.isRemote)
					{
						world.spawnEntityInWorld(entity);
					}

					event.setCanceled(true);
					break;
				}
			}
		}
	}

	@SubscribeEvent
	public void onArrowNock(ArrowNockEvent event)
	{
		if (event.result != null && event.result.getItem() == Items.bow)
		{
			EntityPlayer player = event.entityPlayer;

			for (Item holder : MoreInventoryMod.arrowHolder)
			{
				if (player.capabilities.isCreativeMode || player.inventory.hasItem(holder))
				{
					player.setItemInUse(event.result, event.result.getMaxItemUseDuration());

					break;
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