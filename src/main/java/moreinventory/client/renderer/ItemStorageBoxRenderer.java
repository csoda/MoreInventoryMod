package moreinventory.client.renderer;

import com.google.common.collect.Maps;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.model.ModelItemStorageBox;
import moreinventory.item.ItemBlockStorageBox;
import moreinventory.tileentity.storagebox.StorageBoxType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class ItemStorageBoxRenderer implements IItemRenderer
{
	private final ModelItemStorageBox model = new ModelItemStorageBox();
	private final HashMap<String, ResourceLocation> textureMap = Maps.newHashMap();

	public ItemStorageBoxRenderer()
	{
		for (Entry<String, StorageBoxType> type : StorageBoxType.types.entrySet())
		{
			String name = type.getKey().toLowerCase(Locale.ENGLISH);
			String folder = StorageBoxType.getTextureFolder(type.getKey());

			if (!type.getKey().equals("Glass"))
			{
				textureMap.put(type.getKey() , new ResourceLocation(folder + ":textures/blocks/storagebox_" + name + "_side.png"));
			}
			else
			{
				textureMap.put(type.getKey() , new ResourceLocation(folder + ":textures/blocks/storagebox_" + name + "_0.png"));
			}
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		if(item != null)
		{
			String typeName = ItemBlockStorageBox.readTypeNameFromNBT(item.getTagCompound());
			FMLClientHandler.instance().getClient().getTextureManager().bindTexture(textureMap.get(typeName));
			GL11.glPushMatrix();
			GL11.glTranslatef(0.5F, 1.5F, 0.5F);
			GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
			model.renderAll();
			GL11.glPopMatrix();
		}
	}
}
