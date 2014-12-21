package moreinventory.block;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.renderer.ItemStorageBoxRenderer;
import moreinventory.core.Config;
import moreinventory.core.MoreInventoryMod;
import moreinventory.item.ItemBlockStorageBox;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class BlockStorageBox extends BlockContainer
{
	@SideOnly(Side.CLIENT)
	public Map<String, IIcon[]> iconMap;
	@SideOnly(Side.CLIENT)
	public IIcon[] icons_glass;
	@SideOnly(Side.CLIENT)
	public IIcon icon_air;
	@SideOnly(Side.CLIENT)
	public byte[][] glassIndex;

	public BlockStorageBox(Material material)
	{
		super(material);
		this.setBlockTextureName("moreinv:storagebox");
		this.setHardness(2.0F);
        this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
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
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	public Item getItemDropped(int damage, Random random, int fortune)
	{
		return null;
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
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		return tile != null && tile instanceof TileEntityStorageBox ? TileEntityStorageBox.getPowerOutput((TileEntityStorageBox)tile) : 0;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
	{
		if (!world.isRemote && !player.capabilities.isCreativeMode && willHarvest)
		{
			TileEntity tile = world.getTileEntity(x, y, z);

			if (tile != null && tile instanceof TileEntityStorageBox)
			{
				EntityItem item = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, ItemBlockStorageBox.writeToNBT(new ItemStack(this), ((TileEntityStorageBox)tile).getTypeName()));
				item.delayBeforeCanPickup = 20;

				world.spawnEntityInWorld(item);
			}
		}

		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile != null && tile instanceof TileEntityStorageBox)
		{
			return ((TileEntityStorageBox)tile).face != side.ordinal();
		}

		return super.isSideSolid(world, x, y, z, side);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile != null && tile instanceof TileEntityStorageBox)
		{
			return ItemBlockStorageBox.writeToNBT(new ItemStack(this), ((TileEntityStorageBox)tile).getTypeName());
		}

		return ItemBlockStorageBox.writeToNBT(new ItemStack(this), "Wood");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		int side = target.sideHit;
		Block block = world.getBlock(x, y, z);
		float scale = 0.1F;
		double effectX = (double)x + world.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double)(scale * 2.0F)) + (double)scale + block.getBlockBoundsMinX();
		double effectY = (double)y + world.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double)(scale * 2.0F)) + (double)scale + block.getBlockBoundsMinY();
		double effectZ = (double)z + world.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double)(scale * 2.0F)) + (double)scale + block.getBlockBoundsMinZ();

		switch (side)
		{
			case 0:
				effectY = (double)y + block.getBlockBoundsMinY() - (double)scale;
				break;
			case 1:
				effectY = (double)y + block.getBlockBoundsMaxY() + (double)scale;
				break;
			case 2:
				effectZ = (double)z + block.getBlockBoundsMinZ() - (double)scale;
				break;
			case 3:
				effectZ = (double)z + block.getBlockBoundsMaxZ() + (double)scale;
				break;
			case 4:
				effectX = (double)x + block.getBlockBoundsMinX() - (double)scale;
				break;
			case 5:
				effectX = (double)x + block.getBlockBoundsMaxX() + (double)scale;
				break;
		}

		EntityDiggingFX effect = new EntityDiggingFX(world, effectX, effectY, effectZ, 0.0D, 0.0D, 0.0D, block, world.getBlockMetadata(x, y, z));
		effect.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
		effect.setParticleIcon(block.getIcon(world, x, y, z, side));

		effectRenderer.addEffect(effect);

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
	{
		Block block = world.getBlock(x, y, z);
		byte scale = 4;

		for (int effectX = 0; effectX < scale; ++effectX)
		{
			for (int effectY = 0; effectY < scale; ++effectY)
			{
				for (int effectZ = 0; effectZ < scale; ++effectZ)
				{
					double renderX = (double)x + ((double)effectX + 0.5D) / (double)scale;
					double renderY = (double)y + ((double)effectY + 0.5D) / (double)scale;
					double renderZ = (double)z + ((double)effectZ + 0.5D) / (double)scale;
					int side = world.rand.nextInt(6);
					EntityDiggingFX effect = new EntityDiggingFX(world, renderX, renderY, renderZ, renderX - (double)x - 0.5D, renderY - (double)y - 0.5D, renderZ - (double)z - 0.5D, block, meta, side);
					effect.setParticleIcon(block.getIcon(world, x, y, z, side));

					effectRenderer.addEffect(effect);
				}
			}
		}

		return true;
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
			IIcon[] icons = new IIcon[3];

			if (!type.getKey().equals("Glass"))
			{
				icons[0] = iconRegister.registerIcon(folder + ":storagebox_" + name + "_side");
				icons[1] = iconRegister.registerIcon(folder + ":storagebox_" + name + "_top");
				icons[2] = iconRegister.registerIcon(folder + ":storagebox_" + name + "_face");
				iconMap.put(type.getKey(), icons);

				ItemStorageBoxRenderer.textureMap.put(type.getKey(), new ResourceLocation(folder, "textures/blocks/storagebox_" + name + "_side.png"));
			}
			else
			{
				icons[0] = iconRegister.registerIcon(folder + ":storagebox_" + name + "_0");
				icons[1] = icons[0];
				icons[2] = icons[0];
				iconMap.put(type.getKey(), icons);

				ItemStorageBoxRenderer.textureMap.put(type.getKey(), new ResourceLocation(folder, "textures/blocks/storagebox_" + name + "_0.png"));
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