package moreinventory.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonConfigString extends GuiButton
{
	protected static final ResourceLocation widgetsGuiTexture = new ResourceLocation("textures/gui/widgets.png");

	public boolean isPushed;

	public GuiButtonConfigString(int id, int x, int y, int width, int height, String text, boolean pushed)
	{
		super(id, x, y, width, height, text);
		this.isPushed = pushed;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (visible)
		{
			mc.getTextureManager().bindTexture(widgetsGuiTexture);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int i = getHoverState(field_146123_n);
			drawTexturedModalRect(xPosition, yPosition, 0, 46 + i * 20, width / 2, height);
			drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + i * 20, width / 2, height);
			mouseDragged(mc, mouseX, mouseY);
			i = isPushed ? 0x4dff00 : 14737632;

			if (!enabled)
			{
				i = -6250336;
			}
			else if (field_146123_n)
			{
				i = isPushed ? 0x4dff00 : 16777120;
			}

			drawCenteredString(mc.fontRenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, i);
		}
	}
}
