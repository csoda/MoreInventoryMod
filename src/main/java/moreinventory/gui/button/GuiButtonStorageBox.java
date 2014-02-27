package moreinventory.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiButtonStorageBox extends GuiButton{

	public boolean isPushed;
	
    public GuiButtonStorageBox(int par1, int par2, int par3, int par4, int par5, String par6Str, boolean flg) {
    	super(par1, par2, par3, par4, par5, par6Str);
		isPushed = flg;
	}

	@Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3){
		
		
		FontRenderer fontrenderer = par1Minecraft.fontRenderer;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
        int k = this.getHoverState(this.field_146123_n);
        this.mouseDragged(par1Minecraft, par2, par3);
        int l = isPushed ? 0xff0000 :6250336;

        if (!this.enabled)
        {
            l = isPushed ? 0xff8080: -6250336;
        }
        else if (this.field_146123_n)
        {
            l =0x4dff00;
        }
        fontrenderer.drawString(this.displayString, this.xPosition + this.width / 2 - fontrenderer.getStringWidth(this.displayString) / 2, this.yPosition + (this.height - 8) / 2, l);
    }
}
