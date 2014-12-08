package moreinventory.item;

import java.util.List;

import moreinventory.MoreInventoryMod;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPlating extends Item
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public static final byte[] typeIndex = {1, 2, 3, 4, 5, 6, 7, 10, 12};

	public ItemPlating()
	{
		this.setHasSubtypes(true);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return false;
		}

		if (world.getBlock(x, y, z) == MoreInventoryMod.StorageBox)
		{
			int index = typeIndex[itemstack.getItemDamage()];
			int meta = world.getBlockMetadata(x, y, z);
			int tier1 = StorageBoxType.values()[index].Tier;
			int tier2 = StorageBoxType.values()[meta].Tier;

			if (index != 0 && (tier1 == tier2 || tier1 == tier2 + 1) && StorageBoxType.values()[index].invSize > StorageBoxType.values()[meta].invSize)
			{
				TileEntityStorageBox tile = (TileEntityStorageBox)world.getTileEntity(x, y, z);

				world.setBlockMetadataWithNotify(x, y, z, index, 2);
				world.setTileEntity(x, y, z, tile.upgrade(index));

				if (!player.capabilities.isCreativeMode)
				{
					--itemstack.stackSize;

					player.onUpdate();
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
		return "painting:" + StorageBoxType.values()[ItemPlating.typeIndex[itemstack.getItemDamage()]].name();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < typeIndex.length; i++)
		{
			list.add(new ItemStack(this, 1, i));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[typeIndex.length];

		for (int i = 0; i < typeIndex.length; i++)
		{
			icons[i] = iconRegister.registerIcon("moreinv:plating_" + StorageBoxType.values()[typeIndex[i]].name());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return icons[damage];
	}
}