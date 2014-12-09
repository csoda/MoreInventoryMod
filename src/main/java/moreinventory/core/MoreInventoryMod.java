package moreinventory.core;

import java.util.HashMap;
import java.util.Map;

import moreinventory.block.BlockCatchall;
import moreinventory.block.BlockStorageBox;
import moreinventory.block.BlockStorageBoxAddon;
import moreinventory.block.BlockTransportManager;
import moreinventory.crafting.RecipePouch;
import moreinventory.crafting.RecipeTorchHolder;
import moreinventory.handler.MIMEventHooks;
import moreinventory.handler.MIMGuiHandler;
import moreinventory.handler.MIMWorldSaveHelper;
import moreinventory.item.ItemBlockSBAddon;
import moreinventory.item.ItemBlockStorageBox;
import moreinventory.item.ItemBlockTransportManager;
import moreinventory.item.ItemChestTransporter;
import moreinventory.item.ItemNoFunction;
import moreinventory.item.ItemPlating;
import moreinventory.item.ItemPotionHolder;
import moreinventory.item.ItemPouch;
import moreinventory.item.ItemSpanner;
import moreinventory.item.ItemTorchHolder;
import moreinventory.network.CatchallMessage;
import moreinventory.network.ImporterMessage;
import moreinventory.network.PouchMessage;
import moreinventory.network.SBAddonBaseConfigMessage;
import moreinventory.network.SBAddonBaseMessage;
import moreinventory.network.StorageBoxButtonMessage;
import moreinventory.network.StorageBoxConfigMessage;
import moreinventory.network.StorageBoxContentsMessage;
import moreinventory.network.StorageBoxMessage;
import moreinventory.network.TransportManagerMessage;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.tileentity.TileEntityExporter;
import moreinventory.tileentity.TileEntityImporter;
import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.addon.EnumSBAddon;
import moreinventory.util.MIMUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod
(
	modid = MoreInventoryMod.MODID,
	acceptedMinecraftVersions = "[1.7.10,)",
	dependencies = "after:InvTweaks",
	useMetadata = true
)
public class MoreInventoryMod
{
	public static final String MODID = "MoreInventoryMod";

	@Instance(MODID)
	public static MoreInventoryMod instance;

