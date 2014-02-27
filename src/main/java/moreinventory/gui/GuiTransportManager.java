package moreinventory.gui;

import moreinventory.container.ContainerTransportManager;
import moreinventory.gui.button.GuiButtonConfigString;
import moreinventory.tileentity.TileEntityImporter;
import moreinventory.tileentity.TileEntityTransportManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiTransportManager extends GuiContainer {
	
	private static final ResourceLocation GuiIndex = new ResourceLocation("textures/gui/container/dispenser.png");
	private static final ResourceLocation GuiIndex2 = new ResourceLocation("moreinv:GUI/config.png");

	
	TileEntityTransportManager tile;
	String button1,button2;
	
    public GuiTransportManager (InventoryPlayer inventoryPlayer, TileEntityTransportManager tileEntity){
    	super(new ContainerTransportManager(inventoryPlayer, tileEntity));
    	xSize = 176;
    	ySize = 190;
    	tile  = tileEntity;
    }

    @Override
    public void initGui(){
    	super.initGui();
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
    	this.buttonList.add(new GuiButtonConfigString(buttonList.size(), x+xSize+45, y + 43, 20, 20, "B",tile.sneak==0));
    	this.buttonList.add(new GuiButtonConfigString(buttonList.size(), x+xSize+5,  y + 43, 20, 20, "T",tile.sneak==1));
    	this.buttonList.add(new GuiButtonConfigString(buttonList.size(), x+xSize+25, y + 43, 20, 20, "N",tile.sneak==2));
    	this.buttonList.add(new GuiButtonConfigString(buttonList.size(), x+xSize+25, y + 63, 20, 20, "S",tile.sneak==3));
    	this.buttonList.add(new GuiButtonConfigString(buttonList.size(), x+xSize+5,  y + 63, 20, 20, "W",tile.sneak==4));
    	this.buttonList.add(new GuiButtonConfigString(buttonList.size(), x+xSize+45, y + 63, 20, 20, "E",tile.sneak==5));
    	this.buttonList.add(new GuiButtonConfigString(buttonList.size(), x+xSize+5,  y + 23, 60, 20, "Default",tile.sneak==6));
    	if(tile instanceof TileEntityImporter){
    		button2 = ((TileEntityImporter) tile).Include ? "Include":"Exclude";
    		this.buttonList.add(new GuiButtonConfigString(7, (width - xSize) / 2+5,  (height - ySize) / 2+35, 53, 20, "Register",((TileEntityImporter) tile).isRegister));
    		this.buttonList.add(new GuiButton(8, (width - xSize) / 2+xSize - 58,  (height - ySize) / 2+35, 53, 20, button2));
    	}
    }
    
    protected void actionPerformed(GuiButton par1GuiButton)
    {
    	int id = par1GuiButton.id;
    	if(0<=id&&id<=6){
    		tile.sneak = (byte)id;
    		for(int i = 0; i<7;i++){
    			GuiButtonConfigString bt = (GuiButtonConfigString) this.buttonList.get(i);
    			bt.isPushed = i==id;
    		}
    		tile.sendCommonPacketToServer();
    	}
    	
    	if(tile instanceof TileEntityImporter){
	    	if(id==7){
				GuiButtonConfigString bt = (GuiButtonConfigString) this.buttonList.get(7);
				((TileEntityImporter) tile).isRegister = !((TileEntityImporter) tile).isRegister;
				bt.isPushed = ((TileEntityImporter) tile).isRegister;
	
	    	}
	    	if(id==8){
	    		GuiButton bt = (GuiButton) this.buttonList.get(8);
	    		((TileEntityImporter) tile).Include = !((TileEntityImporter) tile).Include;
	    		bt.displayString = ((TileEntityImporter) tile).Include ? "Include":"Exclude";
	    	}
			((TileEntityImporter) tile).sendPacketToServer(id == 7);
    	}
    }
    
    
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		String name = tile instanceof TileEntityImporter ? "Importer" : "Exporter";
	    fontRendererObj.drawString(name, 8, 6, 4210752);
	    fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 120 + 2, 4210752);
	    fontRendererObj.drawString("Sneaking", xSize+18, 10, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
	            int par3) {
		this.mc.getTextureManager().bindTexture(GuiIndex);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    int x = (width - xSize) / 2;
	    int y = (height - ySize) / 2;
	    this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	    //this.mc.renderEngine.bindTexture("/mods/moreinv/GUI/config.png");
	    this.mc.getTextureManager().bindTexture(GuiIndex2);
	    this.drawTexturedModalRect(x + xSize, y+3, 0, 0, 72, 92);
	    this.drawTexturedModalRect(x + xSize+1, y+5, 24, 92, 16, 16);
	}

}
