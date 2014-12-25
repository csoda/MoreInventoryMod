package moreinventory.client.gui;

import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.gui.button.GuiButtonConfig;
import moreinventory.container.ContainerPouch;
import moreinventory.inventory.InventoryPouch;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiPouch extends GuiContainer
{
	private static final ResourceLocation pouchGuiTexture = new ResourceLocation("moreinv:GUI/Pouch.png");

	private final EntityPlayer usingPlayer;
	private final InventoryPouch pouch;
	private final int grade;

	private GuiButtonConfig config1Button, config2Button, config3Button;
	private HoverChecker config1Hover, config2Hover, config3Hover;

	public GuiPouch(InventoryPlayer inventory, InventoryPouch pouchInventory)
	{
		super(new ContainerPouch(inventory, pouchInventory));
		this.xSize = 176;
		this.ySize = 223;
		this.usingPlayer = inventory.player;
		this.pouch = pouchInventory;
		this.grade = pouch.getGrade() + 2;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		if (config1Button == null)
		{
			config1Button = new GuiButtonConfig(0, 0, 0, 16, 16, 184, 120, pouch.isCollectedByBox, "");
		}

		config1Button.xPosition = guiLeft + xSize + 6;
		config1Button.yPosition = guiTop + 25 + grade * 18;

		if (config2Button == null)
		{
			config2Button = new GuiButtonConfig(1, 0, 0, 16, 16, 200, 120, pouch.isCollectMainInv, "");
		}

		config2Button.xPosition = guiLeft + xSize + 24;
		config2Button.yPosition = config1Button.yPosition;

		if (config3Button == null)
		{
			config3Button = new GuiButtonConfig(2, 0, 0, 16, 16, 216, 120, pouch.isAutoCollect, "");
		}

		config3Button.xPosition = guiLeft + xSize + 42;
		config3Button.yPosition = config1Button.yPosition;

		buttonList.add(config1Button);
		buttonList.add(config2Button);
		buttonList.add(config3Button);

		if (config1Hover == null)
		{
			config1Hover = new HoverChecker(config1Button, 800);
		}

		if (config2Hover == null)
		{
			config2Hover = new HoverChecker(config2Button, 800);
		}

		if (config3Hover == null)
		{
			config3Hover = new HoverChecker(config3Button, 800);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		pouch.sendPacketToServer(button.id);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		super.drawScreen(mouseX, mouseY, ticks);

		if (config1Hover.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("pouch.gui.config1.tooltip"), width / 2), mouseX, mouseY);
		}
		else if (config2Hover.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("pouch.gui.config2.tooltip"), width / 2), mouseX, mouseY);
		}
		else if (config3Hover.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("pouch.gui.config3.tooltip"), width / 2), mouseX, mouseY);
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		pouch.isUseableByPlayer(usingPlayer);

		config1Button.isPushed = pouch.isCollectedByBox;
		config2Button.isPushed = pouch.isCollectMainInv;
		config3Button.isPushed = pouch.isAutoCollect;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(pouch.customName, 8, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		fontRendererObj.drawString(I18n.format("moreinv.gui.config"), xSize + 18, 10, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
	{
		mc.getTextureManager().bindTexture(pouchGuiTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + xSize, guiTop + 3, xSize + 8, 0, 68, 20);

		for (int i = 0; i < grade; ++i)
		{
			drawTexturedModalRect(guiLeft + xSize, guiTop + 23 + i * 18, xSize + 8, 20, 68, 18);
		}

		drawTexturedModalRect(guiLeft + xSize, guiTop + 23 + grade * 18, xSize + 8, 38, 68, 23);
		drawTexturedModalRect(guiLeft + xSize + 1, guiTop + 5, xSize + 8 + 48, 104 + 16, 16, 16);
	}
}