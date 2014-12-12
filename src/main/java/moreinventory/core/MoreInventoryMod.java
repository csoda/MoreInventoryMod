package moreinventory.core;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import moreinventory.block.BlockCatchall;
import moreinventory.block.BlockStorageBox;
import moreinventory.block.BlockStorageBoxAddon;
import moreinventory.block.BlockTransportManager;
import moreinventory.handler.MIMEventHooks;
import moreinventory.handler.MIMGuiHandler;
import moreinventory.handler.MIMWorldSaveHelper;
import moreinventory.item.*;
import moreinventory.network.*;
import moreinventory.recipe.RecipeArrowHolder;
import moreinventory.recipe.RecipePouch;
import moreinventory.recipe.RecipeTorchHolder;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.tileentity.TileEntityExporter;
import moreinventory.tileentity.TileEntityImporter;
import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.addon.EnumSBAddon;
import moreinventory.util.StorageBoxOwnerList;
import moreinventory.util.Version;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod
(
	modid = MoreInventoryMod.MODID,
	acceptedMinecraftVersions = "[1.7.10,)",
	dependencies = "after:InvTweaks",
	guiFactory = "moreinventory.client.config.MIMGuiFactory",
	useMetadata = true
)
public class MoreInventoryMod
{
	public static final String
	MODID = "MoreInventoryMod",
	CONFIG_LANG = "moreinv.config.";

