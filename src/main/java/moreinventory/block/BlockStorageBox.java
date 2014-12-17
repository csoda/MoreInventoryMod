package moreinventory.block;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.Config;
import moreinventory.core.MoreInventoryMod;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class BlockStorageBox extends BlockContainer
{
	@SideOnly(Side.CLIENT)
	private Map<String, IIcon[]> iconMap;
	@SideOnly(Side.CLIENT)
	private IIcon[] icons_glass;
	@SideOnly(Side.CLIENT)
	private IIcon icon_air;
	@SideOnly(Side.CLIENT)
	private byte[][] glassIndex;

	public BlockStorageBox(Material material)
	{
		super(material);
		this.setBlockTextureName("moreinv:storagebox");
		this.setHardness(2.0F);
        this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int idk, float hitX, float hitY, float hitZ)
	{
		TileEntityStorageBox tile = (TileEntityStorageBox)world.getTileEntity(x, y, z);

		if (world.isRemote && StorageBoxType.compareTypes(tile, "Glass"))
		{
			return true;
		}

		ItemStack itemstack = player.getCurrentEquippedItem();

		if (itemstack != null && itemstack.getItem() == MoreInventoryMod.noFunctionItems && itemstack.getItemDamage() == 3 && !StorageBoxType.compareTypes(tile, "Glass"))
		{
			tile.sendGUIPacketToClient();
			player.openGui(MoreInventoryMod.instance, 4, world, x, y, z);

			return true;
		}

		return tile.rightClickEvent(world, player, x, y, z);
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			((TileEntityStorageBox)world.getTileEntity(x, y, z)).leftClickEvent(player);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if (block instanceof BlockStorageBox)
		{
			((TileEntityStorageBox)world.getTileEntity(x, y, z)).onNeighborRemoved();
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
	{
		if (!world.isRemote)
		{
			dropItems(world, x, y, z);
		}

		super.breakBlock(world, x, y, z, block, metadata);
	}

	private void dropItems(World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile == null || !(tile instanceof IInventory) || tile instanceof TileEntityEnderStorageBox)
		{
			return;
		}

		IInventory inventory = (IInventory)tile;

		for (int i = 0; i < inventory.getSizeInventory(); i++)
		{
			MIMUtils.dropItem(world, inventory.getStackInSlot(i), x, y, z);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
	{
		TileEntityStorageBox tile = (TileEntityStorageBox)world.getTileEntity(x, y, z);

		switch (MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3)
		{
			case 0:
				tile.face = 2;
				break;
			case 1:
				tile.face = 5;
				break;
			case 2:
				tile.face = 3;
				break;
			case 3:
				tile.face = 4;
				break;
			default:
				tile.face = 2;
				break;
		}

		tile.onPlaced(entity);
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axe)
	{
		((TileEntityStorageBox)world.getTileEntity(x, y, z)).rotateBlock();

		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return StorageBoxType.createEntity("Wood");
	}

	@Override
	public int getRenderType()
	{
		return 0;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int damageDropped(int metadata)
	{
		return metadata;
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		return tile instanceof TileEntityStorageBox ? TileEntityStorageBox.getPowerOutput((TileEntityStorageBox)tile) : 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		iconMap = Maps.newHashMap();
		icons_glass = new IIcon[16];
		icon_air = iconRegister.registerIcon("moreinv:air");

		for (Entry<String, StorageBoxType> type : StorageBoxType.types.entrySet())
		{
			String name = type.getKey().toLowerCase(Locale.ENGLISH);
			String folder = StorageBoxType.getTextureFolder(type.getKey());

			if (!type.getKey().equals("Glass"))
			{
				IIcon[] icons = new IIcon[3];
				icons[0] = iconRegister.registerIcon(folder + ":storagebox_" + name + "_side");
				icons[1] = iconRegister.registerIcon(folder + ":storagebox_" + name + "_top");
				icons[2] = iconRegister.registerIcon(folder + ":storagebox_" + name + "_face");
				iconMap.put(type.getKey(), icons);
			}
			else
			{
				IIcon[] icons = new IIcon[3];
				icons[0] = iconRegister.registerIcon(folder + ":storagebox_" + name + "_0");
				icons[1] = icons[0];
				icons[2] = icons[0];
				iconMap.put(type.getKey(), icons);
			}
		}

		for (int i = 0; i < 16; i++)
		{
			icons_glass[i] = iconRegister.registerIcon(getTextureName() + "_" + "glass_" + i);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		return iconMap.get("Wood")[0];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntityStorageBox tile = (TileEntityStorageBox)world.getTileEntity(x, y, z);
		String type = tile.getTypeName();

		if (StorageBoxType.compareTypes(tile, "Glass"))
		{
			int pos[] = MIMUtils.getSidePos(x, y, z, side);

			if (world.getBlock(pos[0], pos[1], pos[2]) == this || world.getBlock(pos[0], pos[1], pos[2]).isNormalCube())
			{
				return icon_air;
			}
			else if (Config.clearGlassBox)
			{
				return getGlassIcon(world, x, y, z, side);
			}
		}

		IIcon[] icons = iconMap.get(type);

		if (Config.containerBoxSideTexture)
		{
			return side == 0 || side == 1 ? icons[1] : side == tile.face ? icons[2] : icons[0];
		}

		return icons[0];
	}

	@SideOnly(Side.CLIENT)
	private IIcon getGlassIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		if (glassIndex == null)
		{
			glassIndex = new byte[][] {{2, 5, 3, 4}, {2, 5, 3, 4}, {1, 4, 0, 5}, {1, 5, 0, 4}, {1, 3, 0, 2}, {1, 2, 0, 3}};
		}

		byte index = 0;

		for (int i = 0; i < 4; i++)
		{
			int pos[] = MIMUtils.getSidePos(x, y, z, glassIndex[side][i]);

			if (world.getBlock(pos[0], pos[1], pos[2]) == this)
			{
				index |= 1 << i;
			}
		}

		return icons_glass[index];
	}
}