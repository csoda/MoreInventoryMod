package moreinventory;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import moreinventory.render.RendererItemCatchall;
import moreinventory.render.RendererTileEntityCatchall;
import moreinventory.render.RendererTileEntityStorageBox;
import moreinventory.render.RendererTileEntityTransportManager;
import moreinventory.tileentity.TileEntityCatchall;
import moreinventory.tileentity.TileEntityTransportManager;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.CSutil;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	    @Override
	    public void registerRenderers() {
	    	ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStorageBox.class, new RendererTileEntityStorageBox());
	    	ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCatchall.class, new RendererTileEntityCatchall());
	    	ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransportManager.class, new RendererTileEntityTransportManager());
	    	MinecraftForgeClient.registerItemRenderer(CSutil.getItemBlock(MoreInventoryMod.Catchall), new RendererItemCatchall());
		    //MinecraftForgeClient.registerItemRenderer(MoreInventoryMod.Importer.blockID, new RendererItemImporter());
	    }

		@Override
		public World getClientWorld()
		{
			return FMLClientHandler.instance().getClient().theWorld;
		}
		@Override
		public boolean isClient(){
			return true;
		}

}
