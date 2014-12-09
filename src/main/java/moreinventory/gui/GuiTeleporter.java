package moreinventory.gui;

import moreinventory.container.ContainerTeleporter;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTeleporter extends GuiSBAddonBase
{
	private static final ResourceLocation configGuiTexture = new ResourceLocation("moreinv:GUI/config2.png");

	public GuiTeleporter(InventoryPlayer player, TileEntityTeleporter tile)
	{
		super(player, tile, new ContainerTeleporter(player, tile));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
	{
		mc.getTextureManager().bindTexture(configGuiTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		super.drawGuiContainerBackgroundLayer(ticks, mouseX, mouseY);
	}
}