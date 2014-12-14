package moreinventory.handler;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import moreinventory.util.IWorldDataSave;
import moreinventory.util.MIMLog;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.List;

public class MIMWorldSaveHelper
{
	private static final File
	rootDir = (File)FMLInjectionData.data()[6], /* The minecraft dir */
	savesDir = new File(rootDir, "saves");

	public final World world;

	private final File saveFile;
	private final List<IWorldDataSave> saveList;

	public MIMWorldSaveHelper(World world, String name, List<IWorldDataSave> list)
	{
		this.world = world;
		this.saveFile = new File(FMLLaunchHandler.side().isClient() ? savesDir : rootDir, name + ".dat");
		this.saveList = list;
		this.loadData();
	}

	public MIMWorldSaveHelper(World world, String name, IWorldDataSave... data)
	{
		this(world, name, Lists.newArrayList(data));
	}

	public void saveData()
	{
		try
		{
			NBTTagCompound nbt = new NBTTagCompound();

			for (IWorldDataSave save : saveList)
			{
				save.writeToNBT(nbt);
			}

			if (!saveFile.getParentFile().exists())
			{
				saveFile.getParentFile().mkdirs();
			}

			if (!saveFile.exists())
			{
				saveFile.createNewFile();
			}

			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(saveFile));
		}
		catch (IOException e)
		{
			MIMLog.log(Level.ERROR, e, "An error occurred trying to save the " + FilenameUtils.getBaseName(saveFile.getName()));
		}
	}

	public void loadData()
	{
		try
		{
			if (!saveFile.getParentFile().exists())
			{
				saveFile.getParentFile().mkdirs();
			}

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
			MIMLog.log(Level.ERROR, e, "An error occurred trying to load the " + FilenameUtils.getBaseName(saveFile.getName()));
		}
	}
}