package moreinventory.render;

import moreinventory.model.ModelImporter;
import moreinventory.tileentity.TileEntityImporter;
import moreinventory.tileentity.TileEntityTransportManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RendererTileEntityTransportManager extends TileEntitySpecialRenderer
{
	private static final ResourceLocation[] TextureIndex = new ResourceLocation[5];
	{
		TextureIndex[0] = new ResourceLocation("textures/blocks/anvil_base.png");
		TextureIndex[1] = new ResourceLocation("moreinv:textures/blocks/Importer.png");
		TextureIndex[2] = new ResourceLocation("moreinv:textures/blocks/Importer_black.png");
		TextureIndex[3] = new ResourceLocation("moreinv:textures/blocks/Exporter.png");
		TextureIndex[4] = new ResourceLocation("moreinv:textures/blocks/Exporter_black.png");
	}

	private ModelImporter model;

	public RendererTileEntityTransportManager()
	{
		model = new ModelImporter();
	}

	public void renderAModelAt(TileEntity tile, double d, double d1, double d2, float f)
	{

		int rotation1 = 0;
		int rotation2 = 0;
		int textureIndex = tile instanceof TileEntityImporter ? 1 : 3;
		boolean[] light = new boolean[5];
		if (tile.getWorldObj() != null)
		{
			rotation1 = ((TileEntityTransportManager) tile).face;
			rotation2 = ((TileEntityTransportManager) tile).topFace;
		}

		if (tile.getWorldObj() != null)
		{
			int time = (int) tile.getWorldObj().getTotalWorldTime() % 24;
			boolean flg = tile.getWorldObj().isBlockIndirectlyGettingPowered(tile.xCoord, tile.yCoord, tile.zCoord);
			if (!flg && 0 <= time && time <= 4)
				light[0] = true;
			if (!flg && 5 <= time && time <= 9)
				light[1] = true;
			if (!flg && 10 <= time && time <= 14)
				light[2] = true;
			if (!flg && 15 <= time && time <= 19)
				light[3] = true;
			if (!flg)
				light[4] = true;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 0.5F, (float) d2 + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		rotate(rotation1);
		this.bindTexture(TextureIndex[0]);
		model.renderBottom();
		renderBottomLight(light, textureIndex);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5F, (float) d1 + 0.5F, (float) d2 + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		rotate(Facing.oppositeSide[rotation2]);
		this.bindTexture(TextureIndex[0]);
		model.renderTop();
		renderTopLight(light, textureIndex);
		GL11.glPopMatrix();
	}

	private void rotate(int rotation)
	{
		switch (rotation)
		{
			case 1:
				GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
				break;
			case 2:
				GL11.glRotatef(90F, -1.0F, 0.0F, 0.0F);
				break;
			case 3:
				GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
				break;
			case 4:
				GL11.glRotatef(90F, 0.0F, 0.0F, -1.0F);
				break;
			case 5:
				GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
				break;
		}
	}

	private void renderBottomLight(boolean[] l, int index)
	{
		this.bindTexture(TextureIndex[index]);
		model.renderBottomLight(l[0], l[1], l[2]);
		this.bindTexture(TextureIndex[index + 1]);
		model.renderBottomLight(!l[0], !l[1], !l[2]);
	}

	private void renderTopLight(boolean[] l, int index)
	{
		this.bindTexture(TextureIndex[index]);
		model.renderTopLight(l[3], l[4]);
		this.bindTexture(TextureIndex[index + 1]);
		model.renderTopLight(!l[3], !l[4]);
	}

	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderAModelAt(par1TileEntity, par2, par4, par6, par8);
	}
}