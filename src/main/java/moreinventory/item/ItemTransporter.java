package moreinventory.item;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class ItemTransporter extends Item
{
	public static final Table<String, Integer, Integer> transportableBlocks = HashBasedTable.create();

	public static void refreshTransportableBlocks(String... blocks)
	{
		transportableBlocks.clear();

		for (String entry : blocks)
		{
			String[] args = entry.trim().split("@");

			if (args.length < 2)
			{
				continue;
			}

			transportableBlocks.put(args[0].trim(), Integer.parseInt(args[1].trim()), args.length > 2 && args[2].trim().length() > 0 ? NumberUtils.toInt(args[2].trim()) & 29 : 0);
		}
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemTransporter()
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
			((EntityClientPlayerMP)player).sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(x, y, z, side, player.inventory.getCurrentItem(), hitX, hitY, hitZ));

			return true;
		}

		if (itemstack.getItemDamage() == 0)
		{
			Block block = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			TileEntity tile;

			if (transportableBlocks.contains(MIMUtils.getUniqueName(block), -1))
			{
				meta = -1;
			}

			if (meta == -1 || transportableBlocks.contains(MIMUtils.getUniqueName(block), meta))
			{
				tile = world.getTileEntity(x, y, z);

				if (tile == null || !(tile instanceof IInventory))
				{
					return true;
				}
			}
			else return false;

			if (new Transporter(itemstack).transferToPlayer(tile))
			{
				if (block == Blocks.chest)
				{
					itemstack.setItemDamage(1);
				}
				else if (block == Blocks.trapped_chest)
				{
					itemstack.setItemDamage(2);
				}
				else if (block == Blocks.furnace)
				{
					itemstack.setItemDamage(3);
				}
				else if (block == Blocks.lit_furnace)
				{
					itemstack.setItemDamage(4);
				}
				else if (block == MoreInventoryMod.storageBox)
				{
					itemstack.setItemDamage(world.getBlockMetadata(x, y, z) + 5);
				}
				else
				{
					itemstack.setItemDamage(transportableBlocks.get(MIMUtils.getUniqueName(block), meta) + 21);
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

		if (itemstack.getItemDamage() != 0 && new Transporter(itemstack).placeBlock(world, player, x, y, z, side, hitX, hitY, hitZ))
		{
			player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(MoreInventoryMod.transporter));
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[51];
		icons[0] = iconRegister.registerIcon("moreinv:transporter");
		icons[1] = iconRegister.registerIcon("moreinv:transporter_chest");
		icons[2] = iconRegister.registerIcon("moreinv:transporter_trapchest");
		icons[3] = iconRegister.registerIcon("moreinv:transporter_furnace");
		icons[4] = iconRegister.registerIcon("moreinv:transporter_furnace_lit");


		Set typeSet = StorageBoxType.types.entrySet();
		int t = 0;
		for (Iterator i = typeSet.iterator(); i.hasNext(); t++)
		{
			Map.Entry<String, StorageBoxType> type = (Map.Entry<String, StorageBoxType>)i.next();
			String typeName = type.getKey();
			if (!"Glass".equals(typeName) && !"CobbleStone".equals(typeName) && !"Ender".equals(typeName))
			{
				icons[t + 5] = iconRegister.registerIcon("moreinv:transporter_storagebox_" + typeName.toLowerCase(Locale.ENGLISH));
			}
		}


		for (int i = 0; i < 30; ++i)
		{
			icons[i + 21] = iconRegister.registerIcon("moreinv:transporter_mod_" + i);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return icons[damage];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		int damage = itemstack.getItemDamage();

		if (3 <= damage && damage <= 18)
		{
			list.add("\"" + new Transporter(itemstack).getContentsItemName(itemstack) + "\"");
		}
	}

	class Transporter
	{
		private final ItemStack usingItem;

		private ItemStack tileBlock;

		public Transporter(ItemStack itemstack)
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

			Block block = Block.getBlockFromItem(tileBlock.getItem());

			if (block != null)
			{
				if (tileBlock.getItem().onItemUse(tileBlock, player, world, x, y, z, side, hitX, hitY, hitZ))
				{
					TileEntity tile;

					if (world.getBlock(x, y, z) == block)
					{
						tile = world.getTileEntity(x, y, z);

						if (tile == null)
						{
							world.setBlockToAir(x, y, z);

							return false;
						}
					}
					else
					{
						int[] pos = MIMUtils.getSidePos(x, y, z, side);

						if (world.getBlock(pos[0], pos[1], pos[2]) != block)
						{
							return false;
						}

						tile = world.getTileEntity(pos[0], pos[1], pos[2]);

						if (tile == null)
						{
							world.setBlockToAir(pos[0], pos[1], pos[2]);

							return false;
						}
					}

					transferToBlock(tile);
					block.onBlockPlacedBy(world, tile.xCoord, tile.yCoord, tile.zCoord, player, tileBlock);
					block.onPostBlockPlaced(world, tile.xCoord, tile.yCoord, tile.zCoord, tile.getBlockMetadata());

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
