package moreinventory.handler;

import com.google.common.collect.Lists;
import moreinventory.util.INBTSaveData;
import moreinventory.util.MIMLog;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MIMWorldSaveHelper
{
	public final World world;

	private final File saveFile;
	private final List<INBTSaveData> saveList;

	public MIMWorldSaveHelper(World world, String name, List<INBTSaveData> list)
	{
		this.world = world;
		this.saveFile = new File(DimensionManager.getCurrentSaveRootDirectory(), name + ".dat");
		this.saveList = list;
		this.loadData();
	}

	public MIMWorldSaveHelper(World world, String name, INBTSaveData... data)
	{
		this(world, name, Lists.newArrayList(data));
	}

	public void saveData()
	{
		try
		{
			NBTTagCompound nbt = new NBTTagCompound();

			for (INBTSaveData save : saveList)
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

				for (INBTSaveData save : saveList)
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