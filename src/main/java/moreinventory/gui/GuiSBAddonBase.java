package moreinventory.gui;

import moreinventory.container.ContainerSBAddonBase;
import moreinventory.gui.button.GuiButtonConfigString;
import moreinventory.gui.button.GuiButtonInvisible;
import moreinventory.tileentity.storagebox.addon.EnumSBAddon;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public abstract class GuiSBAddonBase extends GuiContainer
{

	private static final ResourceLocation ownerGuiIndex = new ResourceLocation("moreinv:GUI/config.png");
	protected static boolean tabFlg = false;

	TileEntitySBAddonBase tile;
	String playerName;

	public GuiSBAddonBase(InventoryPlayer inventoryPlayer, TileEntitySBAddonBase tileEntity,
			ContainerSBAddonBase container)
	{
		super(container);
		xSize = 176;
		ySize = 190;
		tile = tileEntity;
		playerName = inventoryPlayer.player.getDisplayName();
	}

	@Override
	public void initGui()
	{
		super.initGui();
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.buttonList.add(new GuiButtonInvisible(0, x + xSize, y + 3, 20, 20));
		this.buttonList.add(new GuiButtonConfigString(1, x + xSize + 5, y + 23, 60, 20, "Private", tile.isPrivate()));
		((GuiButton) this.buttonList.get(1)).visible = tabFlg;
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		int id = par1GuiButton.id;

		if (id == 0)
		{
			tabFlg = !tabFlg;
			((GuiButton) this.buttonList.get(1)).visible = tabFlg;
		}
		else if (id == 1)
		{
			tile.sendCommonGuiPacketToServer((byte) 0, false);
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		GuiButtonConfigString bt = (GuiButtonConfigString) this.buttonList.get(1);
		bt.isPushed = tile.isPrivate();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String name = "";
		for (int i = 0; i < EnumSBAddon.values().length; i++)
		{
			if (EnumSBAddon.values()[i].clazz.equals(tile.getClass()))
			{
				name = EnumSBAddon.values()[i].name();
			}
		}
		fontRendererObj.drawString(name, 8, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 120 + 2, 4210752);
		if (tabFlg)
		{
			fontRendererObj.drawString("Menu", xSize + 20, 10, 4210752);
		}

		int x = par1 - guiLeft;
		int y = par2 - guiTop;
		if (xSize < x && x < xSize + 20 && 0 < y && y < 20)
		{
			this.drawCreativeTabHoveringText("Owner:" + tile.getOwnerName(), x - 30, y);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.mc.getTextureManager().bindTexture(ownerGuiIndex);
		if (tabFlg)
		{
			this.drawTexturedModalRect(x + xSize, y + 3, 0, 0, 72, 46);
			this.drawTexturedModalRect(x + xSize, y + 3 + 46, 0, 88, 72, 4);
		}
		else
		{
			this.drawTexturedModalRect(x + xSize, y + 3, 0, 92, 20, 20);
		}
		this.drawTexturedModalRect(x + xSize + 1, y + 4, 40, 92, 16, 16);
	}
}
