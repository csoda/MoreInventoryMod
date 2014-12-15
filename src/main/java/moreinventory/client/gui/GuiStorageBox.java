package moreinventory.client.gui;

import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.client.gui.button.GuiButtonStorageBox;
import moreinventory.container.ContainerStorageBox;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiStorageBox extends GuiContainer
{
	private static final ResourceLocation storageBoxGuiTexture = new ResourceLocation("moreinv:GUI/StorageBox2.png");

	private final EntityPlayer usingPlayer;
	private final TileEntityStorageBox storageBox;
	private final ItemStack contents;
	private final int maxStack;

	protected GuiButtonStorageBox privateButton, nbtButton, insertButton;
	protected HoverChecker privateHover, nbtHover, insertHover;

	public GuiStorageBox(InventoryPlayer inventory, TileEntityStorageBox tile)
	{
		super(new ContainerStorageBox(inventory, tile));
		this.xSize = 176;
		this.ySize = 190;
		this.usingPlayer = inventory.player;
		this.storageBox = tile;
		this.contents = storageBox.getContents();
		this.maxStack = contents != null ? storageBox.getStorageBoxType().inventorySize * contents.getMaxStackSize() : 0;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		int id = buttonList.size();

		if (privateButton == null)
		{
			privateButton = new GuiButtonStorageBox(id++, 0, 0, 50, 20, "[" + I18n.format("containerbox.gui.private") + "]", storageBox.isPrivate);
		}

		privateButton.xPosition = guiLeft + 23;
		privateButton.yPosition = guiTop + 35;

		if (nbtButton == null)
		{
			nbtButton = new GuiButtonStorageBox(id++, 0, 0, 30, 20, "[" + I18n.format("containerbox.gui.nbt") + "]", storageBox.checkNBT);
		}

		nbtButton.xPosition = guiLeft + 75;
		nbtButton.yPosition = guiTop + 35;

		if (insertButton == null)
		{
			insertButton = new GuiButtonStorageBox(id, 0, 0, 50, 20, "[" + I18n.format("containerbox.gui.insert") + "]", storageBox.canInsert);
		}

		insertButton.xPosition = guiLeft + 107;
		insertButton.yPosition = guiTop + 35;

		buttonList.add(privateButton);
		buttonList.add(nbtButton);
		buttonList.add(insertButton);

		if (!usingPlayer.getUniqueID().toString().equals(storageBox.getOwner()))
		{
			privateButton.enabled = false;

			if (storageBox.isPrivate())
			{
				nbtButton.enabled = false;
				insertButton.enabled = false;
			}
		}

		if (contents != null)
		{
			nbtButton.enabled = false;
		}

		privateHover = new HoverChecker(privateButton, 800);
		nbtHover = new HoverChecker(nbtButton, 800);
		insertHover = new HoverChecker(insertButton, 800);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		storageBox.sendGUIPacketToServer((byte)button.id);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		privateButton.isPushed = storageBox.isPrivate();
		nbtButton.isPushed = storageBox.checkNBT;
		insertButton.isPushed = storageBox.canInsert;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		super.drawScreen(mouseX, mouseY, ticks);

		if (privateHover.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("containerbox.gui.private.tooltip"), width / 2), mouseX, mouseY);
		}
		else if (nbtHover.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("containerbox.gui.nbt.tooltip"), width / 2), mouseX, mouseY);
		}
		else if (insertHover.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format("containerbox.gui.insert.tooltip"), width / 2), mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString("[" + I18n.format("containerbox.gui.details") + "]", 25, 77, 4210752);
		fontRendererObj.drawString(I18n.format("moreinv.gui.owner") + ": " + storageBox.getOwnerName(), 30, 87, 4210752);
		fontRendererObj.drawString(I18n.format("containerbox.gui.connection") + ": " + storageBox.connectCount, 30, 98, 4210752);
		fontRendererObj.drawString(I18n.format("containerbox.gui.tier") + ": " + storageBox.getStorageBoxType().tier, 30, 108, 4210752);

		if (contents != null)
		{
			String count = "";

			if (storageBox.getStorageBoxType() != StorageBoxType.Ender)
			{
				count = "/" + maxStack;
			}

			count = storageBox.contentsCount + count;

			fontRendererObj.drawString(count, 58, 22, 4210752);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
	{
		mc.getTextureManager().bindTexture(storageBoxGuiTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if (contents != null)
		{
			if (storageBox.getStorageBoxType() != StorageBoxType.Ender)
			{
				int width = 64 * storageBox.contentsCount / maxStack;
				drawTexturedModalRect(guiLeft + 57, guiTop + 65, xSize + 8, 5, 64, 5);
				drawTexturedModalRect(guiLeft + 57, guiTop + 65, xSize + 8 + (int)storageBox.getWorldObj().getTotalWorldTime() % 6, 0, width, 5);

				String power = Integer.toString(15 * width / 64);
				fontRendererObj.drawStringWithShadow(power, guiLeft + 88 - fontRendererObj.getStringWidth(power) / 2, guiTop + 65, 0x4DFF00);
				fontRendererObj.drawString("E", guiLeft + 50, guiTop + 64, 4210752);
				fontRendererObj.drawString("F", guiLeft + 123, guiTop + 64, 4210752);
			}
		}
	}
}