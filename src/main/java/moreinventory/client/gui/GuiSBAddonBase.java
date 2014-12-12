package moreinventory.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.gui.button.GuiButtonConfigString;
import moreinventory.client.gui.button.GuiButtonInvisible;
import moreinventory.container.ContainerSBAddonBase;
import moreinventory.tileentity.storagebox.addon.EnumSBAddon;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class GuiSBAddonBase extends GuiContainer
{
	private static final ResourceLocation configGuiTexture = new ResourceLocation("moreinv:GUI/config.png");

	private static boolean tabVisible = false;

	private final TileEntitySBAddonBase addonBase;

	public GuiSBAddonBase(InventoryPlayer inventory, TileEntitySBAddonBase tile, ContainerSBAddonBase container)
	{
		super(container);
		this.xSize = 176;
		this.ySize = 190;
		this.addonBase = tile;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.add(new GuiButtonInvisible(0, guiLeft + xSize, guiTop + 3, 20, 20));
		buttonList.add(new GuiButtonConfigString(1, guiLeft + xSize + 5, guiTop + 23, 60, 20, I18n.format("containerbox.gui.private"), addonBase.isPrivate()));
		((GuiButton)buttonList.get(1)).visible = tabVisible;
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					tabVisible = !tabVisible;
					((GuiButton)buttonList.get(1)).visible = tabVisible;
					break;
				case 1:
					addonBase.sendCommonGuiPacketToServer((byte)0, false);
					break;
			}
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		((GuiButtonConfigString)buttonList.get(1)).isPushed = addonBase.isPrivate();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String name = "";

		for (int i = 0; i < EnumSBAddon.values().length; i++)
		{
			if (EnumSBAddon.values()[i].clazz.equals(addonBase.getClass()))
			{
				name = EnumSBAddon.values()[i].name();
			}
		}

		fontRendererObj.drawString(I18n.format(name + ".name"), 8, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 120 + 2, 4210752);

		if (tabVisible)
		{
			fontRendererObj.drawString(I18n.format("moreinv.gui.menu"), xSize + 20, 10, 4210752);
		}

		int x = mouseX - guiLeft;
		int y = mouseY - guiTop;

		if (xSize < x && x < xSize + 20 && 0 < y && y < 20)
		{
			drawCreativeTabHoveringText(I18n.format("moreinv.gui.owner") + ": " + addonBase.displayedOwnerName, x - 30, y);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(configGuiTexture);

		if (tabVisible)
		{
			drawTexturedModalRect(guiLeft + xSize, guiTop + 3, 0, 0, 72, 46);
			drawTexturedModalRect(guiLeft + xSize, guiTop + 3 + 46, 0, 88, 72, 4);
		}
		else
		{
			drawTexturedModalRect(guiLeft + xSize, guiTop + 3, 0, 92, 20, 20);
		}

		drawTexturedModalRect(guiLeft + xSize + 1, guiTop + 4, 40, 92, 16, 16);
	}
}