	@Instance(MODID)
	public static MoreInventoryMod instance;

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(modId = MODID, clientSide = "moreinventory.client.ClientProxy", serverSide = "moreinventory.core.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MODID);

	public static final CreativeTabs tabMoreInventoryMod = new CreativeTabMoreInventoryMod();

	public static final String[] MATERIALNAME = {"Leather Pack", "Brush", "Dimension Core", "Clipboard"};
	public static final String[] COLORNAME = {"White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};

	public static final String defaultOwner = "***Unknown***";

	public static MIMWorldSaveHelper saveHelper;
	public static StorageBoxOwnerList StorageBoxOwnerList;

	public static Block catchall;
	public static Block storageBox;
	public static Block transportManager;
	public static Block storageBoxAddon;

	public static Item[] torchHolder;
	public static Item[] arrowHolder;
	public static Item transporter;
	public static Item noFunctionItems;
	public static Item potionHolder;
	public static Item spanner;
	public static Item plating;
	public static Item pouch;

	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		Version.versionCheck();

		RecipeSorter.register(MODID + ":torchholder", RecipeTorchHolder.class, Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register(MODID + ":arrowholder", RecipeArrowHolder.class, Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register(MODID + ":pouch", RecipePouch.class, Category.SHAPELESS, "after:minecraft:shapeless");
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		catchall = new BlockCatchall(Material.wood).setBlockName("catchall");
		storageBox = new BlockStorageBox(Material.iron).setBlockName("containerbox");
		transportManager = new BlockTransportManager(Material.rock).setBlockName("transportmanager");
		storageBoxAddon = new BlockStorageBoxAddon(Material.iron);

		torchHolder = new Item[3];
		arrowHolder = new Item[3];
		transporter = new ItemChestTransporter().setUnlocalizedName("transporter");
		noFunctionItems = new ItemNoFunction().setUnlocalizedName("itemnofunction");
		potionHolder = new ItemPotionHolder().setUnlocalizedName("potionholder");
		spanner = new ItemSpanner().setUnlocalizedName("spanner");
		plating = new ItemPlating().setUnlocalizedName("painting");
		pouch = new ItemPouch().setUnlocalizedName("pouch");

		GameRegistry.registerBlock(catchall, ItemBlock.class, "catchall");
		GameRegistry.registerBlock(storageBox, ItemBlockStorageBox.class, "containerbox");
		GameRegistry.registerBlock(transportManager, ItemBlockTransportManager.class, "transportmanager");
		GameRegistry.registerBlock(storageBoxAddon, ItemBlockSBAddon.class, "StorageBoxAddon");

		final String[] gradeName = {"Iron", "Gold", "Diamond"};
		for (int i = 0; i < gradeName.length; i++)
		{
			torchHolder[i] = new ItemTorchHolder(i).setUnlocalizedName("torchholder:" + gradeName[i]);

			GameRegistry.registerItem(torchHolder[i], "torchholder" + gradeName[i]);
		}

		for (int i = 0; i < gradeName.length; i++)
		{
			arrowHolder[i] = new ItemArrowHolder(i).setUnlocalizedName("arrowholder:" + gradeName[i]);

			GameRegistry.registerItem(arrowHolder[i], "arrowholder" + gradeName[i]);

			BlockDispenser.dispenseBehaviorRegistry.putObject(arrowHolder[i], new BehaviorProjectileDispense()
			{
				@Override
				public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemstack)
				{
					World world = blockSource.getWorld();
					EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
					IProjectile projectile = getProjectileEntity(world, BlockDispenser.func_149939_a(blockSource));
					projectile.setThrowableHeading((double)facing.getFrontOffsetX(), (double)((float)facing.getFrontOffsetY() + 0.1F), (double)facing.getFrontOffsetZ(), func_82500_b(), func_82498_a());
					world.spawnEntityInWorld((Entity)projectile);
					itemstack.damageItem(1, FakePlayerFactory.getMinecraft((WorldServer)world));

					return itemstack;
				}

				@Override
				protected IProjectile getProjectileEntity(World world, IPosition pos)
				{
					EntityArrow entity = new EntityArrow(world, pos.getX(), pos.getY(), pos.getZ());
					entity.canBePickedUp = 1;

					return entity;
				}
			});
		}

		GameRegistry.registerItem(transporter, "transporter");
		GameRegistry.registerItem(noFunctionItems, "itemnofunction");
		GameRegistry.registerItem(potionHolder, "potionholder");
		GameRegistry.registerItem(spanner, "spanner");
		GameRegistry.registerItem(plating, "painting");
		GameRegistry.registerItem(pouch, "pouch");

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

		Config.syncConfig();

		int i = 0;
		network.registerMessage(ConfigSyncMessage.class, ConfigSyncMessage.class, i++, Side.SERVER);
		network.registerMessage(OpenUrlMessage.class, OpenUrlMessage.class, i++, Side.CLIENT);
		network.registerMessage(PouchMessage.Client.class, PouchMessage.class, i++, Side.CLIENT);
		network.registerMessage(PouchMessage.Server.class, PouchMessage.class, i++, Side.SERVER);
		network.registerMessage(TransportManagerMessage.Client.class, TransportManagerMessage.class, i++, Side.CLIENT);
		network.registerMessage(TransportManagerMessage.Server.class, TransportManagerMessage.class, i++, Side.SERVER);
		network.registerMessage(CatchallMessage.class, CatchallMessage.class, i++, Side.CLIENT);
		network.registerMessage(ImporterMessage.Client.class, ImporterMessage.class, i++, Side.CLIENT);
		network.registerMessage(ImporterMessage.Server.class, ImporterMessage.class, i++, Side.SERVER);
		network.registerMessage(StorageBoxMessage.class, StorageBoxMessage.class, i++, Side.CLIENT);
		network.registerMessage(StorageBoxContentsMessage.class, StorageBoxContentsMessage.class, i++, Side.CLIENT);
		network.registerMessage(StorageBoxConfigMessage.class, StorageBoxConfigMessage.class, i++, Side.CLIENT);
		network.registerMessage(StorageBoxButtonMessage.class, StorageBoxButtonMessage.class, i++, Side.SERVER);
		network.registerMessage(SBAddonBaseMessage.class, SBAddonBaseMessage.class, i++, Side.CLIENT);
		network.registerMessage(SBAddonBaseConfigMessage.Client.class, SBAddonBaseConfigMessage.class, i++, Side.CLIENT);
		network.registerMessage(SBAddonBaseConfigMessage.Server.class, SBAddonBaseConfigMessage.class, i, Side.SERVER);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MIMGuiHandler());

		FMLCommonHandler.instance().bus().register(MIMEventHooks.instance);

		MinecraftForge.EVENT_BUS.register(MIMEventHooks.instance);

		GameRegistry.addShapedRecipe(new ItemStack(torchHolder[0], 1, ItemTorchHolder.maxDamage[0] - 2),
			"I L", "I I", "III",
			'I', Items.iron_ingot,
			'L', Items.leather
		);
		GameRegistry.addShapedRecipe(new ItemStack(torchHolder[1], 1, ItemTorchHolder.maxDamage[1] - 2),
			"I L", "I I", "ISI",
			'I', Items.gold_ingot,
			'L', Items.leather,
			'S', Items.blaze_rod
		);
		GameRegistry.addShapedRecipe(new ItemStack(torchHolder[2], 1, ItemTorchHolder.maxDamage[2] - 2),
			"I L", "I I", "ISI",
			'I', Items.diamond,
			'L', Items.leather,
			'S', Items.nether_star
		);

		for (Item holder : torchHolder)
		{
			GameRegistry.addRecipe(new RecipeTorchHolder(new ItemStack(Blocks.torch), Lists.newArrayList(new ItemStack(holder, 1, OreDictionary.WILDCARD_VALUE))));
		}

		GameRegistry.addShapedRecipe(new ItemStack(arrowHolder[0], 1, ItemTorchHolder.maxDamage[0] - 3),
			"IAL", "I I", "III",
			'I', Items.iron_ingot,
			'L', Items.leather,
			'A', Items.arrow
		);
		GameRegistry.addShapedRecipe(new ItemStack(arrowHolder[1], 1, ItemTorchHolder.maxDamage[1] - 3),
			"IAL", "I I", "ISI",
			'I', Items.gold_ingot,
			'L', Items.leather,
			'S', Items.blaze_rod,
			'A', Items.arrow
		);
		GameRegistry.addShapedRecipe(new ItemStack(arrowHolder[2], 1, ItemTorchHolder.maxDamage[2] - 3),
			"IAL", "I I", "ISI",
			'I', Items.diamond,
			'L', Items.leather,
			'S', Items.nether_star,
			'A', Items.arrow
		);

		for (Item holder : arrowHolder)
		{
			GameRegistry.addRecipe(new RecipeArrowHolder(new ItemStack(Items.arrow), Lists.newArrayList(new ItemStack(holder, 1, OreDictionary.WILDCARD_VALUE))));
		}

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(catchall),
			"P P", "PCP", "HHH",
			'P', "plankWood",
			'H', "slabWood",
			'C', Blocks.chest
		));

