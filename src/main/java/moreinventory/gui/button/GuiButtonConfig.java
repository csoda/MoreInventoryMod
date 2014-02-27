package moreinventory.gui.button;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiButtonConfig extends GuiButton{
	private int iconIndexX,iconIndexY;
	public boolean isPushed;
	private static final ResourceLocation GuiIndex = new ResourceLocation("moreinv:GUI/Pouch.png");

	
	
	  public GuiButtonConfig(int par1, int par2, int par3, int par4, int par5,int iconx,int icony,boolean flg,String popup)
	    {
	        super(par1, par2, par3, par4, par5, "");
	        iconIndexX = iconx;
	        iconIndexY = icony;
	        isPushed = flg;
	    }

	    @Override
	    public void drawButton(Minecraft par1Minecraft, int par2, int par3){
            if (this.visible)
            {
                FontRenderer fontrenderer = par1Minecraft.fontRenderer;
                par1Minecraft.getTextureManager().bindTexture(GuiIndex);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
                int k = this.getHoverState(this.field_146123_n);
                if(isPushed){
                	this.drawTexturedModalRect(this.xPosition, this.yPosition, 200, 104, 16, 16);
                }
                else
                {
                	this.drawTexturedModalRect(this.xPosition, this.yPosition,184,104, 16, 16);
                }
                this.drawTexturedModalRect(this.xPosition, this.yPosition, iconIndexX, iconIndexY, 16, 16);
                if(isPushed){
                	this.drawTexturedModalRect(this.xPosition, this.yPosition, 216, 104, 16, 16);
                }
                else
                {
                	this.drawTexturedModalRect(this.xPosition, this.yPosition, 232, 104, 16, 16);
                }
                this.mouseDragged(par1Minecraft, par2, par3);
                int l = 14737632;
            }
	    }

}
