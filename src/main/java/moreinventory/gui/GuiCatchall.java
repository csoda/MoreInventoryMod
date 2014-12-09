package moreinventory.gui;

import moreinventory.container.ContainerCatchall;
import moreinventory.tileentity.TileEntityCatchall;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCatchall extends GuiContainer
{
	private static final ResourceLocation catchallGuiTexture = new ResourceLocation("moreinv:GUI/Catchall.png");

	public GuiCatchall(InventoryPlayer inventory, TileEntityCatchall tile)
	{
		super(new ContainerCatchall(inventory, tile));
		this.xSize = 176;
		this.ySize = 190;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(I18n.format("containerbox.name"), 8, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
	{
		mc.getTextureManager().bindTexture(catchallGuiTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}
