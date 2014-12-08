package moreinventory.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSpanner extends Item
{

	public ItemSpanner()
	{
		super();
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		if (!player.isSneaking())
		{
			Block block = world.getBlock(x, y, z);
			if (block != null && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side)))
			{
				player.swingItem();
				return !world.isRemote;
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon("moreinv:spanner_CS");
	}

}
