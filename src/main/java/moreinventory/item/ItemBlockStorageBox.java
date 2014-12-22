package moreinventory.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlockStorageBox extends ItemBlock
{
	public ItemBlockStorageBox(Block block)
	{
		super(block);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		boolean flag = world.getBlock(x, y, z).isReplaceable(world, x, y, z);

		if (!super.onItemUse(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ))
		{
			return false;
		}

		TileEntity tile;

		if (flag)
		{
			tile = world.getTileEntity(x, y, z);
		}
		else
		{
			int[] pos = MIMUtils.getSidePos(x, y, z, side);

			tile = world.getTileEntity(pos[0], pos[1], pos[2]);
		}

		if (tile != null && tile instanceof TileEntityStorageBox)
		{
			TileEntityStorageBox storage = (TileEntityStorageBox)tile;

			storage.setTypeName(readTypeNameFromNBT(itemstack.getTagCompound()));
			world.setTileEntity(tile.xCoord, tile.yCoord, tile.zCoord, storage.upgrade(storage.getTypeName()));

			storage.markDirty();
		}

		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		NBTTagCompound nbt = itemstack.getTagCompound();

		return nbt == null || !nbt.hasKey("TypeName") ? "containerbox" : "containerbox:" + readTypeNameFromNBT(nbt);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (String type : StorageBoxType.types.keySet())
		{
			list.add(writeToNBT(new ItemStack(this), type));
		}
	}

	public static String readTypeNameFromNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return "Wood";
		}

		return nbt.getString("TypeName");
	}

	public static ItemStack writeToNBT(ItemStack itemstack, String type)
	{
		if (itemstack != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("TypeName", type);
			itemstack.setTagCompound(nbt);

			return itemstack;
		}

		return null;
	}
}