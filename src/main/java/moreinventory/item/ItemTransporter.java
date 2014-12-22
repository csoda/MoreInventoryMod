package moreinventory.item;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
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
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

			transportableBlocks.put(args[0].trim(), Integer.parseInt(args[1].trim()), args.length > 2 && args[2].trim().length() > 0 ? NumberUtils.toInt(args[2].trim()) : 0);
		}
	}

	public static final Set<String> forceIcons = Sets.newHashSet();

	static
	{
		forceIcons.add(MIMUtils.getUniqueName(Blocks.chest));
		forceIcons.add(MIMUtils.getUniqueName(Blocks.trapped_chest));
		forceIcons.add(MIMUtils.getUniqueName(Blocks.furnace));
		forceIcons.add(MIMUtils.getUniqueName(Blocks.lit_furnace));
		forceIcons.add(MIMUtils.getUniqueName(Blocks.dispenser));
		forceIcons.add(MIMUtils.getUniqueName(Blocks.dropper));
	}

	@SideOnly(Side.CLIENT)
	public Map<String, IIcon> iconMap;
	@SideOnly(Side.CLIENT)
	public IIcon[] icon_modded;

	public ItemTransporter()
	{
		this.setMaxStackSize(1);
		this.setMaxDamage(40);
		this.setFull3D();
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			((EntityClientPlayerMP)player).sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(x, y, z, side, player.inventory.getCurrentItem(), hitX, hitY, hitZ));

			return true;
		}

		if (itemstack.getTagCompound() == null)
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
				String unique = MIMUtils.getUniqueName(block);

				if (block == MoreInventoryMod.storageBox)
				{
					itemstack.getTagCompound().setString("IconName", unique + ":" + ((TileEntityStorageBox)tile).getTypeName());
				}
				else if (forceIcons.contains(unique))
				{
					itemstack.getTagCompound().setString("IconName", unique);
				}
				else if (transportableBlocks.contains(unique, meta))
				{
					itemstack.getTagCompound().setInteger("Modded", transportableBlocks.get(unique, meta));
				}
				else return false;

				if (world.setBlockToAir(x, y, z))
				{
					itemstack.damageItem(1, player);

					if (itemstack.getItemDamage() >= itemstack.getMaxDamage())
					{
						player.destroyCurrentEquippedItem();

						world.playSoundAtEntity(player, "dig.wood", 1.5F, 0.85F);
					}
				}

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

		if (itemstack.getTagCompound() != null && new Transporter(itemstack).placeBlock(world, player, x, y, z, side, hitX, hitY, hitZ))
		{
			itemstack.setTagCompound(null);
			itemstack.damageItem(1, player);

			if (itemstack.getItemDamage() >= itemstack.getMaxDamage())
			{
				player.destroyCurrentEquippedItem();

				world.playSoundAtEntity(player, "dig.wood", 1.5F, 0.85F);
			}
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		iconMap = Maps.newHashMap();
		icon_modded = new IIcon[30];

		itemIcon = iconRegister.registerIcon("moreinv:transporter");
		iconMap.put("default", itemIcon);
		iconMap.put(MIMUtils.getUniqueName(Blocks.chest), iconRegister.registerIcon("moreinv:transporter_chest"));
		iconMap.put(MIMUtils.getUniqueName(Blocks.trapped_chest), iconRegister.registerIcon("moreinv:transporter_trapchest"));
		iconMap.put(MIMUtils.getUniqueName(Blocks.furnace), iconRegister.registerIcon("moreinv:transporter_furnace"));
		iconMap.put(MIMUtils.getUniqueName(Blocks.lit_furnace), iconRegister.registerIcon("moreinv:transporter_furnace_lit"));
		iconMap.put(MIMUtils.getUniqueName(Blocks.dispenser), iconRegister.registerIcon("moreinv:transporter_dispenser"));
		iconMap.put(MIMUtils.getUniqueName(Blocks.dropper), iconRegister.registerIcon("moreinv:transporter_dropper"));

		for (Entry<String, StorageBoxType> type : StorageBoxType.types.entrySet())
		{
			String typeName = type.getKey();

			iconMap.put(MIMUtils.getUniqueName(MoreInventoryMod.storageBox) + ":" + typeName, iconRegister.registerIcon("moreinv:transporter_storagebox_" + typeName.toLowerCase(Locale.ENGLISH)));
		}

		for (int i = 0; i < icon_modded.length; ++i)
		{
			icon_modded[i] = iconRegister.registerIcon("moreinv:transporter_mod_" + i);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack itemstack, int pass)
	{
		return getIconIndex(itemstack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconIndex(ItemStack itemstack)
	{
		NBTTagCompound nbt = itemstack.getTagCompound();

		if (nbt == null)
		{
			return iconMap.get("default");
		}

		IIcon result = null;

		if (nbt.hasKey("IconName"))
		{
			result = iconMap.get(nbt.getString("IconName"));
		}
		else if (nbt.hasKey("Modded"))
		{
			result = getModIcon(nbt.getInteger("Modded"));
		}

		return result == null ? iconMap.get("default") : result;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getModIcon(int index)
	{
		return icon_modded[MathHelper.clamp_int(index, 0, icon_modded.length - 1)];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().getString("IconName").startsWith(MIMUtils.getUniqueName(MoreInventoryMod.storageBox)))
		{
			String name = "Empty";
			ItemStack contents = ItemStack.loadItemStackFromNBT(itemstack.getTagCompound().getCompoundTag("Contents"));

			if (contents != null)
			{
				name = contents.getDisplayName();
			}

			list.add("\"" + name + "\"");
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
				boolean flag = world.getBlock(x, y, z).isReplaceable(world, x, y, z);

				if (tileBlock.getItem().onItemUse(tileBlock, player, world, x, y, z, side, hitX, hitY, hitZ))
				{
					TileEntity tile;

					if (flag)
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

					tile.markDirty();

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
					if (itemstack.getItem() == ItemTransporter.this && itemstack.getTagCompound() != null ||
						itemstack.getItem() == MoreInventoryMod.pouch && !checkMatryoshka(new InventoryPouch(itemstack)))
					{
						return false;
					}
				}
			}

			return true;
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
