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

public class GuiStorageBox extends GuiContainer
{
	private TileEntityStorageBox tile;
	private int maxStack;
	private ItemStack item;
	private EntityPlayer usingPlayer;
	private static final ResourceLocation GuiIndex = new ResourceLocation("moreinv:GUI/StorageBox2.png");

	public GuiStorageBox(InventoryPlayer inventoryPlayer, TileEntityStorageBox tileEntity)
	{
		super(new ContainerStorageBox(inventoryPlayer, tileEntity));
		this.usingPlayer = inventoryPlayer.player;
		this.xSize = 176;
		this.ySize = 190;
		this.tile = tileEntity;
		this.item = tile.getContents();
		this.maxStack = item != null ? tile.getStorageBoxType().invSize * item.getMaxStackSize() : 0;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		boolean isOwner = usingPlayer.getDisplayName().equals(tile.getOwnerName());
		this.buttonList.add(new GuiButtonStorageBox(0, guiLeft + 23, guiTop + 35, 50, 20, "[Private]", tile.isPrivate));
		this.buttonList.add(new GuiButtonStorageBox(1, guiLeft + 75, guiTop + 35, 30, 20, "[NBT]", tile.checkNBT));
		this.buttonList.add(new GuiButtonStorageBox(2, guiLeft + 107, guiTop + 35, 50, 20, "[Insert]", tile.canInsert));

		if (!isOwner)
		{
			((GuiButton) buttonList.get(0)).enabled = false;

			if (tile.isPrivate())
			{
				((GuiButton) buttonList.get(1)).enabled = false;
				((GuiButton) buttonList.get(2)).enabled = false;
			}
		}

		if (item != null)
		{
			((GuiButton) buttonList.get(1)).enabled = false;
		}

	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		int id = par1GuiButton.id;
		tile.sendGUIPacketToServer((byte) id);

	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		GuiButtonStorageBox bt = (GuiButtonStorageBox) this.buttonList.get(0);
		bt.isPushed = tile.isPrivate();
		bt = (GuiButtonStorageBox) this.buttonList.get(1);
		bt.isPushed = tile.checkNBT;
		bt = (GuiButtonStorageBox) this.buttonList.get(2);
		bt.isPushed = tile.canInsert;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2)
	{

		fontRendererObj.drawString("[Details]", 25, 77, 4210752);
		fontRendererObj.drawString("Owner: " + tile.getOwnerName(), 30, 87, 4210752);
		fontRendererObj.drawString("Connection: " + tile.connectCount, 30, 98, 4210752);
		fontRendererObj.drawString("Tier: " + tile.getStorageBoxType().Tier, 30, 108, 4210752);

		if (item != null)
		{
			String count = "";
			if (tile.getStorageBoxType() != StorageBoxType.Ender)
			{
				count = "/" + maxStack;
			}
			count = tile.ContentsItemCount + count;
			fontRendererObj.drawString(count, 58, 22, 4210752);
		}

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		this.mc.getTextureManager().bindTexture(GuiIndex);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		if (item != null)
		{
			if (tile.getStorageBoxType() != StorageBoxType.Ender)
			{
				int width = 0;
				width = 64 * tile.ContentsItemCount / maxStack;
				drawTexturedModalRect(x + 57, y + 65, xSize + 8, 5, 64, 5);
				drawTexturedModalRect(x + 57, y + 65, xSize + 8 + (int) tile.getWorldObj().getTotalWorldTime() % 6, 0, width, 5);
				String power = Integer.toString(15 * width / 64);
				int size = fontRendererObj.getStringWidth(power);
				fontRendererObj.drawStringWithShadow(power, x + 88 - size / 2, y + 65, 0x4dff00);
				fontRendererObj.drawString("E", x + 50, y + 64, 4210752);
				fontRendererObj.drawString("F", x + 123, y + 64, 4210752);
			}
		}
	}
}