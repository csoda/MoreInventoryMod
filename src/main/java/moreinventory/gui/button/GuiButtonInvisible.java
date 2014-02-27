package moreinventory.gui.button;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;


@SideOnly(Side.CLIENT)
public class GuiButtonInvisible extends GuiButton{
	
    public GuiButtonInvisible(int par1, int par2, int par3, int par4, int par5)
    {
        super(par1, par2, par3, par4, par5, "");
    }

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3){}
}