		GameRegistry.addShapedRecipe(new ItemStack(spanner),
			"SSS", " I ", "SSS",
			'S', Blocks.stone,
			'I', Items.iron_ingot
		);

		GameRegistry.addShapedRecipe(new ItemStack(potionHolder),
			"SLS", "BBB",
			'S', Items.string,
			'L', Items.leather,
			'B', Items.glass_bottle
		);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(transporter),
			"P P", "PHP", "HHH",
			'H', "slabWood",
			'P', "plankWood"
		));

		GameRegistry.addShapedRecipe(new ItemStack(transportManager, 1, 0),
			" B ", "SBS", "SSS",
			'B', Blocks.lapis_block,
			'S', Blocks.stone
		);
		GameRegistry.addShapedRecipe(new ItemStack(transportManager, 1, 1),
			" B ", "SBS", "SSS",
			'B', Blocks.redstone_block,
			'S', Blocks.stone
		);

		GameRegistry.addShapedRecipe(new ItemStack(pouch),
			"PPP", "BLB", "PBP",
			'L', Items.diamond,
			'P', Items.leather,
			'B', new ItemStack(noFunctionItems, 1, 0)
		);

		for (int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new RecipePouch(new ItemStack(pouch, 1, i + 1), "dye" + COLORNAME[i], new ItemStack(pouch, 1, OreDictionary.WILDCARD_VALUE)));
		}

		ItemStack upgradePouch = new ItemStack(pouch);
		upgradePouch.setStackDisplayName(upgradePouch.getDisplayName() + "(Upgrade)");
		GameRegistry.addRecipe(new RecipePouch(upgradePouch, new ItemStack(Items.ender_pearl), new ItemStack(pouch, 1, OreDictionary.WILDCARD_VALUE)));

		GameRegistry.addShapedRecipe(new ItemStack(noFunctionItems, 1, 0),
			"LLL", "LSL", "LLL",
			'L', Items.leather,
			'S', Items.string
		);
		GameRegistry.addShapedRecipe(new ItemStack(noFunctionItems, 4, 1),
			" WW", " WW", "S  ",
			'W', Blocks.wool,
			'S', Items.stick
		);
		GameRegistry.addShapedRecipe(new ItemStack(noFunctionItems, 1, 2),
			"ODO", "DED", "ODO",
			'O', Blocks.obsidian,
			'D', Items.diamond,
			'E', Items.ender_eye
		);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(noFunctionItems, 1, 3),
			"WIW", "WPW", "WPW",
			'W', "slabWood",
			'I', Items.iron_ingot,
			'P', Items.paper
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(storageBox, 1, 0),
			"IHI", "I I", "IHI",
			'I', "logWood",
			'C', new ItemStack(storageBox, 1, 0),
			'H', "slabWood"
		));

		ItemStack woodStorageBox = new ItemStack(storageBox, 1, 0);
		ItemStack halfStone = new ItemStack(Blocks.stone_slab);

		for (int i = 0; i < StorageBoxType.values().length; i++)
		{
			if (StorageBoxType.values()[i].canCraft)
			{
				for (int j = 0; j < StorageBoxType.values()[i].materials.length; j++)
				{
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(storageBox, 3, i), true,
						"IHI", "ICI", "IHI",
						'I', StorageBoxType.values()[i].materials[j],
						'C', woodStorageBox,
						'H', halfStone
					));
				}
			}
		}

		GameRegistry.addShapedRecipe(new ItemStack(storageBox, 32, 8),
			"IHI", "I I", "IHI",
			'H', Blocks.glass_pane,
			'I', Blocks.glass
		);
		GameRegistry.addShapedRecipe(new ItemStack(storageBox, 1, 9),
			"IHI", "I I", "IHI",
			'H', new ItemStack(Blocks.stone_slab, 1, 3),
			'I', Blocks.cobblestone
		);
		GameRegistry.addShapedRecipe(new ItemStack(storageBox, 2, 11),
			"OOO", "CEC", "OOO",
			'O', Blocks.obsidian,
			'C', new ItemStack(noFunctionItems, 1, 2),
			'E', new ItemStack(storageBox, 1, 10)
		);

		GameRegistry.addShapedRecipe(new ItemStack(storageBoxAddon, 1, 0),
				"SSS", "CEC", "SSS",
				'S', Blocks.end_stone,
				'C', new ItemStack(noFunctionItems, 1, 2),
				'E', new ItemStack(storageBox, 1, 10)
		);

		ItemStack lava = new ItemStack(Items.lava_bucket);
		ItemStack brush = new ItemStack(noFunctionItems, 1, 1);

		for (int i = 0; i < ItemPlating.typeIndex.length; i++)
		{
			int j = ItemPlating.typeIndex[i];

			for (int k = 0; k < StorageBoxType.values()[j].materials.length; k++)
			{
				Object ingot = StorageBoxType.values()[j].materials[k];

				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(plating, 1, i), ingot, ingot, lava, brush));
			}
		}

		proxy.registerRenderers();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandMoreInventoryMod());

		if (event.getSide().isServer() && (Version.isDev() || Config.versionNotify && Version.isOutdated()))
		{
			event.getServer().logInfo(StatCollector.translateToLocalFormatted("moreinv.version.message", "MoreInventoryMod") + ": " + Version.getLatest());
		}
	}
}