package moreinventory.client.renderer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.Config;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityStorageBoxRenderer extends TileEntitySpecialRenderer
{
	private final EntityItem entityItem = new EntityItem(null, 0.0D, 0.0D, 0.0D);

	public void renderModelAt(TileEntityStorageBox tile, double posX, double posY, double posZ, float ticks)
	{
		int rotation = 1;
		int rad = 0;
		float siftX = 0;
		float siftZ = 0;

		if (tile.getWorldObj() != null)
		{
			rotation = tile.face;
		}

		switch (rotation)
		{
			case 2:
				siftZ = -0.5125F;
				rad = 180;
				break;
			case 3:
				siftZ = 0.5125F;
				rad = 0;
				break;
			case 4:
				siftX = -0.5125F;
				rad = -90;
				break;
			case 5:
				siftX = 0.5125F;
				rad = 90;
				break;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float)posX + 0.5F + siftX, (float)posY + 0.35F, (float)posZ + 0.5F + siftZ);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(rad, 0.0F, 1.0F, 0.0F);
		renderContentsItem(tile);
		GL11.glPopMatrix();
	}

	private void renderContentsItem(TileEntityStorageBox tile)
	{
		int count = tile.displayedStackCount;
		int size = tile.displayedStackSize;
		ItemStack itemstack = tile.getContents();

		if (itemstack != null)
		{
			if (Config.pointedContainerBoxInfo)
			{
				MovingObjectPosition moving = FMLClientHandler.instance().getClient().objectMouseOver;

				if (moving == null || moving.typeOfHit != MovingObjectType.BLOCK || moving.blockX != tile.xCoord || moving.blockY != tile.yCoord || moving.blockZ != tile.zCoord)
				{
					return;
				}
			}

			entityItem.setEntityItemStack(itemstack);
			entityItem.getEntityItem().stackSize = 1;
			entityItem.hoverStart = 0.0F;

			RenderItem.renderInFrame = true;
			RenderManager.instance.renderEntityWithPosYaw(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
			RenderItem.renderInFrame = false;

			StringBuilder builder = new StringBuilder(10);

			if (count > 0)
			{
				builder.append("[");
				builder.append(count);
				builder.append("]");

				if (size > 0)
				{
					builder.append("+");
				}
			}

			if (size > 0)
			{
				if (count < 1000)
				{
					builder.append(size);
				}
			}

			String text = builder.substring(0);
			float scale = 0.016666668F * 0.6666667F * 2.0F;
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 0);
			GL11.glScalef(scale, -scale, scale);
			GL11.glNormal3f(0.0F, 0.0F, -1.0F * scale);
			GL11.glDepthMask(false);
			func_147498_b().drawString(text, -func_147498_b().getStringWidth(text) / 2, 5, tile.getStorageBoxType().color);
			GL11.glDepthMask(true);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double posX, double posY, double posZ, float ticks)
	{
		renderModelAt((TileEntityStorageBox)tile, posX, posY, posZ, ticks);
	}
}