package moreinventory.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.model.ModelCatchall;
import moreinventory.tileentity.TileEntityCatchall;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityCatchallRenderer extends TileEntitySpecialRenderer
{
	private final ModelCatchall model = new ModelCatchall();
	private final EntityItem entityItem = new EntityItem(null, 0.0D, 0.0D, 0.0D);

	public void renderModelAt(TileEntityCatchall tile, double posX, double posY, double posZ, float ticks)
	{
		byte rotation = 0;

		if (tile.getWorldObj() != null)
		{
			rotation = (byte)tile.getBlockMetadata();
		}

		bindTexture(ItemCatchallRenderer.woodTexture);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)posX + 0.5F, (float)posY + 1.5F, (float)posZ + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.renderAll();
		GL11.glPopMatrix();

		if (tile.getWorldObj() != null)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float)posX + 0.5F, (float)posY + 0.5F, (float)posZ + 0.525F);
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

			renderContentsItem(tile);
			GL11.glPopMatrix();
		}
	}

	private void renderContentsItem(TileEntityCatchall tile)
	{
		float renderX, renderY, renderZ;

		for (int i = 0; i < tile.getSizeInventory(); ++i)
		{
			ItemStack itemstack = tile.getStackInSlot(i);

			if (itemstack != null)
			{
				entityItem.setEntityItemStack(itemstack);
				entityItem.getEntityItem().stackSize = 1;
				entityItem.hoverStart = 0.0F;

				byte x = (byte)(i % 3);
				byte y = (byte)((i - i % 3) / 3 % 3);
				byte z = (byte)((i - i % 9) / 9 % 9);
				renderX = x * 0.3F - 0.3F;
				renderY = y * 0.33F - 0.55F;
				renderZ = z * -0.2F + (x + y) % 3 * 0.01F - 0.01F + 0.4F;

				if (RenderManager.instance.options.fancyGraphics)
				{
					RenderManager.instance.renderEntityWithPosYaw(entityItem, renderX, renderY, renderZ, 0.0F, 0.0F);
				}
				else
				{
					renderX += (x + z) % 3 * 0.01F - 0.01F;
					renderZ += -(x + z) % 3 * 0.01F + 0.01F;
					RenderManager.instance.renderEntityWithPosYaw(entityItem, renderX, -renderZ, renderY, 0.0F, 0.0F);
				}
			}
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double posX, double posY, double posZ, float ticks)
	{
		renderModelAt((TileEntityCatchall)tile, posX, posY, posZ, ticks);
	}
}