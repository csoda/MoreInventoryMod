package moreinventory.client.config;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

@SideOnly(Side.CLIENT)
public class MIMGuiFactory implements IModGuiFactory
{
	@Override
	public void initialize(Minecraft mc) {}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass()
	{
		return MIMConfigGui.class;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
	{
		return null;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}
}