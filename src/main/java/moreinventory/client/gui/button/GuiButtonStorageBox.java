package moreinventory.client.gui.button;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiButtonStorageBox extends GuiButton
{
	public boolean isPushed;

	public GuiButtonStorageBox(int id, int x, int y, int width, int height, String text, boolean pushed)
	{
		super(id, x, y, width, height, text);
		this.isPushed = pushed;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		mouseDragged(mc, mouseX, mouseY);

		int color = isPushed ? 0xff0000 : 6250336;

		if (!enabled)
		{
			color = isPushed ? 0xff8080 : -6250336;
		}
		else if (field_146123_n)
		{
			color = 0x4dff00;
		}

		mc.fontRenderer.drawString(displayString, xPosition + width / 2 - mc.fontRenderer.getStringWidth(displayString) / 2, yPosition + (height - 8) / 2, color);
	}
}