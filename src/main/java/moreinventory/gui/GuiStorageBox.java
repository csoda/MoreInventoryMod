package moreinventory.gui;

import moreinventory.container.ContainerStorageBox;
import moreinventory.gui.button.GuiButtonStorageBox;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStorageBox extends GuiContainer
{
	private static final ResourceLocation storageBoxGuiTexture = new ResourceLocation("moreinv:GUI/StorageBox2.png");

	private final EntityPlayer usingPlayer;
	private final TileEntityStorageBox storageBox;
	private final ItemStack contents;
	private final int maxStack;

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

		buttonList.add(new GuiButtonStorageBox(0, guiLeft + 23, guiTop + 35, 50, 20, "[Private]", storageBox.isPrivate));
		buttonList.add(new GuiButtonStorageBox(1, guiLeft + 75, guiTop + 35, 30, 20, "[NBT]", storageBox.checkNBT));
		buttonList.add(new GuiButtonStorageBox(2, guiLeft + 107, guiTop + 35, 50, 20, "[Insert]", storageBox.canInsert));

		if (!usingPlayer.getDisplayName().equals(storageBox.getOwnerName()))
		{
			((GuiButton)buttonList.get(0)).enabled = false;

			if (storageBox.isPrivate())
			{
				((GuiButton)buttonList.get(1)).enabled = false;
				((GuiButton)buttonList.get(2)).enabled = false;
			}
		}

		if (contents != null)
		{
			((GuiButton)buttonList.get(1)).enabled = false;
		}
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

		((GuiButtonStorageBox)buttonList.get(0)).isPushed = storageBox.isPrivate();
		((GuiButtonStorageBox)buttonList.get(1)).isPushed = storageBox.checkNBT;
		((GuiButtonStorageBox)buttonList.get(2)).isPushed = storageBox.canInsert;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString("[Details]", 25, 77, 4210752);
		fontRendererObj.drawString("Owner: " + storageBox.getOwnerName(), 30, 87, 4210752);
		fontRendererObj.drawString("Connection: " + storageBox.connectCount, 30, 98, 4210752);
		fontRendererObj.drawString("Tier: " + storageBox.getStorageBoxType().tier, 30, 108, 4210752);

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
				fontRendererObj.drawStringWithShadow(power, guiLeft + 88 - fontRendererObj.getStringWidth(power) / 2, guiTop + 65, 0x4dff00);
				fontRendererObj.drawString("E", guiLeft + 50, guiTop + 64, 4210752);
				fontRendererObj.drawString("F", guiLeft + 123, guiTop + 64, 4210752);
			}
		}
	}
}