package moreinventory;

import moreinventory.render.RendererItemCatchall;
import moreinventory.render.RendererTileEntityCatchall;
import moreinventory.render.RendererTileEntityStorageBox;
import moreinventory.render.RendererTileEntityTransportManager;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.CSUtil;
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStorageBox.class, new RendererTileEntityStorageBox());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCatchall.class, new RendererTileEntityCatchall());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransportManager.class, new RendererTileEntityTransportManager());
		MinecraftForgeClient.registerItemRenderer(CSUtil.getItemBlock(MoreInventoryMod.Catchall), new RendererItemCatchall());
		// MinecraftForgeClient.registerItemRenderer(MoreInventoryMod.Importer.blockID, new RendererItemImporter());
	}
}