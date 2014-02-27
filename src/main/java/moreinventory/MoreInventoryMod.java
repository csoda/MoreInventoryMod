package moreinventory;


import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import moreinventory.block.BlockCatchall;
import moreinventory.block.BlockStorageBox;
import moreinventory.block.BlockStorageBoxAddon;
import moreinventory.block.BlockTransportManager;
import moreinventory.crafting.RecipePouch;
import moreinventory.crafting.RecipeTorchHolder;
import moreinventory.event.EventChestTPDrop;
import moreinventory.event.EventItemPickup;
import moreinventory.event.EventWorldSave;
import moreinventory.gui.GuiHandler;
import moreinventory.item.*;
import moreinventory.item.itemblock.ItemBlockSBAddon;
import moreinventory.item.itemblock.ItemBlockStorageBox;
import moreinventory.item.itemblock.ItemBlockTransportManager;
import moreinventory.network.PacketPipeline;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.tileentity.TileEntityExporter;
import moreinventory.tileentity.TileEntityImporter;
import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.addon.EnumSBAddon;
import moreinventory.util.CSWorldSaveHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.HashMap;
import java.util.Map;


@Mod(modid="MoreInventoryMod", name = "MoreInventoryMod", version="2.1.1",dependencies = "after:InvTweaks; after:IronChest")
public class MoreInventoryMod {

public static final String MODID = "MoreInventoryMod";

@Instance("MoreInventoryMod")
public static MoreInventoryMod instance;
public static CSWorldSaveHelper saveHelper;
public static final PacketPipeline packetPipeline = new PacketPipeline();

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

/***Client***/
public static boolean StorageBoxsideTexture;


public static Map<String,Integer> transportableChest = new HashMap();
public static Map<String,Map<Integer,Integer>> transportableChestIcon = new HashMap();


public static final String[] MATERIALNAME = {"Leather Pack","Brush","Dimension Core","Clipboard"};
public static final String[] COLORNAME = 	{"White","Orange","Magenta","LightBlue",
											 "Yellow", "Lime","Pink","Gray",
											 "LightGray","Cyan","Purple","Blue",
											 "Brown","Green","Red","Black"};

public static final String defaultOwner = "***Unknown***";

public static CreativeTabs customTab = new MoreInventoryModTab("MoreInventoryModTabs");

@SidedProxy(clientSide="moreinventory.ClientProxy", serverSide="moreinventory.CommonProxy")
public static CommonProxy proxy;


@Mod.EventHandler
public void preInit(FMLPreInitializationEvent event) {

	Configuration config = new Configuration(event.getSuggestedConfigurationFile());
	config.load();
	Property prop1 = config.get(Configuration.CATEGORY_GENERAL, "isCollectTorchs", true);
	prop1.comment = "Put away  when you pick up torch";
	isCollectTorch = prop1.getBoolean(true);
	Property prop2 = config.get(Configuration.CATEGORY_GENERAL, "fullAutoCollect", false);
	prop2.comment = "Pouch collects items from your inventory whenever you pick up drop items";
	isFullAutoCollectPouch = prop2.getBoolean(true);
	Property prop3 = config.get("client.general", "displayedItemSize",36);
	prop3.comment = "How many Items are displayed in catchall(1~36)";
	displayedItemSize = prop3.getInt();
	Property prop4 = config.get(Configuration.CATEGORY_GENERAL, "modChestRegistry","");
	prop4.comment = "You can register other Mod's Chest to transport. [BlockName : Metadata(-1~15) : TextureIndex(1~29), …]";
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
	
	config.addCustomCategoryComment("client.color","Color of number displayed on ContainerBox(000000 ~ ffffff)");
	Property[] prop7 = new Property[StorageBoxType.values().length];
	for(int i = 0; i < prop7.length; i++){
		if(i != 8){
			String st = StorageBoxType.values()[i].color == 0 ?"000000" : "ffffff";
	        prop7[i] = config.get("client.color", StorageBoxType.values()[i].name() + "Color", st);
	        StorageBoxType.values()[i].color = Integer.parseInt(prop7[i].getString(),16);
		}
	}
	config.save();


    /***Blocks***/
    Catchall = new BlockCatchall(Material.wood).setBlockName("catchall");
    StorageBox = new BlockStorageBox(Material.iron).setBlockName("containerbox");
    TransportManager = new BlockTransportManager(Material.rock).setBlockName("transportmanager");
    StorageBoxAddon = new BlockStorageBoxAddon(Material.iron);
    GameRegistry.registerBlock(Catchall,ItemBlock.class,"catchall");
    GameRegistry.registerBlock(StorageBox,ItemBlockStorageBox.class,"containerbox");
    GameRegistry.registerBlock(TransportManager,ItemBlockTransportManager.class,"transportmanager");
    GameRegistry.registerBlock(StorageBoxAddon,ItemBlockSBAddon.class,"StorageBoxAddon");

    /***Items***/
    ChestTransporter = new ItemChestTransporter().setUnlocalizedName("transporter");
    NoFunctionItems = new ItemNoFunction().setUnlocalizedName("itemnofunction");
    Potionholder = new ItemPotionholder().setUnlocalizedName("potionholder");
    Spanner = new ItemSpanner().setUnlocalizedName("spanner").setCreativeTab(MoreInventoryMod.customTab);
    Plating = new ItemPlating() .setUnlocalizedName("painting").setCreativeTab(MoreInventoryMod.customTab);
    Pouch = new ItemPouch().setUnlocalizedName("pouch");

    final String[] gradeName = {"Iron","Gold", "Diamond"};
    for (int i = 0; i < 3; i++){
        Torchholder[i] = new ItemTorchholder(i).setUnlocalizedName("torchholder:"+gradeName[i]);
        GameRegistry.registerItem(Torchholder[i],"torchholder"+gradeName[i]);
    }

    GameRegistry.registerItem(ChestTransporter,"transporter");
    GameRegistry.registerItem(NoFunctionItems,"itemnofunction");
    GameRegistry.registerItem(Potionholder,"potionholder");
    GameRegistry.registerItem(Spanner,"spanner");
    GameRegistry.registerItem(Plating,"painting");
    GameRegistry.registerItem(Pouch,"pouch");


    /***Events***/
    MinecraftForge.EVENT_BUS.register(new EventItemPickup());
    MinecraftForge.EVENT_BUS.register(new EventWorldSave());
    MinecraftForge.EVENT_BUS.register(new EventChestTPDrop());

    /***TileEntity***/
    GameRegistry.registerTileEntity(TileEntityCatchall.class,"containerCatchall");
    GameRegistry.registerTileEntity(TileEntityTransportManager.class, "TileEntityTransportManagerMeta");
    GameRegistry.registerTileEntity(TileEntityImporter.class, "TilEntityImporter");
    GameRegistry.registerTileEntity(TileEntityExporter.class,"TileEntityExporter");
    for(StorageBoxType type: StorageBoxType.values())
    {
        GameRegistry.registerTileEntity(type.clazz,"TileEntity"+type.name()+"StorageBox");
    }
    for(int i = 0; i < EnumSBAddon.values().length; i ++)
    {
        GameRegistry.registerTileEntity(EnumSBAddon.values()[i].clazz,"moreinventory." + EnumSBAddon.values()[i].name());
    }
}

@Mod.EventHandler
public void load(FMLInitializationEvent event) {
    LanguageRegistry.instance().addStringLocalization("itemGroup.MoreInventoryModTabs", "en_US", "MoreInventoryMod");
    NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    this.packetPipeline.init(MODID);


    /***Recipes***/
    ItemStack ingotGold = new ItemStack(Items.gold_ingot);
    ItemStack diamond = new ItemStack(Items.diamond);
    GameRegistry.addRecipe(new ItemStack(Torchholder[0],1,ItemTorchholder.maxDamage[0]-2),"I L","I I","III",
    'I',new ItemStack(Items.iron_ingot),
    'L',new ItemStack(Items.leather));
    GameRegistry.addRecipe(new RecipeTorchHolder(3,3,
    new ItemStack[]{ingotGold,null,ingotGold,
                    ingotGold,new ItemStack(this.Torchholder[0],1,32767),ingotGold,
                    ingotGold,new ItemStack(Items.blaze_rod),ingotGold},new ItemStack(Torchholder[1],1,ItemTorchholder.maxDamage[1]-2)));
    GameRegistry.addRecipe(new RecipeTorchHolder(3,3,
    new ItemStack[]{diamond,null,diamond,
                    diamond,new ItemStack(this.Torchholder[1],1,32767),diamond,
                    diamond,new ItemStack(Items.nether_star),diamond},new ItemStack(Torchholder[2],1,ItemTorchholder.maxDamage[1]-2)));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Catchall),
            new Object[]{"P P","PCP","HHH",
                'P', "plankWood",
                'H', "slabWood",
                'C',new ItemStack(Blocks.chest)}));
    GameRegistry.addRecipe(new ItemStack(Spanner),
            "SSS"," I ","SSS",
                'S',new ItemStack(Blocks.stone),
                'I',new ItemStack(Items.iron_ingot));
    GameRegistry.addRecipe(new ItemStack(Potionholder),
            "SLS","BBB",
                'S',new ItemStack(Items.string),
                'L',new ItemStack(Items.leather),
                'B',new ItemStack(Items.glass_bottle));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ChestTransporter),
            new Object[]{"P P","PHP","HHH",
                'H',"slabWood",
                'P',"plankWood"}));
    GameRegistry.addRecipe(new ItemStack(TransportManager,1,0),
            " B ","SBS","SSS",
                'B',new ItemStack(Blocks.lapis_block),
                'S',new ItemStack(Blocks.stone));
    GameRegistry.addRecipe(new ItemStack(TransportManager,1,1),
            " B ","SBS","SSS",
                'B',new ItemStack(Blocks.redstone_block),
                'S',new ItemStack(Blocks.stone));

    GameRegistry.addRecipe(new ItemStack(Pouch),
            "PPP","BLB","PBP",
                'L',new ItemStack(Items.diamond),
                'P',new ItemStack(Items.leather),
                'B',new ItemStack(NoFunctionItems,1,0));


    for(int i=0;i<16;i++){
        CraftingManager.getInstance().getRecipeList().add(new RecipePouch(new ItemStack(Pouch,1,i+1),
                new Object[]{"dye"+ this.COLORNAME[i],new ItemStack(Pouch,1,32767)}));
    }
    ItemStack upgradePouch = new ItemStack(Pouch);
    upgradePouch.setStackDisplayName(upgradePouch.getDisplayName() + "(Upgrade)");
    GameRegistry.addRecipe(new RecipePouch(upgradePouch,
            new Object[]{new ItemStack(Items.ender_pearl),new ItemStack(Pouch,1,32767)}));

    GameRegistry.addRecipe(new ItemStack(NoFunctionItems,1,0),
            "LLL","LSL","LLL",
                'L',new ItemStack(Items.leather),
                'S',new ItemStack(Items.string));
    GameRegistry.addRecipe(new ItemStack(NoFunctionItems,4,1),
            " WW"," WW" ,"S  ",
                'W',new ItemStack(Blocks.wool),
                'S',new ItemStack(Items.stick));
    GameRegistry.addRecipe(new ItemStack(NoFunctionItems,1,2),
            "ODO","DED" ,"ODO",
                'O',new ItemStack(Blocks.obsidian),
                'D',new ItemStack(Items.diamond),
                'E',new ItemStack(Items.ender_eye));
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(NoFunctionItems,1,3),
            new Object[]{"WIW","WPW" ,"WPW",
                'W',"slabWood",
                'I',new ItemStack(Items.iron_ingot),
                'P',new ItemStack(Items.paper)}));



    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StorageBox,1,0),
            new Object[]{"IHI","I I","IHI",
                'I', "logWood",'C',new ItemStack(StorageBox,1,0),
                'H', "slabWood"}));
    ItemStack woodStorageBox = new ItemStack(StorageBox,1,0);
    ItemStack halfStone = new ItemStack(Blocks.stone_slab);

    for(int i = 0; i < StorageBoxType.values().length;i++){
        if(StorageBoxType.values()[i].canCraft){
            for(int t = 0; t<StorageBoxType.values()[i].materials.length;t++){
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(StorageBox,3,i), true,
                new Object[]{"IHI","ICI","IHI",
                'I', StorageBoxType.values()[i].materials[t],'C',woodStorageBox,'H', halfStone}));
            }
        }
    }

    GameRegistry.addRecipe(new ItemStack(StorageBox,32,8),"IHI","I I","IHI",
            'H',new ItemStack(Blocks.glass_pane),
            'I',new ItemStack(Blocks.glass));
        GameRegistry.addRecipe(new ItemStack(StorageBox,1,9),"IHI","I I","IHI",
            'H',new ItemStack(Blocks.stone_slab,1,3),
            'I',new ItemStack(Blocks.cobblestone));
        GameRegistry.addRecipe(new ItemStack(StorageBox,2,11),"OOO","CEC","OOO",
            'O',new ItemStack(Blocks.obsidian),
            'C',new ItemStack(NoFunctionItems,1,2),
            'E',new ItemStack(StorageBox,1,10));

    GameRegistry.addRecipe(new ItemStack(StorageBoxAddon,1,0),"SSS","CEC","SSS",
            'S',new ItemStack(Blocks.end_stone),
            'C',new ItemStack(NoFunctionItems,1,2),
            'E',new ItemStack(StorageBox,1,10));

    ItemStack lava = new ItemStack(Items.lava_bucket);
    ItemStack brush = new ItemStack(NoFunctionItems,1,1);
    for(int i = 0; i < ItemPlating.typeIndex.length; i++){
        int k = ItemPlating.typeIndex[i];
        for(int t = 0; t < StorageBoxType.values()[k].materials.length;t++){
            Object ingot = StorageBoxType.values()[k].materials[t];
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Plating,1,i),
                    new Object[]{ingot, ingot, lava, brush}));
        }
    }

    proxy.registerRenderers();

    this.chestRegistry();
}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
        packetPipeline.postInit();
		modChestRegistry();
	}
	

	private void chestRegistry(){
        transportableChest.put(Blocks.chest.getUnlocalizedName(), -1);
        transportableChest.put(Blocks.trapped_chest.getUnlocalizedName(), -1);
        transportableChest.put(StorageBox.getUnlocalizedName(), -1);

		modChest = modChest.trim();
		if(modChest.length()>0)
		{
			String[] chestData = modChest.split(",");
			String[] chestData2;

            for (String aChestData : chestData) {
                chestData2 = aChestData.split(":");
                String name = chestData2[0].trim();
                int damage = Integer.parseInt(chestData2[1].trim());

                transportableChest.put(name, damage);
                if (!transportableChestIcon.containsKey(name)) {
                    transportableChestIcon.put(name, new HashMap<Integer, Integer>());
                }
                int iconindex = chestData2[2].trim().length() > 0 ? Byte.parseByte(chestData2[2].trim()) : 0;
                transportableChestIcon.get(name).put(damage, iconindex);
            }
		}
	}
	   
	private void modChestRegistry(){
        Block block;
        block = Block.getBlockFromName("tile.ironchest");
        if(block != null)
        {
            transportableChest.put(block.getUnlocalizedName(),-1);
            for (int t = 0; t < 7; t++) {
                transportableChestIcon.get(block.getUnlocalizedName()).put(t, (t + 1));
            }
        }

        block = Block.getBlockFromName("tile.multipagechest");
        if(block != null)
        {
            transportableChest.put(block.getUnlocalizedName(),-1);
            transportableChestIcon.get(block.getUnlocalizedName()).put(-1, 11);
        }
	}

}
