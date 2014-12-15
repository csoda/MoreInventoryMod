package moreinventory.client.gui;

import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

@SideOnly(Side.CLIENT)
public class GuiTransportManager extends GuiContainer
{
	private static final ResourceLocation dispenserGuiTexture = new ResourceLocation("textures/gui/container/dispenser.png");
	private static final ResourceLocation configGuiTexture = new ResourceLocation("moreinv:GUI/config.png");

	private final TileEntityTransportManager transportManager;

	protected GuiButtonConfigString registerButton;
	protected GuiButton cludeButton;
	protected HoverChecker cludeHover;

	public GuiTransportManager(InventoryPlayer inventoryPlayer, TileEntityTransportManager tile)
	{
		super(new ContainerTransportManager(inventoryPlayer, tile));
		this.xSize = 176;
		this.ySize = 190;
		this.transportManager = tile;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiLeft + xSize + 45, guiTop + 43, 20, 20, "B", transportManager.sneak == 0));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiLeft + xSize + 5, guiTop + 43, 20, 20, "T", transportManager.sneak == 1));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiLeft + xSize + 25, guiTop + 43, 20, 20, "N", transportManager.sneak == 2));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiLeft + xSize + 25, guiTop + 63, 20, 20, "S", transportManager.sneak == 3));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiLeft + xSize + 5, guiTop + 63, 20, 20, "W", transportManager.sneak == 4));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiLeft + xSize + 45, guiTop + 63, 20, 20, "E", transportManager.sneak == 5));
		buttonList.add(new GuiButtonConfigString(buttonList.size(), guiLeft + xSize + 5, guiTop + 23, 60, 20, I18n.format("moreinv.gui.default"), transportManager.sneak == 6));

		if (transportManager instanceof TileEntityImporter)
		{
			if (registerButton == null)
			{
				registerButton = new GuiButtonConfigString(7, 0, 0, 53, 20, I18n.format("moreinv.gui.register"), ((TileEntityImporter)transportManager).register);
			}

			registerButton.xPosition = guiLeft + 5;
			registerButton.yPosition = guiTop + 35;

			if (cludeButton == null)
			{
				cludeButton = new GuiButton(8, 0, 0, 53, 20, ((TileEntityImporter)transportManager).include ? I18n.format("transportmanager.gui.include") : I18n.format("transportmanager.gui.exclude"));
			}

			cludeButton.xPosition = guiLeft + xSize - 58;
			cludeButton.yPosition = guiTop + 35;

			buttonList.add(registerButton);
			buttonList.add(cludeButton);

			cludeHover = new HoverChecker(cludeButton, 800);
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
					registerButton.isPushed = ((TileEntityImporter)transportManager).register;
					break;
				case 8:
					((TileEntityImporter)transportManager).include = !((TileEntityImporter)transportManager).include;
					cludeButton.displayString = ((TileEntityImporter)transportManager).include ? I18n.format("transportmanager.gui.include") : I18n.format("transportmanager.gui.exclude");
					break;
			}

			((TileEntityImporter)transportManager).sendPacketToServer(button.id == 7);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		super.drawScreen(mouseX, mouseY, ticks);

		if (cludeHover != null && cludeHover.checkHover(mouseX, mouseY) && transportManager instanceof TileEntityImporter)
		{
			if (((TileEntityImporter)transportManager).include)
			{
				func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("transportmanager.gui.include.tooltip"), width / 2), mouseX, mouseY);
			}
			else
			{
				func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("transportmanager.gui.exclude.tooltip"), width / 2), mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		boolean flag = transportManager instanceof TileEntityImporter;
		fontRendererObj.drawString(flag ? I18n.format("transportmanager:importer.name") : I18n.format("transportmanager:exporter.name"), 8, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 120 + 2, 4210752);
		fontRendererObj.drawString(flag ? I18n.format("transportmanager.gui.sneaking.importer") : I18n.format("transportmanager.gui.sneaking.exporter"), xSize + 18, 10, 4210752);
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