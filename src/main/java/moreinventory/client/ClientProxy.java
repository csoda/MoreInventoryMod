package moreinventory.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.config.TransportableBlocksEntry;
import moreinventory.client.renderer.ItemCatchallRenderer;
import moreinventory.client.renderer.TileEntityCatchallRenderer;
import moreinventory.client.renderer.TileEntityStorageBoxRenderer;
import moreinventory.client.renderer.TileEntityTransportManagerRenderer;
import moreinventory.core.CommonProxy;
import moreinventory.core.Config;
import moreinventory.core.MoreInventoryMod;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void initConfigEntryClasses()
	{
		Config.transportableBlocksEntry = TransportableBlocksEntry.class;
	}

	@Override
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStorageBox.class, new TileEntityStorageBoxRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCatchall.class, new TileEntityCatchallRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransportManager.class, new TileEntityTransportManagerRenderer());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(MoreInventoryMod.catchall), new ItemCatchallRenderer());
	}
}