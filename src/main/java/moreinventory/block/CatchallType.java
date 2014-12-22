package moreinventory.block;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.registry.GameRegistry;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Map;
import java.util.Map.Entry;

public class CatchallType
{
	public static final Map<String, CatchallType> types = Maps.newLinkedHashMap();

	public Object[] material;
	public float hardness, resistance;
	public String iconName;
	public ResourceLocation texturePath;

	public static final ResourceLocation woodTexture = new ResourceLocation("textures/blocks/planks_oak.png");

	public CatchallType(Object[] material, float hardness, float resistance, String icon)
	{
		this.material = material;
		this.hardness = hardness;
		this.resistance = resistance;
		this.iconName = icon;

		String s1 = "minecraft";
		String s2 = icon;
		int i = icon.indexOf(58);

		if (i >= 0)
		{
			s2 = icon.substring(i + 1, icon.length());

			if (i > 1)
			{
				s1 = icon.substring(0, i);
			}
		}

		this.texturePath = new ResourceLocation(s1.toLowerCase(), "textures/blocks/" + s2 + ".png");
	}

	public CatchallType(Object[] material, float hardness, String icon)
	{
		this(material, hardness, hardness * 2.0F, icon);
	}

	public CatchallType(Object[] material, String icon)
	{
		this(material, 1.0F, icon);
	}

	public static ItemStack createItemStack(String type)
	{
		ItemStack itemstack = new ItemStack(MoreInventoryMod.catchall);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("TypeName", type);
		itemstack.setTagCompound(nbt);

		return itemstack;
	}

	public static float getHardness(String type)
	{
		if (!types.containsKey(type))
		{
			return 1.0F;
		}

		return types.get(type).hardness;
	}

	public static float getResistance(String type)
	{
		if (!types.containsKey(type))
		{
			return 2.0F;
		}

		return types.get(type).resistance;
	}

	public static String getIconName(String type)
	{
		if (!types.containsKey(type))
		{
			return "planks_oak";
		}

		return types.get(type).iconName;
	}

	public static ResourceLocation getTexturePath(String type)
	{
		if (!types.containsKey(type))
		{
			return woodTexture;
		}

		return types.get(type).texturePath;
	}

	public static void initialize()
	{
		types.put("Oak", new CatchallType(new Object[] {new ItemStack(Blocks.planks, 1, 0), new ItemStack(Blocks.wooden_slab, 1, 0)}, "planks_oak"));
		types.put("Spruce", new CatchallType(new Object[] {new ItemStack(Blocks.planks, 1, 1), new ItemStack(Blocks.wooden_slab, 1, 1)}, "planks_spruce"));
		types.put("Birch", new CatchallType(new Object[] {new ItemStack(Blocks.planks, 1, 2), new ItemStack(Blocks.wooden_slab, 1, 2)}, "planks_birch"));
		types.put("Jungle", new CatchallType(new Object[] {new ItemStack(Blocks.planks, 1, 3), new ItemStack(Blocks.wooden_slab, 1, 3)}, "planks_jungle"));
		types.put("Acacia", new CatchallType(new Object[] {new ItemStack(Blocks.planks, 1, 4), new ItemStack(Blocks.wooden_slab, 1, 4)}, "planks_acacia"));
		types.put("DarkOak", new CatchallType(new Object[] {new ItemStack(Blocks.planks, 1, 5), new ItemStack(Blocks.wooden_slab, 1, 5)}, "planks_big_oak"));

		for (Entry<String, CatchallType> entry : types.entrySet())
		{
			CatchallType type = entry.getValue();

			GameRegistry.addRecipe(new ShapedOreRecipe(createItemStack(entry.getKey()),
				"P P", "PCP", "HHH",
				'P', type.material[0],
				'H', type.material[1],
				'C', Blocks.chest
			));
		}
	}
}