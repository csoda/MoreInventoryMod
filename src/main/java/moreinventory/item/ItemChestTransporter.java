package moreinventory.item;

import java.util.List;
import java.util.Map;

import moreinventory.MoreInventoryMod;
import moreinventory.item.inventory.InventoryChestTransporter;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.util.CSUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemChestTransporter extends Item
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemChestTransporter()
	{
		this.setMaxStackSize(1);
		this.setCreativeTab(MoreInventoryMod.customTab);
		this.setHasSubtypes(true);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return false;
		}

		if (itemstack.getItemDamage() == 0)
		{
			Block block = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			TileEntity tile;

			if (MoreInventoryMod.transportableChest.containsKey(CSUtil.getUniqueName(block)))
			{
				tile = world.getTileEntity(x, y, z);

				if (tile == null || !(tile instanceof IInventory) || ((IInventory)tile).getSizeInventory() < 27)
				{
					return true;
				}
			}
			else
			{
				return false;
			}

			if (new InventoryChestTransporter(itemstack).transferToPlayer(tile))
			{
				if (block == Blocks.chest)
				{
					itemstack.setItemDamage(1);
				}
				else if (block == Blocks.trapped_chest)
				{
					itemstack.setItemDamage(2);
				}
				else if (block == MoreInventoryMod.StorageBox)
				{
					itemstack.setItemDamage(meta + 3);
				}
				else
				{
					Map<Integer, Integer> map = MoreInventoryMod.transportableChestIcon.get(CSUtil.getUniqueName(block));

					if (map != null && map.containsKey(meta))
					{
						itemstack.setItemDamage(map.get(meta) + 19);
					}
					else if (map != null && map.containsKey(-1))
					{
						itemstack.setItemDamage(map.get(-1) + 19);
					}
					else
					{
						itemstack.setItemDamage(19);
					}
				}

				world.setBlockToAir(x, y, z);

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return true;
		}

		if (itemstack.getItemDamage() != 0)
		{
			if (new InventoryChestTransporter(itemstack).placeBlock(world, player, x, y, z, side, hitX, hitY, hitZ))
			{
				if (!player.capabilities.isCreativeMode)
				{
					itemstack.setItemDamage(0);
					itemstack.setTagCompound(null);
				}
				else
				{
					player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(MoreInventoryMod.ChestTransporter));
				}
			}
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[49];
		icons[0] = iconRegister.registerIcon("moreinv:ChestTransporter");
		icons[1] = iconRegister.registerIcon("moreinv:ChestTransporter_Chest");
		icons[2] = iconRegister.registerIcon("moreinv:ChestTransporter_TrapChest");

		for (int i = 0; i < StorageBoxType.values().length; i++)
		{
			if (i != StorageBoxType.Glass.ordinal() && i != StorageBoxType.CobbleStone.ordinal() && i != StorageBoxType.Ender.ordinal())
			{
				icons[i + 3] = iconRegister.registerIcon("moreinv:ChestTransporter_" + StorageBoxType.values()[i].name() + "Box");
			}
		}

		for (int i = 0; i < 30; i++)
		{
			icons[i + 19] = iconRegister.registerIcon("moreinv:ChestTransporter_mod" + i);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int par1)
	{
		return icons[par1];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		int damage = itemstack.getItemDamage();

		if (3 <= damage && damage <= 18)
		{
			list.add("\"" + new InventoryChestTransporter(itemstack).getContentsItemName(itemstack) + "\"");
		}
	}
}