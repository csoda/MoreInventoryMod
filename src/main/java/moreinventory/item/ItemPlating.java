package moreinventory.item;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class ItemPlating extends Item
{
	@SideOnly(Side.CLIENT)
	private Map<String, IIcon> iconMap;

	public ItemPlating()
	{
		this.setFull3D();
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return false;
		}

		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile != null && tile instanceof TileEntityStorageBox)
		{
			String typeA = readTypeNameFromNBT(itemstack.getTagCompound());
			String typeB =  ((TileEntityStorageBox)tile).getTypeName();
			int tierA = StorageBoxType.getTier(typeA);
			int tierB = StorageBoxType.getTier(typeB);

			if (tierB != 0 && (tierA == tierB || tierA == tierB + 1) && StorageBoxType.getInventorySize(typeA) > StorageBoxType.getInventorySize(typeB))
			{
				world.setTileEntity(x, y, z, ((TileEntityStorageBox)tile).upgrade(typeA));
				world.markBlockForUpdate(x, y, z);

				if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
				{
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}

				return true;
			}

			return false;
		}

		return false;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		NBTTagCompound nbt = itemstack.getTagCompound();

		return nbt == null || !nbt.hasKey("TypeName") ? "painting" : "painting:" + readTypeNameFromNBT(nbt);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (Entry<String, StorageBoxType> type : StorageBoxType.types.entrySet())
		{
			if (type.getValue().materials != null && type.getValue().materials.length > 0)
			{
				list.add(ItemBlockStorageBox.writeToNBT(new ItemStack(this), type.getKey()));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		iconMap = Maps.newHashMap();

		for (Entry<String, StorageBoxType> type : StorageBoxType.types.entrySet())
		{
			String folder = type.getValue().textureFolder;

			if (type.getValue().materials != null && type.getValue().materials.length > 0)
			{
				iconMap.put(type.getKey(), iconRegister.registerIcon(folder + ":plating_" + type.getKey().toLowerCase(Locale.ENGLISH)));
			}
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
		return iconMap.get(readTypeNameFromNBT(itemstack.getTagCompound()));
	}

	public static String readTypeNameFromNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return "Iron";
		}

		return nbt.getString("TypeName");
	}
}