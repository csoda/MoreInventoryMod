package moreinventory.util;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.util.MathHelper;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

public class Version extends RecursiveAction
{
	private static final Version instance = new Version();

	private static Optional<String> CURRENT = Optional.absent();
	private static Optional<String> LATEST = Optional.absent();

	private static Optional<Status> status = Optional.fromNullable(Status.PENDING);

	public static String getCurrent()
	{
		return CURRENT.orNull();
	}

	public static String getLatest()
	{
		return LATEST.or(getCurrent());
	}

	public static Status getStatus()
	{
		return status.orNull();
	}

	public static boolean isDev()
	{
		return !FMLForgePlugin.RUNTIME_DEOBF || getStatus() == Status.AHEAD || getStatus() == Status.BETA || getStatus() == Status.BETA_OUTDATED;
	}

	public static boolean isOutdated()
	{
		return getStatus() == Status.OUTDATED;
	}

	public static void initialize()
	{
		CURRENT = Optional.of(Strings.nullToEmpty(MoreInventoryMod.metadata.version));
		LATEST = Optional.fromNullable(CURRENT.orNull());
	}

	public static void versionCheck()
	{
		if (!CURRENT.isPresent() || !LATEST.isPresent())
		{
			initialize();
		}

		MIMUtils.getPool().execute(instance);
	}

	@Override
	protected void compute()
	{
		try
		{
			URL url = new URL(MoreInventoryMod.metadata.updateUrl);
			Map<String, Object> data = null;

			try (InputStream input = url.openStream())
			{
				byte[] dat = ByteStreams.toByteArray(input);

				if (dat != null && dat.length > 0)
				{
					data = new Gson().fromJson(new String(dat), Map.class);
				}
			}
			finally
			{
				if (data == null)
				{
					status = Optional.of(Status.FAILED);

					return;
				}
			}

			if (data.containsKey("homepage"))
			{
				MoreInventoryMod.metadata.url = String.valueOf(data.get("homepage"));
			}

			if (data.containsKey("description"))
			{
				MoreInventoryMod.metadata.description = String.valueOf(data.get("description"));
			}

			Map<String, String> versions = Maps.newHashMap();

			if (data.containsKey("versions"))
			{
				versions = (Map<String, String>)data.get("versions");
			}

			String version = versions.get(MinecraftForge.MC_VERSION);
			ArtifactVersion current = new DefaultArtifactVersion(CURRENT.or("1.0.0"));

			if (!Strings.isNullOrEmpty(version))
			{
				ArtifactVersion latest = new DefaultArtifactVersion(version);

				LATEST = Optional.of(version);

				switch (MathHelper.clamp_int(latest.compareTo(current), -1, 1))
				{
					case 0:
						status = Optional.of(Status.UP_TO_DATE);
						return;
					case -1:
						status = Optional.of(Status.AHEAD);
						return;
					case 1:
						status = Optional.of(Status.OUTDATED);
						return;
					default:
						status = Optional.of(Status.FAILED);
						return;
				}
			}

			version = versions.get("latest");

			if (!Strings.isNullOrEmpty(version))
			{
				LATEST = Optional.of(version);
			}

			status = Optional.of(Status.FAILED);
		}
		catch (Exception e)
		{
			MIMLog.log(Level.WARN, e, "An error occurred trying to version check");

			status = Optional.of(Status.FAILED);
		}
	}
}