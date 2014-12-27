package moreinventory.client.renderer;

import com.google.common.collect.Maps;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.model.ModelItemStorageBox;
import moreinventory.item.ItemBlockStorageBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class ItemStorageBoxRenderer implements IItemRenderer
{
	public static final Map<String, ResourceLocation> textureMap = Maps.newHashMap();

	private final ModelItemStorageBox model = new ModelItemStorageBox();

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
		if (item != null)
		{
			String typeName = ItemBlockStorageBox.readTypeNameFromNBT(item.getTagCompound());
			FMLClientHandler.instance().getClient().getTextureManager().bindTexture(textureMap.get(typeName));
			GL11.glPushMatrix();

			if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON)
			{
				GL11.glTranslatef(1.0F, 2.0F, 1.0F);
			}
			else
			{
				GL11.glTranslatef(0.5F, 1.5F, 0.5F);
			}

			GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
			model.renderAll();
			GL11.glPopMatrix();
		}
	}
}
