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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemBlockStorageBox extends ItemBlock
{

	public ItemBlockStorageBox(Block block)
	{
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tile;
		boolean flg = !world.getBlock(x, y, z).isReplaceable(world, x, y, z) ? true : false;

		super.onItemUse(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);

		if (flg)
		{
			int[] pos = MIMUtils.getSidePos(x, y, z, side);
			tile = world.getTileEntity(pos[0], pos[1], pos[2]);
		}
		else
		{
			tile = world.getTileEntity(x, y, z);
		}

		if (tile != null && tile instanceof TileEntityStorageBox)
		{
			String type = this.readTypeNameFromNBT(itemstack.getTagCompound());
			TileEntityStorageBox newTile = ((TileEntityStorageBox) tile).upgrade(type);
			world.setTileEntity(tile.xCoord, tile.yCoord, tile.zCoord, newTile);
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		Set typeSet = StorageBoxType.types.entrySet();
		for (Iterator i = typeSet.iterator(); i.hasNext();)
		{
			Map.Entry<String, StorageBoxType> type = (Map.Entry<String, StorageBoxType>)i.next();

			ItemStack itemstack = new ItemStack(this);
			this.writeToNBT(itemstack, type.getKey());
			list.add(itemstack);
		}

	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return "containerbox:" + this.readTypeNameFromNBT(itemstack.getTagCompound());
	}

	public static String readTypeNameFromNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return "Wood";
		}

		return nbt.getString("TypeName");
	}

	public static void writeToNBT(ItemStack itemstack, String type)
	{
		if (itemstack != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("TypeName", type);
			itemstack.setTagCompound(nbt);
		}
	}
}