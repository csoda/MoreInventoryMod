package moreinventory.client.gui.button;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

@SideOnly(Side.CLIENT)
public class GuiButtonInvisible extends GuiButton
{
	public GuiButtonInvisible(int id, int x, int y, int width, int height)
	{
		super(id, x, y, width, height, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {}
}