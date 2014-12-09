package moreinventory.tileentity.storagebox;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public enum StorageBoxType
{
	Wood(64, TileEntityStorageBox.class, 0xFFFFFF, 1, false, null),
	Iron(128, TileEntityIronStorageBox.class, 0xFFFFFF, 2, true, new Object[] {new ItemStack(Items.iron_ingot), "ingotRefinedIron", "blockTofuMetal"}),
	Gold(256, TileEntityGoldStorageBox.class, 0xFFFFFF, 3, true, new Object[] {new ItemStack(Items.gold_ingot), "ingotElectrum"}),
	Diamond(512, TileEntityDiamondStorageBox.class, 0xFFFFFF, 4, true, new Object[] {new ItemStack(Items.diamond), "blockTofuDiamond"}),
	Copper(96, TileEntityCopperStorageBox.class, 0xFFFFFF, 2, true, new Object[] {"ingotCopper"}),
	Tin(96, TileEntityTinStorageBox.class, 0xFFFFFF, 2, true, new String[] {"ingotTin"}),
	Bronze(128, TileEntityBronzeStorageBox.class, 0xFFFFFF, 2, true, new Object[] {"ingotBronze", "ingotBrass"}),
	Silver(192, TileEntitySilverStorageBox.class, 0xFFFFFF, 3, true, new Object[] {"ingotSilver"}),
	Glass(0, TileEntityGlassStorageBox.class, 0xFFFFFF, 0, false, null),
	CobbleStone(1, TileEntityCobbleStoneStorageBox.class, 0xFFFFFF, 0, false, null),
	Emerald(1028, TileEntityEmeraldStorageBox.class, 0xFFFFFF, 5, false, new Object[] {new ItemStack(Items.emerald)}),
	Ender(2, TileEntityEnderStorageBox.class, 0xFFFFFF, 7, false, null),
	Steel(384, TileEntitySteelStorageBox.class, 0xFFFFFF, 3, false, new Object[] {"ingotSteel", "ingotInvar"});

	public final int inventorySize;
	public final Class<? extends TileEntityStorageBox> clazz;
	public int color;
	public final int tier;
	public final boolean canCraft;
	public final Object[] materials;

	private StorageBoxType(int inventorySize, Class<? extends TileEntityStorageBox> clazz, int color, int tier, boolean canCraft, Object[] materials)
	{
		this.inventorySize = inventorySize;
		this.clazz = clazz;
		this.color = color;
		this.tier = tier;
		this.canCraft = canCraft;
		this.materials = materials;
	}

	public static TileEntityStorageBox makeEntity(int metadata)
	{
		int type = validateMeta(metadata);

		if (type == metadata)
		{
			try
			{
				return values()[type].clazz.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	public static int validateMeta(int i)
	{
		if (i < values().length)
		{
			return i;
		}

		return 0;
	}
}