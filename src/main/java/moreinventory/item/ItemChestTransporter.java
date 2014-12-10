package moreinventory.item;

import java.util.List;

import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemChestTransporter extends Item
{
	public static final Table<String, Integer, Integer> transportableChests = HashBasedTable.create();

	public static void refreshTransportableChests(String... chests)
	{
		transportableChests.clear();

		for (String entry : chests)
		{
			String[] args = entry.trim().split("@");

			if (args.length < 2)
			{
				continue;
			}

			transportableChests.put(args[0].trim(), Integer.parseInt(args[1].trim()), args.length > 2 && args[2].trim().length() > 0 ? NumberUtils.toInt(args[2].trim()) & 29 : 0);
		}
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemChestTransporter()
	{
		this.setMaxStackSize(1);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
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

			if (transportableChests.contains(MIMUtils.getUniqueName(block), -1))
			{
				meta = -1;
			}

			if (meta == -1 || transportableChests.contains(MIMUtils.getUniqueName(block), meta))
			{
				tile = world.getTileEntity(x, y, z);

				if (tile == null || !(tile instanceof IInventory) || ((IInventory)tile).getSizeInventory() < 27)
				{
					return true;
				}
			}
			else return false;

			if (new ChestTransporter(itemstack).transferToPlayer(tile))
			{
				if (block == Blocks.chest)
				{
					itemstack.setItemDamage(1);
				}
				else if (block == Blocks.trapped_chest)
				{
					itemstack.setItemDamage(2);
				}
				else if (block == MoreInventoryMod.storageBox)
				{
					itemstack.setItemDamage(world.getBlockMetadata(x, y, z) + 3);
				}
				else
				{
					itemstack.setItemDamage(transportableChests.get(MIMUtils.getUniqueName(block), meta) + 19);
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
			if (new ChestTransporter(itemstack).placeBlock(world, player, x, y, z, side, hitX, hitY, hitZ))
			{
				if (!player.capabilities.isCreativeMode)
				{
					itemstack.setItemDamage(0);
					itemstack.setTagCompound(null);
				}
				else
				{
					player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(MoreInventoryMod.transporter));
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
			list.add("\"" + new ChestTransporter(itemstack).getContentsItemName(itemstack) + "\"");
		}
	}

	class ChestTransporter
	{
		private final ItemStack usingItem;

		private ItemStack tileBlock;

		public ChestTransporter(ItemStack itemstack)
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
			if (tile != null && checkMatryoshka((IInventory)tile))
			{
				NBTTagCompound nbt = usingItem.getTagCompound();

				if (nbt == null)
				{
					nbt = new NBTTagCompound();
				}

				tile.writeToNBT(nbt);

				Block block = tile.getWorldObj().getBlock(tile.xCoord, tile.yCoord, tile.zCoord);
				int meta = tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord);
				tileBlock = new ItemStack(block, 1, meta);

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
					if (itemstack.getItem() == MoreInventoryMod.transporter && itemstack.getItemDamage() != 0 ||
						itemstack.getItem() == MoreInventoryMod.pouch && !checkMatryoshka(new InventoryPouch(itemstack)))
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
	}
}