package moreinventory.gui;

import moreinventory.container.ContainerTeleporter;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiTeleporter extends GuiSBAddonBase
{
	private static final ResourceLocation GuiIndex = new ResourceLocation("moreinv:GUI/config2.png");

	public GuiTeleporter(InventoryPlayer inventoryPlayer, TileEntityTeleporter tileEntity)
	{
		super(inventoryPlayer, tileEntity, new ContainerTeleporter(inventoryPlayer, tileEntity));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		this.mc.getTextureManager().bindTexture(GuiIndex);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
	}
}