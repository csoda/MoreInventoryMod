package moreinventory.item;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.block.CatchallType;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ItemBlockCatchall extends ItemBlock
{
	@SideOnly(Side.CLIENT)
	public static Map<String, IIcon> iconMap;

	public ItemBlockCatchall(Block block)
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

		if (itemstack.getTagCompound() == null || !itemstack.getTagCompound().hasKey("TypeName"))
		{
			return true;
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

		if (tile != null && tile instanceof TileEntityCatchall)
		{
			((TileEntityCatchall)tile).setTypeName(itemstack.getTagCompound().getString("TypeName"));
		}

		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		if (itemstack.getTagCompound() == null || !itemstack.getTagCompound().hasKey("TypeName"))
		{
			return super.getUnlocalizedName(itemstack);
		}

		return "tile.catchall:" + itemstack.getTagCompound().getString("TypeName");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (String type : CatchallType.types.keySet())
		{
			list.add(CatchallType.createItemStack(type));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		iconMap = Maps.newHashMap();

		for (String type : CatchallType.types.keySet())
		{
			iconMap.put(type, iconRegister.registerIcon(CatchallType.getIconName(type)));
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
		if (itemstack.getTagCompound() == null || !itemstack.getTagCompound().hasKey("TypeName"))
		{
			return Blocks.planks.getIcon(1, 0);
		}

		return iconMap.get(itemstack.getTagCompound().getString("TypeName"));
	}
}