	@SidedProxy(modId = MODID, clientSide = "moreinventory.core.ClientProxy", serverSide = "moreinventory.core.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MODID);

	public static MIMWorldSaveHelper saveHelper;

	public static Block Catchall;
	public static Block StorageBox;
	public static Block TransportManager;
	public static Block StorageBoxAddon;
	public static Item[] Torchholder = new Item[3];
	public static Item ChestTransporter;
	public static Item Pouch;
	public static Item NoFunctionItems;
	public static Item Spanner;
	public static Item Potionholder;
	public static Item Plating;

	public static boolean isCollectTorch;
	public static boolean isFullAutoCollectPouch;
	public static boolean leftClickGUI;
	public static boolean clearGlassBox;
	public static int displayedItemSize;
	private String modChest = "";

	/*** Client ***/
	public static boolean StorageBoxsideTexture;

	public static final Map<String, Integer> transportableChest = Maps.newHashMap();
	public static final Map<String, Map<Integer, Integer>> transportableChestIcon = Maps.newHashMap();

	public static final String[] MATERIALNAME = {"Leather Pack", "Brush", "Dimension Core", "Clipboard"};
	public static final String[] COLORNAME = {"White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};

	public static final String defaultOwner = "***Unknown***";

	public static final CreativeTabs tabMoreInventoryMod = new CreativeTabMoreInventoryMod();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		Property prop1 = config.get(Configuration.CATEGORY_GENERAL, "isCollectTorchs", true);
		prop1.comment = "Put away  when you pick up torch";
		isCollectTorch = prop1.getBoolean(true);
		Property prop2 = config.get(Configuration.CATEGORY_GENERAL, "fullAutoCollect", false);
		prop2.comment = "Pouch collects items from your inventory whenever you pick up drop items";
		isFullAutoCollectPouch = prop2.getBoolean(true);
		Property prop3 = config.get("client.general", "displayedItemSize", 36);
		prop3.comment = "How many Items are displayed in catchall(1~36)";
		displayedItemSize = prop3.getInt();
		Property prop4 = config.get(Configuration.CATEGORY_GENERAL, "modChestRegistry", "");
		prop4.comment = "You can register other Mod's Chest to transport. [BlockName @ Metadata(-1~15) @ TextureIndex(1~29), …]";
		modChest = prop4.getString();
		Property prop6 = config.get("client.general", "SideTexture", false);
		prop6.comment = "Use side-texture of ContainerBox";
		StorageBoxsideTexture = prop6.getBoolean(true);
		Property prop8 = config.get(Configuration.CATEGORY_GENERAL, "LeftClickGUI", false);
		prop8.comment = "left-click to open Catchall's GUI";
		leftClickGUI = prop8.getBoolean(true);
		Property prop9 = config.get("client.general", "ClearGlassContainer", true);
		prop9.comment = "Connect texture of GlassContainerBox";
		clearGlassBox = prop9.getBoolean(true);

		config.addCustomCategoryComment("client.color", "Color of number displayed on ContainerBox(000000 ~ ffffff)");
		Property[] prop7 = new Property[StorageBoxType.values().length];
		for (int i = 0; i < prop7.length; i++)
		{
			if (i != 8)
			{
				String st = StorageBoxType.values()[i].color == 0 ? "000000" : "ffffff";
				prop7[i] = config.get("client.color", StorageBoxType.values()[i].name() + "Color", st);
				StorageBoxType.values()[i].color = Integer.parseInt(prop7[i].getString(), 16);
			}
		}
		config.save();

		/*** Blocks ***/
		Catchall = new BlockCatchall(Material.wood).setBlockName("catchall");
		StorageBox = new BlockStorageBox(Material.iron).setBlockName("containerbox");
		TransportManager = new BlockTransportManager(Material.rock).setBlockName("transportmanager");
		StorageBoxAddon = new BlockStorageBoxAddon(Material.iron);
		GameRegistry.registerBlock(Catchall, ItemBlock.class, "catchall");
		GameRegistry.registerBlock(StorageBox, ItemBlockStorageBox.class, "containerbox");
		GameRegistry.registerBlock(TransportManager, ItemBlockTransportManager.class, "transportmanager");
		GameRegistry.registerBlock(StorageBoxAddon, ItemBlockSBAddon.class, "StorageBoxAddon");

		/*** Items ***/
		ChestTransporter = new ItemChestTransporter().setUnlocalizedName("transporter");
		NoFunctionItems = new ItemNoFunction().setUnlocalizedName("itemnofunction");
		Potionholder = new ItemPotionHolder().setUnlocalizedName("potionholder");
		Spanner = new ItemSpanner().setUnlocalizedName("spanner").setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
		Plating = new ItemPlating().setUnlocalizedName("painting").setCreativeTab(MoreInventoryMod.tabMoreInventoryMod);
		Pouch = new ItemPouch().setUnlocalizedName("pouch");

		final String[] gradeName = { "Iron", "Gold", "Diamond" };
		for (int i = 0; i < 3; i++)
		{
			Torchholder[i] = new ItemTorchHolder(i).setUnlocalizedName("torchholder:" + gradeName[i]);
			GameRegistry.registerItem(Torchholder[i], "torchholder" + gradeName[i]);
		}

		GameRegistry.registerItem(ChestTransporter, "transporter");
		GameRegistry.registerItem(NoFunctionItems, "itemnofunction");
		GameRegistry.registerItem(Potionholder, "potionholder");
		GameRegistry.registerItem(Spanner, "spanner");
		GameRegistry.registerItem(Plating, "painting");
		GameRegistry.registerItem(Pouch, "pouch");

		/*** TileEntity ***/
		GameRegistry.registerTileEntity(TileEntityCatchall.class, "containerCatchall");
		GameRegistry.registerTileEntity(TileEntityTransportManager.class, "TileEntityTransportManagerMeta");
		GameRegistry.registerTileEntity(TileEntityImporter.class, "TilEntityImporter");
		GameRegistry.registerTileEntity(TileEntityExporter.class, "TileEntityExporter");

		for (StorageBoxType type : StorageBoxType.values())
		{
			GameRegistry.registerTileEntity(type.clazz, "TileEntity" + type.name() + "StorageBox");
		}

		for (int i = 0; i < EnumSBAddon.values().length; i++)
		{
			GameRegistry.registerTileEntity(EnumSBAddon.values()[i].clazz, "moreinventory." + EnumSBAddon.values()[i].name());
		}

		/*** Message ***/
		int i = 0;
		network.registerMessage(PouchMessage.Client.class, PouchMessage.class, i++, Side.CLIENT);
		network.registerMessage(PouchMessage.Server.class, PouchMessage.class, i++, Side.SERVER);
		network.registerMessage(TransportManagerMessage.class, TransportManagerMessage.class, i++, Side.CLIENT);
		network.registerMessage(CatchallMessage.class, CatchallMessage.class, i++, Side.CLIENT);
		network.registerMessage(ImporterMessage.Client.class, ImporterMessage.class, i++, Side.CLIENT);
		network.registerMessage(ImporterMessage.Server.class, ImporterMessage.class, i++, Side.SERVER);
		network.registerMessage(StorageBoxMessage.class, StorageBoxMessage.class, i++, Side.CLIENT);
		network.registerMessage(StorageBoxContentsMessage.class, StorageBoxContentsMessage.class, i++, Side.CLIENT);
		network.registerMessage(StorageBoxConfigMessage.class, StorageBoxConfigMessage.class, i++, Side.CLIENT);
		network.registerMessage(StorageBoxButtonMessage.class, StorageBoxButtonMessage.class, i++, Side.SERVER);
		network.registerMessage(SBAddonBaseMessage.class, SBAddonBaseMessage.class, i++, Side.CLIENT);
		network.registerMessage(SBAddonBaseConfigMessage.Client.class, SBAddonBaseConfigMessage.class, i++, Side.CLIENT);
		network.registerMessage(SBAddonBaseConfigMessage.Server.class, SBAddonBaseConfigMessage.class, i++, Side.SERVER);
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MIMGuiHandler());

		MinecraftForge.EVENT_BUS.register(MIMEventHooks.instance);

		GameRegistry.addShapedRecipe(new ItemStack(Torchholder[0], 1, ItemTorchHolder.maxDamage[0] - 2),
			"I L", "I I", "III",
			'I', new ItemStack(Items.iron_ingot),
			'L', new ItemStack(Items.leather)
		);
		GameRegistry.addShapedRecipe(new ItemStack(Torchholder[1], 1, ItemTorchHolder.maxDamage[1] - 2),
			"I L", "I I", "ISI",
			'I', new ItemStack(Items.gold_ingot),
			'L', new ItemStack(Items.leather),
			'S', new ItemStack(Items.blaze_rod)
		);
		GameRegistry.addShapedRecipe(new ItemStack(Torchholder[2], 1, ItemTorchHolder.maxDamage[2] - 2),
			"I L", "I I", "ISI",
			'I', new ItemStack(Items.diamond),
			'L', new ItemStack(Items.leather),
			'S', new ItemStack(Items.nether_star)
		);

		for (int i = 0; i < 2; ++i)
		{
			GameRegistry.addRecipe(new RecipeTorchHolder(new ItemStack(Blocks.torch), Lists.newArrayList(new ItemStack(Torchholder[i], 1, OreDictionary.WILDCARD_VALUE))));
		}

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Catchall),
			"P P", "PCP", "HHH",
			'P', "plankWood",
			'H', "slabWood",
			'C', new ItemStack(Blocks.chest)
		));

		GameRegistry.addShapedRecipe(new ItemStack(Spanner),
			"SSS", " I ", "SSS",
			'S', new ItemStack(Blocks.stone),
			'I', new ItemStack(Items.iron_ingot)
		);

		GameRegistry.addShapedRecipe(new ItemStack(Potionholder),
			"SLS", "BBB",
			'S', new ItemStack(Items.string),
			'L', new ItemStack(Items.leather),
			'B', new ItemStack(Items.glass_bottle)
		);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ChestTransporter),
			"P P", "PHP", "HHH",
			'H', "slabWood",
			'P', "plankWood"
		));

		GameRegistry.addShapedRecipe(new ItemStack(TransportManager, 1, 0),
			" B ", "SBS", "SSS",
			'B', new ItemStack(Blocks.lapis_block),
			'S', new ItemStack(Blocks.stone)
		);
		GameRegistry.addShapedRecipe(new ItemStack(TransportManager, 1, 1),
			" B ", "SBS", "SSS",
			'B', new ItemStack(Blocks.redstone_block),
			'S', new ItemStack(Blocks.stone)
		);

		GameRegistry.addShapedRecipe(new ItemStack(Pouch),
			"PPP", "BLB", "PBP",
			'L', new ItemStack(Items.diamond),
			'P', new ItemStack(Items.leather),
			'B', new ItemStack(NoFunctionItems, 1, 0)
		);

		for (int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new RecipePouch(new ItemStack(Pouch, 1, i + 1), "dye" + COLORNAME[i], new ItemStack(Pouch, 1, OreDictionary.WILDCARD_VALUE)));
		}

		ItemStack upgradePouch = new ItemStack(Pouch);
		upgradePouch.setStackDisplayName(upgradePouch.getDisplayName() + "(Upgrade)");
		GameRegistry.addRecipe(new RecipePouch(upgradePouch, new ItemStack(Items.ender_pearl), new ItemStack(Pouch, 1, OreDictionary.WILDCARD_VALUE)));

		GameRegistry.addShapedRecipe(new ItemStack(NoFunctionItems, 1, 0),
			"LLL", "LSL", "LLL",
			'L', new ItemStack(Items.leather),
			'S', new ItemStack(Items.string)
		);
		GameRegistry.addShapedRecipe(new ItemStack(NoFunctionItems, 4, 1),
			" WW", " WW", "S  ",
			'W', new ItemStack(Blocks.wool),
			'S', new ItemStack(Items.stick)
		);
		GameRegistry.addShapedRecipe(new ItemStack(NoFunctionItems, 1, 2),
			"ODO", "DED", "ODO",
			'O', new ItemStack(Blocks.obsidian),
			'D', new ItemStack(Items.diamond),
			'E', new ItemStack(Items.ender_eye)
		);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(NoFunctionItems, 1, 3),
			"WIW", "WPW", "WPW",
			'W', "slabWood",
			'I', new ItemStack(Items.iron_ingot),
			'P', new ItemStack(Items.paper)
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StorageBox, 1, 0),
			"IHI", "I I", "IHI",
			'I', "logWood",
			'C', new ItemStack(StorageBox, 1, 0),
			'H', "slabWood"
		));

		ItemStack woodStorageBox = new ItemStack(StorageBox, 1, 0);
		ItemStack halfStone = new ItemStack(Blocks.stone_slab);

		for (int i = 0; i < StorageBoxType.values().length; i++)
		{
			if (StorageBoxType.values()[i].canCraft)
			{
				for (int j = 0; j < StorageBoxType.values()[i].materials.length; j++)
				{
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StorageBox, 3, i), true,
						"IHI", "ICI", "IHI",
						'I', StorageBoxType.values()[i].materials[j],
						'C', woodStorageBox,
						'H', halfStone
					));
				}
			}
		}

		GameRegistry.addShapedRecipe(new ItemStack(StorageBox, 32, 8),
			"IHI", "I I", "IHI",
			'H', new ItemStack(Blocks.glass_pane),
			'I', new ItemStack(Blocks.glass)
		);
		GameRegistry.addShapedRecipe(new ItemStack(StorageBox, 1, 9),
			"IHI", "I I", "IHI",
			'H', new ItemStack(Blocks.stone_slab, 1, 3),
			'I', new ItemStack(Blocks.cobblestone)
		);
		GameRegistry.addShapedRecipe(new ItemStack(StorageBox, 2, 11),
			"OOO", "CEC", "OOO",
			'O', new ItemStack(Blocks.obsidian),
			'C', new ItemStack(NoFunctionItems, 1, 2),
			'E', new ItemStack(StorageBox, 1, 10)
		);

		GameRegistry.addRecipe(new ItemStack(StorageBoxAddon, 1, 0),
			"SSS", "CEC", "SSS",
			'S', new ItemStack(Blocks.end_stone),
			'C', new ItemStack(NoFunctionItems, 1, 2),
			'E', new ItemStack(StorageBox, 1, 10)
		);

		ItemStack lava = new ItemStack(Items.lava_bucket);
		ItemStack brush = new ItemStack(NoFunctionItems, 1, 1);

		for (int i = 0; i < ItemPlating.typeIndex.length; i++)
		{
			int j = ItemPlating.typeIndex[i];

			for (int k = 0; k < StorageBoxType.values()[j].materials.length; k++)
			{
				Object ingot = StorageBoxType.values()[j].materials[k];

				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Plating, 1, i), ingot, ingot, lava, brush));
			}
		}

		proxy.registerRenderers();

		chestRegistry();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		modChestRegistry();
	}

	private void chestRegistry()
	{
		transportableChest.put(MIMUtils.getUniqueName(Blocks.chest), -1);
		transportableChest.put(MIMUtils.getUniqueName(Blocks.trapped_chest), -1);
		transportableChest.put(MIMUtils.getUniqueName(StorageBox), -1);

		modChest = modChest.trim();

		if (modChest.length() > 0)
		{
			for (String entry : modChest.split(","))
			{
				String[] args = entry.split("@");
				String name = args[0].trim();
				int damage = Integer.parseInt(args[1].trim());

				transportableChest.put(name, damage);

				if (!transportableChestIcon.containsKey(name))
				{
					transportableChestIcon.put(name, new HashMap<Integer, Integer>());
				}

				int i = args[2].trim().length() > 0 ? Byte.parseByte(args[2].trim()) : 0;
				transportableChestIcon.get(name).put(damage, i);
			}
		}
	}

	private void modChestRegistry()
	{
		transportableChest.put("IronChest:BlockIronChest", -1);

		for (int i = 0; i < 7; i++)
		{
			Map<Integer, Integer> map = Maps.newHashMap();
			map.put(i, i + 1);
			transportableChestIcon.put("IronChest:BlockIronChest", map);
		}

		transportableChest.put("MultiPageChest:multipagechest", -1);
		Map<Integer, Integer> map = Maps.newHashMap();
		map.put(-1, 11);
		transportableChestIcon.put("MultiPageChest:multipagechest", map);
	}
}