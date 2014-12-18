package moreinventory.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import moreinventory.item.ItemTransporter;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.util.MIMLog;
import moreinventory.util.MIMUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Config
{
	public static Configuration config;

	public static boolean versionNotify;
	public static String[] transportableBlocksDefault;
	public static String[] transportableBlocks;

	public static final Set<String> isCollectTorch = Sets.newHashSet();
	public static final Set<String> isCollectArrow = Sets.newHashSet();
	public static final Set<String> isFullAutoCollectPouch = Sets.newHashSet();
	public static final Set<String> leftClickCatchall = Sets.newHashSet();

	// Client-side only
	public static boolean containerBoxSideTexture;
	public static boolean pointedContainerBoxInfo;
	public static boolean clearGlassBox;

	public static Class<? extends IConfigEntry> transportableBlocksEntry;

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

				MIMLog.log(Level.ERROR, e, "A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName());
			}
		}

		String category = Configuration.CATEGORY_GENERAL;
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = config.get(category, "versionNotify", true);
		prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		versionNotify = prop.getBoolean(versionNotify);

		if (transportableBlocksDefault == null)
		{
			List<String> chests = Lists.newArrayList();

			chests.add(MIMUtils.getUniqueName(Blocks.chest) + "@-1@0");
			chests.add(MIMUtils.getUniqueName(Blocks.trapped_chest) + "@-1@0");
			chests.add(MIMUtils.getUniqueName(Blocks.furnace) + "@-1@0");
			chests.add(MIMUtils.getUniqueName(Blocks.lit_furnace) + "@-1@0");
			chests.add(MIMUtils.getUniqueName(MoreInventoryMod.storageBox) + "@-1@0");

			for (int i = 0; i < 7; i++)
			{
				chests.add("IronChest:BlockIronChest@" + i + "@" + (i + 1));
			}

			chests.add("BambooMod:jpChest@-1@10");
			chests.add("MultiPageChest:multipagechest@-1@11");

			transportableBlocksDefault = chests.toArray(new String[chests.size()]);
		}

		prop = config.get(category, "transportableBlocks", transportableBlocksDefault);
		prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName()).setConfigEntryClass(transportableBlocksEntry);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += Configuration.NEW_LINE + Configuration.NEW_LINE;
		prop.comment += "BlockName @ Metadata(-1~15) @ TextureIndex(1~29)";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "..";
		propOrder.add(prop.getName());
		transportableBlocks = prop.getStringList();

		ItemTransporter.refreshTransportableBlocks(transportableBlocks);

		if (FMLCommonHandler.instance().getSide().isClient())
		{
			prop = config.get(category, "isCollectTorch", true);
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());

			if (prop.getBoolean(isCollectTorch.remove("client")))
			{
				isCollectTorch.add("client");
			}

			prop = config.get(category, "isCollectArrow", true);
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());

			if (prop.getBoolean(isCollectArrow.remove("client")))
			{
				isCollectArrow.add("client");
			}

			prop = config.get(category, "isFullAutoCollectPouch", false);
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());

			if (prop.getBoolean(isFullAutoCollectPouch.remove("client")))
			{
				isFullAutoCollectPouch.add("client");
			}

			prop = config.get(category, "leftClickCatchall", false);
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());

			if (prop.getBoolean(leftClickCatchall.remove("client")))
			{
				leftClickCatchall.add("client");
			}

			prop = config.get(category, "containerBoxSideTexture", true);
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			containerBoxSideTexture = prop.getBoolean(containerBoxSideTexture);
			prop = config.get(category, "pointedContainerBoxInfo", false);
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			pointedContainerBoxInfo = prop.getBoolean(pointedContainerBoxInfo);
			prop = config.get(category, "clearGlassBox", true);
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			clearGlassBox = prop.getBoolean(clearGlassBox);
		}

		config.setCategoryPropertyOrder(category, propOrder);

		category = "color";

		for (Entry<String, StorageBoxType> type : StorageBoxType.types.entrySet())
		{
			if (type.getKey().equals("Glass"))
			{
				continue;
			}

			prop = config.get(category, type.getKey(), type.getValue().color == 0 ? "000000" : "FFFFFF");
			prop.setLanguageKey(MoreInventoryMod.CONFIG_LANG + category + "." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [color: 000000 ~ FFFFFF, default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			type.getValue().color = Integer.parseInt(prop.getString(), 16);
		}

		config.setCategoryPropertyOrder(category, propOrder);
		config.setCategoryLanguageKey(category, MoreInventoryMod.CONFIG_LANG + category);

		if (config.hasChanged())
		{
			config.save();
		}
	}
}