package moreinventory.item;

import moreinventory.core.MoreInventoryMod;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSpanner extends Item
{
	public ItemSpanner()
	{
		this.setTextureName("moreinv:spanner_CS");
		this.setMaxStackSize(1);
		this.setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
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
}