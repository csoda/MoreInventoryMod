package moreinventory.render;

import cpw.mods.fml.client.FMLClientHandler;
import moreinventory.model.ModelCatchall;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RendererItemCatchall implements IItemRenderer {

	private ModelCatchall model;
    private static final ResourceLocation textureIndex1 = new ResourceLocation("textures/blocks/planks_oak.png");

	public RendererItemCatchall() {

	    model = new ModelCatchall();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

	return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

	return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(textureIndex1);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.5F, (float)1.5F, (float)0.5F);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        model.renderAll();
        GL11.glPopMatrix();
	}
}
