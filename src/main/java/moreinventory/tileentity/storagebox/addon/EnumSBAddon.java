package moreinventory.tileentity.storagebox.addon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.HashMap;
import java.util.Map;

public enum EnumSBAddon {
	Teleporter(5, TileEntityTeleporter.class, new String[]{"side","top","bottom"});

	public final int guiID;
	public final Class<? extends TileEntity> clazz;
	public final String[] icons;
	
	EnumSBAddon(int guiID, Class<? extends TileEntity> clazz, String[] icons){
		this.guiID = guiID;
		this.clazz = clazz;
		this.icons = icons;
	}
	
	 public static TileEntity makeEntity(int metadata)
	 {
		 int meta = validateMeta(metadata);
	       if (meta ==metadata)
	        {
	                TileEntity te;
					try 
					{
						te = values()[meta].clazz.newInstance();
						return te;
					} 
					catch (InstantiationException e) 
					{
						e.printStackTrace();
					} 
					catch (IllegalAccessException e) 
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
        else
        {
            return 0;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static Map<String,IIcon> registerIcon(IIconRegister IconRegister){
    	Map<String,IIcon> iconMap = new HashMap();
    	for(int i = 0; i < values().length; i++)
    	{
    		for(int t = 0; t < values()[i].icons.length; t++)
    		{
    			String name = values()[i].name()+"_"+values()[i].icons[t];
    			iconMap.put(values()[i].name()+"_"+values()[i].icons[t], IconRegister.registerIcon("moreinv:"+name));
    		}
    	}
    	
		return iconMap;
    }
    
    @SideOnly(Side.CLIENT)
    public static IIcon getBlockTexture(Map<String,IIcon> map,IBlockAccess world, int x, int y, int z, int side){
    	
    	int meta = world.getBlockMetadata(x, y, z);
    	
    	return getIcon(map,side,meta);
    }
    
    @SideOnly(Side.CLIENT)
    public static IIcon getIcon(Map<String,IIcon> map,int side, int meta){
    	EnumSBAddon en = values()[meta];
    	IIcon icon = null;
    	if(meta == 0){
    		if(side == 0){
    			icon = map.get(en.name()+"_"+en.icons[2]);
    		}
    		else if(side == 1)
    		{
    			icon = map.get(en.name()+"_"+en.icons[1]);
    		}
    		else
    		{
    			icon = map.get(en.name()+"_"+en.icons[0]);
    		}
    	}
		return icon;
    }
}
