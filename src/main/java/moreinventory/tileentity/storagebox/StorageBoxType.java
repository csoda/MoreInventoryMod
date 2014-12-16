package moreinventory.tileentity.storagebox;

import cpw.mods.fml.common.registry.GameRegistry;
import moreinventory.core.MoreInventoryMod;
import moreinventory.item.ItemBlockStorageBox;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public  class StorageBoxType
{
	public static LinkedHashMap<String, StorageBoxType> types = new LinkedHashMap<String, StorageBoxType>();

	public int inventorySize;
	public Class clazz;
	public int color;
	public int tier;
	public boolean canCraft;
	public Object[] materials;
	public boolean isDefault;
	public String textureFolder;

	public StorageBoxType(int inventorySize, Class clazz, int color, int tier, boolean canCraft, Object[] materials, String textureFolder, boolean isDefault)
	{
		this.inventorySize = inventorySize;
		this.clazz = clazz;
		this.color = color;
		this.tier = tier;
		this.canCraft = canCraft;
		this.materials = materials;
		this.textureFolder = textureFolder;
		this.isDefault = isDefault;
	}

	public static boolean compareTypes(TileEntityStorageBox tile1 , TileEntityStorageBox tile2)
	{
		if(tile1 != null)
		{
			return compareTypes(tile2 , tile1.getTypeName());
		}

		return false;
	}

	public static boolean compareTypes(TileEntityStorageBox tile, String type)
	{
		if (tile != null && tile.getTypeName().equals(type))
		{
			return true;
		}

		return false;
	}

	public static boolean isExistType(String type)
	{
		return types.containsKey(type);
	}

	public static int getInventorysize(String type)
	{
		if (isExistType(type))
		{
			return types.get(type).inventorySize;
		}

		return types.get("Wood").inventorySize;
	}

	public static int getNumberColor(String type)
	{
		if (isExistType(type))
		{
			return types.get(type).color;
		}

		return types.get("Wood").color;
	}

	public static int getTier(String type)
	{
		if (isExistType(type))
		{
			return types.get(type).tier;
		}

		return types.get("Wood").tier;
	}

	public static TileEntityStorageBox makeEntity(String type)
	{

		if (!isExistType(type))
		{
			type = "Wood";
		}

		try
		{
			return (TileEntityStorageBox)types.get(type).clazz.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static String getTextureFolder(String type)
	{
		if (isExistType(type))
		{
			return types.get(type).textureFolder;
		}

		return types.get("CobbleStone").textureFolder;
	}

	public static void initialize()
	{
		String name = "moreinv";
		StorageBoxType.types.put("Wood", new StorageBoxType(64, TileEntityStorageBox.class, 0xFFFFFF, 1, false, null , name, true));
		StorageBoxType.types.put("Iron", new StorageBoxType(128, TileEntityIronStorageBox.class, 0xFFFFFF, 2, true, new Object[] {new ItemStack(Items.iron_ingot), "ingotRefinedIron", "blockTofuMetal"}, name, true));
		StorageBoxType.types.put("Gold", new StorageBoxType(256, TileEntityGoldStorageBox.class, 0xFFFFFF, 3, true, new Object[] {new ItemStack(Items.gold_ingot), "ingotElectrum"}, name, true));
		StorageBoxType.types.put("Diamond", new StorageBoxType(512, TileEntityDiamondStorageBox.class, 0xFFFFFF, 4, true, new Object[] {new ItemStack(Items.diamond), "blockTofuDiamond"}, name, true));
		StorageBoxType.types.put("Copper", new StorageBoxType(96, TileEntityCopperStorageBox.class, 0xFFFFFF, 2, true, new Object[] {"ingotCopper"}, name, true));
		StorageBoxType.types.put("Tin", new StorageBoxType(96, TileEntityTinStorageBox.class, 0xFFFFFF, 2, true, new String[] {"ingotTin"} , name, true));
		StorageBoxType.types.put("Bronze", new StorageBoxType(128, TileEntityBronzeStorageBox.class, 0xFFFFFF, 2, true, new Object[] {"ingotBronze", "ingotBrass"}, name, true));
		StorageBoxType.types.put("Silver", new StorageBoxType(192, TileEntitySilverStorageBox.class, 0xFFFFFF, 3, true, new Object[] {"ingotSilver"} , name, true));
		StorageBoxType.types.put("Glass", new StorageBoxType(0, TileEntityGlassStorageBox.class, 0xFFFFFF, 0, false, null , name, true));
		StorageBoxType.types.put("Cobblestone", new StorageBoxType(1, TileEntityCobbleStoneStorageBox.class, 0xFFFFFF, 0, false, null , name, true));
		StorageBoxType.types.put("Emerald", new StorageBoxType(1028, TileEntityEmeraldStorageBox.class, 0xFFFFFF, 5, false, new Object[] {new ItemStack(Items.emerald)} , name, true));
		StorageBoxType.types.put("Ender", new StorageBoxType(2, TileEntityEnderStorageBox.class, 0xFFFFFF, 0, false, null , name, true));
		StorageBoxType.types.put("Steel", new StorageBoxType(384, TileEntitySteelStorageBox.class, 0xFFFFFF, 3, false, new Object[] {"ingotSteel", "ingotInvar"} , name, true));


		Set typeSet = StorageBoxType.types.entrySet();

		/* class */
		for (Iterator i = typeSet.iterator(); i.hasNext();)
		{
			Map.Entry<String, StorageBoxType> type = (Map.Entry<String, StorageBoxType>)i.next();
			GameRegistry.registerTileEntity(type.getValue().clazz, "TileEntity" + type.getKey() + "StorageBox");
		}

		/* recipe */
		ItemStack stoneslab = new ItemStack(Blocks.stone_slab);
		ItemStack woodStorageBox = new ItemStack(MoreInventoryMod.storageBox, 1, 0);
		ItemBlockStorageBox.writeToNBT(woodStorageBox, "Wood");

		for (Iterator i = typeSet.iterator(); i.hasNext();)
		{
			Map.Entry<String, StorageBoxType> type = (Map.Entry<String, StorageBoxType>)i.next();
			if (type.getValue().canCraft)
			{
				for (int t = 0; t < type.getValue().materials.length; t++)
				{

					ItemStack itemstack = new ItemStack(MoreInventoryMod.storageBox,3,0);
					ItemBlockStorageBox.writeToNBT(itemstack, type.getKey());
					GameRegistry.addRecipe(new ShapedOreRecipe(itemstack, true,
							"IHI", "ICI", "IHI",
							'I', type.getValue().materials[t],
							'C', woodStorageBox,
							'H', stoneslab
					));
				}
			}
		}
	}
}