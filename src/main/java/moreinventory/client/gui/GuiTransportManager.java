package moreinventory.client.gui;

import moreinventory.client.gui.button.GuiButtonConfigString;
import moreinventory.container.ContainerTransportManager;
import moreinventory.tileentity.TileEntityImporter;
import moreinventory.tileentity.TileEntityTransportManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTransportManager extends GuiContainer
{
	private static final ResourceLocation dispenserGuiTexture = new ResourceLocation("textures/gui/container/dispenser.png");
	private static final ResourceLocation configGuiTexture = new ResourceLocation("moreinv:GUI/config.png");

	private final TileEntityTransportManager transportManager;

	public GuiTransportManager(InventoryPlayer inventoryPlayer, TileEntityTransportManager tileEntity)
	{
		super(new ContainerTransportManager(inventoryPlayer, tileEntity));
		this.xSize = 176;
		this.ySize = 190;
		this.transportManager = tileEntity;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiTop + xSize + 45, guiLeft + 43, 20, 20, "B", transportManager.sneak == 0));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiTop + xSize + 5, guiLeft + 43, 20, 20, "T", transportManager.sneak == 1));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiTop + xSize + 25, guiLeft + 43, 20, 20, "N", transportManager.sneak == 2));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiTop + xSize + 25, guiLeft + 63, 20, 20, "S", transportManager.sneak == 3));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiTop + xSize + 5, guiLeft + 63, 20, 20, "W", transportManager.sneak == 4));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiTop + xSize + 45, guiLeft + 63, 20, 20, "E", transportManager.sneak == 5));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiTop + xSize + 5, guiLeft + 23, 60, 20, "Default", transportManager.sneak == 6));

		if (transportManager instanceof TileEntityImporter)
		{
			buttonList.add(new GuiButtonConfigString(7, (width - xSize) / 2 + 5, (height - ySize) / 2 + 35, 53, 20, "Register", ((TileEntityImporter)transportManager).register));
			buttonList.add(new GuiButton(8, (width - xSize) / 2 + xSize - 58, (height - ySize) / 2 + 35, 53, 20, ((TileEntityImporter)transportManager).include ? "Include" : "Exclude"));
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (0 <= button.id && button.id <= 6)
		{
			transportManager.sneak = (byte)button.id;

			for (int i = 0; i < 7; i++)
			{
				((GuiButtonConfigString)buttonList.get(i)).isPushed = i == button.id;
			}

			transportManager.sendCommonPacketToServer();
		}

		if (transportManager instanceof TileEntityImporter)
		{
			switch (button.id)
			{
				case 7:
					((TileEntityImporter)transportManager).register = !((TileEntityImporter)transportManager).register;
					((GuiButtonConfigString)buttonList.get(7)).isPushed = ((TileEntityImporter)transportManager).register;
					break;
				case 8:
					((TileEntityImporter)transportManager).include = !((TileEntityImporter)transportManager).include;
					((GuiButton)buttonList.get(8)).displayString = ((TileEntityImporter)transportManager).include ? "Include" : "Exclude";
					break;
			}

			((TileEntityImporter)transportManager).sendPacketToServer(button.id == 7);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(transportManager instanceof TileEntityImporter ? "Importer" : "Exporter", 8, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 120 + 2, 4210752);
		fontRendererObj.drawString("Sneaking", xSize + 18, 10, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
	{
		mc.getTextureManager().bindTexture(dispenserGuiTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		mc.getTextureManager().bindTexture(configGuiTexture);
		drawTexturedModalRect(guiLeft + xSize, guiTop + 3, 0, 0, 72, 92);
		drawTexturedModalRect(guiLeft + xSize + 1, guiTop + 5, 24, 92, 16, 16);
	}
}