package moreinventory.gui;

import moreinventory.container.ContainerPotionholder;
import moreinventory.item.inventory.InventoryPotionholder;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiPotionholder extends GuiContainer
{
	private static final ResourceLocation GuiIndex = new ResourceLocation("textures/gui/container/dispenser.png");

	public GuiPotionholder(InventoryPlayer inventoryPlayer, InventoryPotionholder IInventory)
	{
		super(new ContainerPotionholder(inventoryPlayer, IInventory));
		this.xSize = 176;
		this.ySize = 190;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2)
	{
		fontRendererObj.drawString("Potion Holder", 8, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 120 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		mc.getTextureManager().bindTexture(GuiIndex);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}