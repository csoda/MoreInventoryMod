package moreinventory.tileentity.storagebox.addon;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.Map;

public enum EnumSBAddon
{
	Teleporter(5, TileEntityTeleporter.class, new String[] {"side", "top", "bottom"});

	public final int guiID;
	public final Class<? extends TileEntity> clazz;
	public final String[] icons;

	private EnumSBAddon(int guiID, Class<? extends TileEntity> clazz, String[] icons)
	{
		this.guiID = guiID;
		this.clazz = clazz;
		this.icons = icons;
	}

	public static TileEntity makeEntity(int metadata)
	{
		int meta = validateMeta(metadata);

		if (meta == metadata)
		{
			try
			{
				return values()[meta].clazz.newInstance();
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

	@SideOnly(Side.CLIENT)
	public static Map<String, IIcon> registerIcon(IIconRegister iconRegister)
	{
		Map<String, IIcon> iconMap = Maps.newHashMap();

		for (int i = 0; i < values().length; i++)
		{
			for (int j = 0; j < values()[i].icons.length; j++)
			{
				iconMap.put(values()[i].name() + "_" + values()[i].icons[j], iconRegister.registerIcon("moreinv:" + values()[i].name() + "_" + values()[i].icons[j]));
			}
		}

		return iconMap;
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getBlockTexture(Map<String, IIcon> map, IBlockAccess world, int x, int y, int z, int side)
	{
		return getIcon(map, side, world.getBlockMetadata(x, y, z));
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getIcon(Map<String, IIcon> map, int side, int meta)
	{
		EnumSBAddon addon = values()[meta];

		if (meta == 0)
		{
			switch (side)
			{
				case 0:
					return map.get(addon.name() + "_" + addon.icons[2]);
				case 1:
					return map.get(addon.name() + "_" + addon.icons[1]);
				default:
					return map.get(addon.name() + "_" + addon.icons[0]);
			}
		}

		return null;
	}
}