package moreinventory.client.renderer;

import moreinventory.client.model.ModelImporter;
import moreinventory.tileentity.TileEntityImporter;
import moreinventory.tileentity.TileEntityTransportManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityTransportManagerRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation[] transportManagerTextures = new ResourceLocation[5];
	{
		transportManagerTextures[0] = new ResourceLocation("textures/blocks/anvil_base.png");
		transportManagerTextures[1] = new ResourceLocation("moreinv:textures/blocks/Importer.png");
		transportManagerTextures[2] = new ResourceLocation("moreinv:textures/blocks/Importer_black.png");
		transportManagerTextures[3] = new ResourceLocation("moreinv:textures/blocks/Exporter.png");
		transportManagerTextures[4] = new ResourceLocation("moreinv:textures/blocks/Exporter_black.png");
	}

	private final ModelImporter model = new ModelImporter();

	public void renderModelAt(TileEntity tile, double posX, double posY, double posZ, float ticks)
	{
		int rotation1 = 0;
		int rotation2 = 0;
		int index = tile instanceof TileEntityImporter ? 1 : 3;
		boolean[] light = new boolean[5];

		if (tile.getWorldObj() != null)
		{
			rotation1 = ((TileEntityTransportManager)tile).face;
			rotation2 = ((TileEntityTransportManager)tile).topFace;

			int time = (int)tile.getWorldObj().getTotalWorldTime() % 24;
			boolean flag = tile.getWorldObj().isBlockIndirectlyGettingPowered(tile.xCoord, tile.yCoord, tile.zCoord);

			if (!flag && 0 <= time && time <= 4)
			{
				light[0] = true;
			}

			if (!flag && 5 <= time && time <= 9)
			{
				light[1] = true;
			}

			if (!flag && 10 <= time && time <= 14)
			{
				light[2] = true;
			}

			if (!flag && 15 <= time && time <= 19)
			{
				light[3] = true;
			}

			if (!flag)
			{
				light[4] = true;
			}
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float)posX + 0.5F, (float)posY + 0.5F, (float)posZ + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		rotate(rotation1);
		bindTexture(transportManagerTextures[0]);
		model.renderBottom();
		renderBottomLight(light, index);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslatef((float)posX + 0.5F, (float)posY + 0.5F, (float)posZ + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		rotate(Facing.oppositeSide[rotation2]);
		bindTexture(transportManagerTextures[0]);
		model.renderTop();
		renderTopLight(light, index);
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

	private void renderBottomLight(boolean[] light, int index)
	{
		bindTexture(transportManagerTextures[index]);
		model.renderBottomLight(light[0], light[1], light[2]);
		bindTexture(transportManagerTextures[index + 1]);
		model.renderBottomLight(!light[0], !light[1], !light[2]);
	}

	private void renderTopLight(boolean[] light, int index)
	{
		bindTexture(transportManagerTextures[index]);
		model.renderTopLight(light[3], light[4]);
		bindTexture(transportManagerTextures[index + 1]);
		model.renderTopLight(!light[3], !light[4]);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double posX, double posY, double posZ, float ticks)
	{
		renderModelAt(tile, posX, posY, posZ, ticks);
	}
}