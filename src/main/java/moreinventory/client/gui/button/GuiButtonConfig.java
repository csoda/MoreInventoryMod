package moreinventory.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonConfig extends GuiButton
{
	private static final ResourceLocation pouchGuiTexture = new ResourceLocation("moreinv:GUI/Pouch.png");

	private int iconIndexX, iconIndexY;
	public boolean isPushed;

	public GuiButtonConfig(int id, int x, int y, int width, int height, int iconX, int iconY, boolean pushed, String text)
	{
		super(id, x, y, width, height, "");
		this.iconIndexX = iconX;
		this.iconIndexY = iconY;
		this.isPushed = pushed;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (visible)
		{
			mc.getTextureManager().bindTexture(pouchGuiTexture);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;

			if (isPushed)
			{
				drawTexturedModalRect(xPosition, yPosition, 200, 104, 16, 16);
			}
			else
			{
				drawTexturedModalRect(xPosition, yPosition, 184, 104, 16, 16);
			}

			drawTexturedModalRect(xPosition, yPosition, iconIndexX, iconIndexY, 16, 16);

			if (isPushed)
			{
				drawTexturedModalRect(xPosition, yPosition, 216, 104, 16, 16);
			}
			else
			{
				drawTexturedModalRect(xPosition, yPosition, 232, 104, 16, 16);
			}

			mouseDragged(mc, mouseX, mouseY);
		}
	}
}