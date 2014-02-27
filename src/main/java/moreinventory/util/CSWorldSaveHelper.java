package moreinventory.util;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.io.*;
import java.util.List;

/**
 * @author c_soda
 * This is World-Save helper.
 * You can use this  to save your original NBT data into each World.
 */

public class CSWorldSaveHelper {
	public World world;
	private String saveFileName;
	private List<IWorldDataSave> SaveList;
	
	public CSWorldSaveHelper(World world, String name , List<IWorldDataSave> save){
		this.world = world;
		saveFileName = name;
		SaveList = save;
		loadData();
	}
	
	public void saveData(){
		MinecraftServer mc = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		NBTTagCompound nbt = new NBTTagCompound();
		int k = SaveList.size();
		for(int i = 0; i < k; i++){
			SaveList.get(i).writeToNBT(nbt);
		}
		
		File saveDir = this.getSaveDir(mc);
		
		if (!saveDir.exists())
		{
			saveDir.mkdirs();
		}
		
		File saveFile = this.getSaveFile(mc);
		
		try {
			if (!saveFile.exists())
			{
				saveFile.createNewFile();
			}
			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(saveFile));
			
			} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void loadData(){
		try{
			MinecraftServer mc = FMLCommonHandler.instance().getMinecraftServerInstance();
			File saveDir = this.getSaveDir(mc);
			
			if (!saveDir.exists())
			{
				saveDir.mkdirs();
				return;
			}
			
			File saveFile = this.getSaveFile(mc);
			
			if (saveFile.exists())
			{
				NBTTagCompound nbt = CompressedStreamTools.readCompressed(new BufferedInputStream(new  FileInputStream(saveFile)));
				int k = SaveList.size();
				for(int i = 0; i < k; i++){
					SaveList.get(i).readFromNBT(nbt);
				}
			}
		
		}catch(IOException ioexception){
			
			ioexception.printStackTrace();
		}
	}
	
	public File getSaveDir(MinecraftServer mc)
	{
		return mc.isSinglePlayer() ? new File(mc.getFile("saves"), world.getSaveHandler().getWorldDirectoryName())
				:new File(mc.getFolderName());
	}
	
	public File getSaveFile(MinecraftServer mc)
	{
		return (new File(this.getSaveDir(mc).toString(), this.saveFileName + ".dat"));
	}
	
	
	
}
