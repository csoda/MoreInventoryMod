package moreinventory.core;

import moreinventory.renderer.ItemCatchallRenderer;
import moreinventory.renderer.TileEntityCatchallRenderer;
import moreinventory.renderer.TileEntityStorageBoxRenderer;
import moreinventory.renderer.TileEntityTransportManagerRenderer;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.MIMUtils;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStorageBox.class, new TileEntityStorageBoxRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCatchall.class, new TileEntityCatchallRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransportManager.class, new TileEntityTransportManagerRenderer());

		MinecraftForgeClient.registerItemRenderer(MIMUtils.getItemBlock(MoreInventoryMod.Catchall), new ItemCatchallRenderer());
	}
}