package moreinventory.item;

import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.util.CSUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockTransportManager extends ItemBlock
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemBlockTransportManager(Block block)
	{
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		super.onItemUse(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);

		TileEntity tile;

		if (!world.getBlock(x, y, z).isReplaceable(world, x, y, z))
		{
			int[] pos = CSUtil.getSidePos(x, y, z, side);
			tile = world.getTileEntity(pos[0], pos[1], pos[2]);
		}
		else
		{
			tile = world.getTileEntity(x, y, z);
		}

		if (tile != null && tile instanceof TileEntityTransportManager)
		{
			((TileEntityTransportManager)tile).face = (byte)Facing.oppositeSide[side];
			((TileEntityTransportManager)tile).rotateBlock();
		}

		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return itemstack.getItemDamage() == 0 ? "transportmanager:importer" : "transportmanager:exporter";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[2];
		icons[0] = iconRegister.registerIcon("moreinv:Importer_Item");
		icons[1] = iconRegister.registerIcon("moreinv:Exporter_Item");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return icons[damage];
	}
}