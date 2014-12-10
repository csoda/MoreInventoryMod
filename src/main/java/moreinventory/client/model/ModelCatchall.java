package moreinventory.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCatchall extends ModelBase
{
	ModelRenderer Shape1;
	ModelRenderer Shape21;
	ModelRenderer Shape22;
	ModelRenderer Shape31;
	ModelRenderer Shape32;

	public ModelCatchall()
	{
		textureWidth = 16;
		textureHeight = 16;

		Shape1 = new ModelRenderer(this, 0, 0);
		Shape1.addBox(0F, 0F, 0F, 16, 1, 16);
		Shape1.setRotationPoint(-8F, 23F, -8F);
		Shape1.setTextureSize(16, 16);
		Shape1.mirror = true;
		setRotation(Shape1, 0F, 0F, 0F);
		Shape21 = new ModelRenderer(this, 0, 0);
		Shape21.addBox(0F, 0F, 0F, 15, 11, 1);
		Shape21.setRotationPoint(-8F, 12F, 7F);
		Shape21.setTextureSize(16, 16);
		Shape21.mirror = true;
		setRotation(Shape21, 0F, 0F, 0F);
		Shape22 = new ModelRenderer(this, 0, 0);
		Shape22.addBox(0F, 0F, 0F, 15, 11, 1);
		Shape22.setRotationPoint(-7F, 12F, -8F);
		Shape22.setTextureSize(16, 16);
		Shape22.mirror = true;
		setRotation(Shape22, 0F, 0F, 0F);
		Shape31 = new ModelRenderer(this, 0, 0);
		Shape31.addBox(0F, 0F, 0F, 1, 11, 15);
		Shape31.setRotationPoint(7F, 12F, -7F);
		Shape31.setTextureSize(16, 16);
		Shape31.mirror = true;
		setRotation(Shape31, 0F, 0F, 0F);
		Shape32 = new ModelRenderer(this, 0, 0);
		Shape32.addBox(0F, 0F, 0F, 1, 11, 15);
		Shape32.setRotationPoint(-8F, 12F, -8F);
		Shape32.setTextureSize(16, 16);
		Shape32.mirror = true;
		setRotation(Shape32, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5);
		Shape1.render(f5);
		Shape21.render(f5);
		Shape22.render(f5);
		Shape31.render(f5);
		Shape32.render(f5);
	}

	public void renderAll()
	{
		Shape1.render(0.0625F);
		Shape21.render(0.0625F);
		Shape22.render(0.0625F);
		Shape31.render(0.0625F);
		Shape32.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, null);
	}
}