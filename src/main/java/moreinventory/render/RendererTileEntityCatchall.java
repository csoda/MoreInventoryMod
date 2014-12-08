package moreinventory.render;

import moreinventory.model.ModelCatchall;
import moreinventory.tileentity.TileEntityCatchall;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RendererTileEntityCatchall extends TileEntitySpecialRenderer
{
	private final ModelCatchall model = new ModelCatchall();
	private static final ResourceLocation TextureIndex = new ResourceLocation("textures/blocks/planks_oak.png");

	public void renderAModelAt(TileEntityCatchall tile, double d, double d1, double d2, float f)
	{
		byte rotation = 0;
		if (tile.getWorldObj() != null)
			rotation = (byte) tile.getBlockMetadata();
		this.bindTexture(TextureIndex);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.renderAll();
		GL11.glPopMatrix();

		// Items
		if (tile.getWorldObj() != null)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float) d + 0.5F, (float) d1 + 0.5F, (float) d2 + 0.525F);
			GL11.glScalef(0.8F, -0.8F, -0.8F);
			GL11.glRotatef(90, -1.0F, 0.0F, 0.0F);
			if (RenderManager.instance.options.fancyGraphics)
			{
				GL11.glRotatef(90 * rotation, 0.0F, 0.0F, 1.0F);
			}
			else
			{
				GL11.glRotatef(90, -1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, 0.25F);

			}
			this.renderContentsItem(tile);
			GL11.glPopMatrix();
		}
	}

	private void renderContentsItem(TileEntityCatchall tile)
	{
		float rx, ry, rz;
		ItemStack[] dItems = tile.getDisplayedItem();

		if (dItems == null || dItems.length <= 0)
		{
			return;
		}

		for (int i = 0; i < dItems.length; i++)
		{
			ItemStack itemstack = dItems[i];
			if (itemstack != null)
			{
				EntityItem entityitem = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, itemstack);
				entityitem.getEntityItem().stackSize = 1;
				entityitem.hoverStart = 0.0F;
				byte x = (byte) (i % 3);
				byte y = (byte) ((i - i % 3) / 3 % 3);
				byte z = (byte) ((i - i % 9) / 9 % 9);
				rx = x * 0.3F - 0.3F;
				ry = y * 0.33F - 0.55F;
				rz = z * -0.2F + (x + y) % 3 * 0.01F - 0.01F + 0.4F;

				if (RenderManager.instance.options.fancyGraphics)
				{
					RenderManager.instance.renderEntityWithPosYaw(entityitem, rx, ry, rz, 0.0F, 0.0F);
				}
				else
				{
					rx += (x + z) % 3 * 0.01F - 0.01F;
					rz += -(x + z) % 3 * 0.01F + 0.01F;
					RenderManager.instance.renderEntityWithPosYaw(entityitem, rx, -rz, ry, 0.0F, 0.0F);
				}
			}
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		renderAModelAt((TileEntityCatchall)tile, par2, par4, par6, par8);
	}
}
