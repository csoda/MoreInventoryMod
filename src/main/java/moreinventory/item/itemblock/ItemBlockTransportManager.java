package moreinventory.item.itemblock;

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
	private IIcon[] icons = new IIcon[2];

	public ItemBlockTransportManager(Block block)
	{
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damageValue)
	{
		return damageValue;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);

		boolean flg = false;

		if (par3World.getBlock(par4, par5, par6).isReplaceable(par3World, par4, par5, par6))
		{
			flg = true;
		}

		TileEntity tileEntity;
		if (!flg)
		{
			int[] pos = CSUtil.getSidePos(par4, par5, par6, par7);
			tileEntity = par3World.getTileEntity(pos[0], pos[1], pos[2]);
		}
		else
		{
			tileEntity = par3World.getTileEntity(par4, par5, par6);
		}

		if (tileEntity instanceof TileEntityTransportManager)
		{
			((TileEntityTransportManager) tileEntity).face = (byte) Facing.oppositeSide[par7];
			((TileEntityTransportManager) tileEntity).rotateBlock();
		}

		return true;

	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return itemstack.getItemDamage() == 0 ? "transportmanager:importer" : "transportmanager:exporter";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		this.icons[0] = iconRegister.registerIcon("moreinv:Importer_Item");
		this.icons[1] = iconRegister.registerIcon("moreinv:Exporter_Item");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int par1)
	{
		return icons[par1];
	}

	public boolean canProvidePower()
	{
		return true;
	}
}