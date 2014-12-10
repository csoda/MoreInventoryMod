package moreinventory.client.config;

import java.util.List;

import moreinventory.core.Config;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MIMConfigGui extends GuiConfig
{
	public MIMConfigGui(GuiScreen parent)
	{
		super(parent, getConfigElements(), MoreInventoryMod.MODID, false, false, I18n.format(MoreInventoryMod.CONFIG_LANG + "title"));
	}

	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		list.addAll(new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
		list.addAll(new ConfigElement(Config.config.getCategory("client")).getChildElements());
		list.add(new DummyCategoryElement(MoreInventoryMod.MODID, MoreInventoryMod.CONFIG_LANG + "color", ColorEntry.class));

		return list;
	}

	public static class ColorEntry extends CategoryEntry
	{
		public ColorEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
		{
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen()
		{
			return new GuiConfig(owningScreen, new ConfigElement(Config.config.getCategory("color")).getChildElements(),
				owningScreen.modID, Configuration.CATEGORY_GENERAL, configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart,
				configElement.requiresMcRestart() || owningScreen.allRequireMcRestart, I18n.format(MoreInventoryMod.CONFIG_LANG + "color"));
		}
	}
}