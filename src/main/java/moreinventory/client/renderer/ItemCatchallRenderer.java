package moreinventory.client.renderer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.block.CatchallType;
import moreinventory.client.model.ModelCatchall;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ItemCatchallRenderer implements IItemRenderer
{
	private final ModelCatchall model = new ModelCatchall();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (item != null && item.getTagCompound() != null && item.getTagCompound().hasKey("TypeName"))
		{
			mc.getTextureManager().bindTexture(CatchallType.getTexturePath(item.getTagCompound().getString("TypeName")));
		}
		else
		{
			mc.getTextureManager().bindTexture(CatchallType.woodTexture);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(0.5F, 1.5F, 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.renderAll();
		GL11.glPopMatrix();
	}
}