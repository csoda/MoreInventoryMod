package moreinventory.render;

import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RendererTileEntityStorageBox extends TileEntitySpecialRenderer
{
	public void renderAModelAt(TileEntityStorageBox tile, double d, double d1, double d2, float f)
	{
		int rotation = 1;
		int rad = 0;
		float siftX = 0, siftZ = 0;
		if (tile.getWorldObj() != null)
			rotation = tile.face;

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

		// Contents Item
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F + siftX, (float) d1 + 0.35F, (float) d2 + 0.5F + siftZ);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(rad, 0.0F, 1.0F, 0.0F);
		this.renderContentsItem(tile);
		GL11.glPopMatrix();
	}

	private void renderContentsItem(TileEntityStorageBox tile)
	{
		int stackcount, stacksize, fontcolor;
		stackcount = tile.displayedStackCount;
		stacksize = tile.displayedStackSize;
		fontcolor = tile.getStorageBoxType().color;
		ItemStack itemstack = tile.getContents();
		if (itemstack != null)
		{
			EntityItem entityitem = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, itemstack);
			entityitem.getEntityItem().stackSize = 1;
			entityitem.hoverStart = 0.0F;
			RenderItem.renderInFrame = true;
			RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
			RenderItem.renderInFrame = false;

			StringBuffer s = new StringBuffer(10);
			if (stackcount > 0)
			{
				s.append("[");
				s.append(stackcount);
				s.append("]");
				if (stacksize > 0)
				{
					s.append("+");
				}
			}
			if (stacksize > 0)
			{
				if (stackcount < 1000)
				{
					s.append(stacksize);
				}
			}
			String ss = s.substring(0);
			float f1 = 0.6666667F, f2;
			FontRenderer fontrenderer = this.func_147498_b();
			f2 = 0.016666668F * f1 * 2.0F;
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 0);
			GL11.glScalef(f2, -f2, f2);
			GL11.glNormal3f(0.0F, 0.0F, -1.0F * f2);
			GL11.glDepthMask(false);
			fontrenderer.drawString(ss, -fontrenderer.getStringWidth(ss) / 2, 5, fontcolor);
			GL11.glDepthMask(true);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		renderAModelAt((TileEntityStorageBox) par1TileEntity, par2, par4, par6, par8);
	}
}