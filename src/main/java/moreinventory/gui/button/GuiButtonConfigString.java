package moreinventory.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonConfigString extends GuiButton
{
	protected static final ResourceLocation GuiIndex = new ResourceLocation("textures/gui/widgets.png");

	public boolean isPushed;

	public GuiButtonConfigString(int par1, int par2, int par3, int par4, int par5, String par6Str, boolean flg)
	{
		super(par1, par2, par3, par4, par5, par6Str);
		isPushed = flg;
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = par1Minecraft.fontRenderer;
			par1Minecraft.getTextureManager().bindTexture(GuiIndex);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition
					&& par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
			int k = this.getHoverState(this.field_146123_n);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2,
					46 + k * 20, this.width / 2, this.height);
			this.mouseDragged(par1Minecraft, par2, par3);
			int l = isPushed ? 0x4dff00 : 14737632;

			if (!this.enabled)
			{
				l = -6250336;
			}
			else if (this.field_146123_n)
			{
				l = isPushed ? 0x4dff00 : 16777120;
			}

			this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition
					+ (this.height - 8) / 2, l);
		}
	}
}
