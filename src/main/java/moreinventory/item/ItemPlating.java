package moreinventory.item;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemPlating extends Item
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public static final String[] typeNameIndex = {"Iron", "Gold", "Diamond", "Copper", "Tin", "Bronze", "Silver", "Emerald", "Steel"};

	public ItemPlating()
	{
		this.setHasSubtypes(true);
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
			String typeA = typeNameIndex[itemstack.getItemDamage()];
			String typeB =  ((TileEntityStorageBox) tile).getTypeName();
			int tierA = StorageBoxType.getTier(typeA);
			int tierB = StorageBoxType.getTier(typeB);

			if (tierB != 0 && (tierA == tierB || tierA == tierB + 1) && StorageBoxType.getInventorysize(typeA) > StorageBoxType.getInventorysize(typeB))
			{
				world.setTileEntity(x, y, z, ((TileEntityStorageBox) tile).upgrade(typeA));

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
		return "painting:" + typeNameIndex[itemstack.getItemDamage()];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < typeNameIndex.length; i++)
		{
			list.add(new ItemStack(this, 1, i));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[typeNameIndex.length];

		for (int i = 0; i < typeNameIndex.length; i++)
		{
			icons[i] = iconRegister.registerIcon("moreinv:plating_" + typeNameIndex[i]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return icons[damage];
	}
}