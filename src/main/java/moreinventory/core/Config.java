package moreinventory.core;

import java.io.File;
import java.util.List;

import moreinventory.tileentity.storagebox.StorageBoxType;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

public class Config
{
	public static Configuration config;

	public static boolean isCollectTorch;
	public static boolean isFullAutoCollectPouch;
	public static boolean leftClickCatchall;
	public static String[] transportableChests;

	// Client-side only
	public static boolean containerBoxSideTexture;
	public static boolean clearGlassBox;

	public static void syncConfig()
	{
		if (config == null)
		{
			File file = new File(Loader.instance().getConfigDir(), "MoreInventoryMod.cfg");
			config = new Configuration(file);

			try
			{
				config.load();
			}
			catch (Exception e)
			{
				File dest = new File(file.getParentFile(), file.getName() + ".bak");

				if (dest.exists())
				{
					dest.delete();
				}

				file.renameTo(dest);

				FMLLog.log(Level.ERROR, e, "A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName());
			}
		}

		String category = Configuration.CATEGORY_GENERAL;
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = config.get(category, "isCollectTorch", true);
		prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		isCollectTorch = prop.getBoolean(isCollectTorch);
		prop = config.get(category, "isFullAutoCollectPouch", false);
		prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		isFullAutoCollectPouch = prop.getBoolean(isFullAutoCollectPouch);
		prop = config.get(category, "leftClickCatchall", false);
		prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		leftClickCatchall = prop.getBoolean(leftClickCatchall);
		prop = config.get(category, "transportableChests", new String[0]);
		prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += Configuration.NEW_LINE + Configuration.NEW_LINE;
		prop.comment += "BlockName @ Metadata(-1~15) @ TextureIndex(1~29)";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "..";
		propOrder.add(prop.getName());
		transportableChests = prop.getStringList();
		MoreInventoryMod.modChest = Joiner.on(",").skipNulls().join(transportableChests);

		config.setCategoryPropertyOrder(category, propOrder);

		if (FMLCommonHandler.instance().getSide().isClient())
		{
			category = "client";
			prop = config.get(category, "containerBoxSideTexture", true);
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			containerBoxSideTexture = prop.getBoolean(containerBoxSideTexture);
			prop = config.get(category, "clearGlassBox", true);
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			clearGlassBox = prop.getBoolean(clearGlassBox);

			config.addCustomCategoryComment(category, "If multiplayer, client-side only.");
			config.setCategoryPropertyOrder(category, propOrder);
		}

		category = "color";

		for (StorageBoxType type : StorageBoxType.values())
		{
			if (type == StorageBoxType.Glass)
			{
				continue;
			}

			prop = config.get(category, type.name(), type.color == 0 ? "000000" : "FFFFFF");
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [color: 000000 ~ FFFFFF, default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			type.color = Integer.parseInt(prop.getString(), 16);
		}

		config.setCategoryPropertyOrder(category, propOrder);
		config.setCategoryLanguageKey(category, MoreInventoryMod.CONFIG_LANG + category);

		if (config.hasChanged())
		{
			config.save();
		}
	}
}