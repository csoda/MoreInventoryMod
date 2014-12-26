package moreinventory.plugin.appeng;

import appeng.api.AEApi;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

public class AppEngPlugin
{
	public static final String MODID = "appliedenergistics2";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		AEApi.instance().registries().externalStorage().addExternalStorageInterface(new AEExternalStorageHandler());
	}
}