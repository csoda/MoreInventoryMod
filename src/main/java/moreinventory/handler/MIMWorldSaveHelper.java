package moreinventory.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import moreinventory.util.IWorldDataSave;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;

public class MIMWorldSaveHelper
{
	public final World world;
	private final String saveFileName;
	private final List<IWorldDataSave> saveList;

	public MIMWorldSaveHelper(World world, String name, List<IWorldDataSave> save)
	{
		this.world = world;
		this.saveFileName = name;
		this.saveList = save;
		this.loadData();
	}

	public void saveData()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		if (server == null)
		{
			return;
		}

		NBTTagCompound nbt = new NBTTagCompound();

		for (IWorldDataSave save : saveList)
		{
			save.writeToNBT(nbt);
		}

		File saveDir = getSaveDir(server);

		if (!saveDir.exists())
		{
			saveDir.mkdirs();
		}

		File saveFile = getSaveFile(server);

		try
		{
			if (!saveFile.exists())
			{
				saveFile.createNewFile();
			}

			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(saveFile));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void loadData()
	{
		try
		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			if (server == null)
			{
				return;
			}

			File saveDir = getSaveDir(server);

			if (!saveDir.exists())
			{
				saveDir.mkdirs();

				return;
			}

			File saveFile = getSaveFile(server);

			if (saveFile.exists())
			{
				NBTTagCompound nbt = CompressedStreamTools.readCompressed(new BufferedInputStream(new FileInputStream(saveFile)));

				for (IWorldDataSave save : saveList)
				{
					save.readFromNBT(nbt);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public File getSaveDir(MinecraftServer server)
	{
		return server.isSinglePlayer() ? new File(server.getFile("saves"), world.getSaveHandler().getWorldDirectoryName()) : new File(server.getFolderName());
	}

	public File getSaveFile(MinecraftServer server)
	{
		return new File(getSaveDir(server).toString(), saveFileName + ".dat");
	}
}