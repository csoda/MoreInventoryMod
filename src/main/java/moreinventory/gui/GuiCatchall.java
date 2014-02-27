package moreinventory.gui;

import moreinventory.container.ContainerCatchall;
import moreinventory.tileentity.TileEntityCatchall;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiCatchall extends GuiContainer {
	
	private static final ResourceLocation GuiIndex = new ResourceLocation("moreinv:GUI/Catchall.png");

    public GuiCatchall (InventoryPlayer inventoryPlayer,TileEntityCatchall tileEntity) {
	    super(new ContainerCatchall(inventoryPlayer, tileEntity));
	    xSize = 176;
	    ySize = 190;
}

@Override
protected void drawGuiContainerForegroundLayer(int param1, int param2) {
    fontRendererObj.drawString("Catchall", 8, 6, 4210752);
    fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
}

@Override
protected void drawGuiContainerBackgroundLayer(float par1, int par2,int par3) {
	this.mc.getTextureManager().bindTexture(GuiIndex);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    int x = (width - xSize) / 2;
    int y = (height - ySize) / 2;
    this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
}
	
}
