package moreinventory.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;

@SideOnly(Side.CLIENT)
public class RenderMinecartStorageBox extends RenderMinecart
{
	@Override
	protected void func_147910_a(EntityMinecart entity, float f, Block block, int i)
	{
		super.func_147910_a(entity, f, block, i);
	}
}