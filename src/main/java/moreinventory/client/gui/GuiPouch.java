package moreinventory.client.gui;

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

		buttonList.add(new GuiButtonConfig(0, guiLeft + xSize + 6, guiTop + 25 + grade * 18, 16, 16, 184, 120, pouch.isCollectedByBox, ""));
		buttonList.add(new GuiButtonConfig(1, guiLeft + xSize + 24, guiTop + 25 + grade * 18, 16, 16, 200, 120, pouch.isCollectMainInv, ""));
		buttonList.add(new GuiButtonConfig(2, guiLeft + xSize + 42, guiTop + 25 + grade * 18, 16, 16, 216, 120, pouch.isAutoCollect, ""));
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		pouch.sendPacketToServer(button.id);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		pouch.isUseableByPlayer(usingPlayer);

		((GuiButtonConfig)buttonList.get(0)).isPushed = pouch.isCollectedByBox;
		((GuiButtonConfig)buttonList.get(1)).isPushed = pouch.isCollectMainInv;
		((GuiButtonConfig)buttonList.get(2)).isPushed = pouch.isAutoCollect;
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

		for (int i = 0; i < grade; i++)
		{
			drawTexturedModalRect(guiLeft + xSize, guiTop + 23 + i * 18, xSize + 8, 20, 68, 18);
		}

		drawTexturedModalRect(guiLeft + xSize, guiTop + 23 + grade * 18, xSize + 8, 38, 68, 23);
		drawTexturedModalRect(guiLeft + xSize + 1, guiTop + 5, xSize + 8 + 48, 104 + 16, 16, 16);
	}
}