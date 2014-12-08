package moreinventory.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelImporter extends ModelBase
{
	ModelRenderer Shape1;
	ModelRenderer Shape2;
	ModelRenderer Shape4A;
	ModelRenderer Shape11A;
	ModelRenderer Shape14A;
	ModelRenderer Shape9A;
	ModelRenderer Shape7A;
	ModelRenderer Shape5;
	ModelRenderer Shape6;
	ModelRenderer Shape3;

	public ModelImporter()
	{
		textureWidth = 32;
		textureHeight = 32;

		Shape1 = new ModelRenderer(this, 0, 20);
		Shape1.addBox(-4F, 6 - 16F, -4F, 8, 1, 8);
		Shape1.setRotationPoint(0F, 16F, 0F);
		Shape1.setTextureSize(32, 32);
		Shape1.mirror = true;
		setRotation(Shape1, 0F, 0F, 0F);
		Shape2 = new ModelRenderer(this, 0, 16);
		Shape2.addBox(-6F, 7 - 16F, -6F, 12, 1, 12);
		Shape2.setRotationPoint(0F, 16F, 0F);
		Shape2.setTextureSize(32, 32);
		Shape2.mirror = true;
		setRotation(Shape2, 0F, 0F, 0F);
		Shape4A = new ModelRenderer(this, 0, 0);
		Shape4A.addBox(0F, 0 - 16F, 0F, 4, 1, 4);
		Shape4A.setRotationPoint(-2F, 18F, -2F);
		Shape4A.setTextureSize(32, 32);
		Shape4A.mirror = true;
		setRotation(Shape4A, 0F, 0F, 0F);
		Shape11A = new ModelRenderer(this, 0, 0);
		Shape11A.addBox(0F, 0 - 16F, 0F, 6, 1, 6);
		Shape11A.setRotationPoint(-3F, 20F, -3F);
		Shape11A.setTextureSize(32, 32);
		Shape11A.mirror = true;
		setRotation(Shape11A, 0F, 0F, 0F);
		Shape14A = new ModelRenderer(this, 0, 0);
		Shape14A.addBox(0F, 0 - 16F, 0F, 2, 2, 2);
		Shape14A.setRotationPoint(-1F, 15F, -1F);
		Shape14A.setTextureSize(32, 32);
		Shape14A.mirror = true;
		setRotation(Shape14A, 0F, 0F, 0F);
		Shape9A = new ModelRenderer(this, 0, 10);
		Shape9A.addBox(0F, 0 - 16F, 0F, 4, 1, 4);
		Shape9A.setRotationPoint(-2F, 13F, -2F);
		Shape9A.setTextureSize(32, 32);
		Shape9A.mirror = true;
		setRotation(Shape9A, 0F, 0F, 0F);
		Shape7A = new ModelRenderer(this, 0, 0);
		Shape7A.addBox(-1F, -8 - 16F, -1F, 2, 2, 2);
		Shape7A.setRotationPoint(0F, 16F, 0F);
		Shape7A.setTextureSize(32, 32);
		Shape7A.mirror = true;
		setRotation(Shape7A, 0F, 0F, 0F);
		Shape5 = new ModelRenderer(this, 0, 16);
		Shape5.addBox(-4F, -3 - 16F, -4F, 8, 1, 8);
		Shape5.setRotationPoint(0F, 14.06667F, 0F);
		Shape5.setTextureSize(32, 32);
		Shape5.mirror = true;
		setRotation(Shape5, 0F, 0F, 0F);
		Shape6 = new ModelRenderer(this, 0, 18);
		Shape6.addBox(-2F, -5 - 16F, -2F, 4, 1, 4);
		Shape6.setRotationPoint(0F, 15F, 0F);
		Shape6.setTextureSize(32, 32);
		Shape6.mirror = true;
		setRotation(Shape6, 0F, 0F, 0F);
		Shape3 = new ModelRenderer(this, 0, 17);
		Shape3.addBox(0F, 0 - 16F, 0F, 6, 1, 6);
		Shape3.setRotationPoint(-3F, 12F, -3F);
		Shape3.setTextureSize(32, 32);
		Shape3.mirror = true;
		setRotation(Shape3, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5);
		Shape1.render(f5);
		Shape2.render(f5);
		Shape4A.render(f5);
		Shape11A.render(f5);
		Shape14A.render(f5);
		Shape9A.render(f5);
		Shape7A.render(f5);
		Shape5.render(f5);
		Shape6.render(f5);
		Shape3.render(f5);
	}

	public void renderBottom()
	{
		float f5 = 0.0625F;
		Shape1.render(f5);
		Shape2.render(f5);
	}

	public void renderBottomLight(boolean b1, boolean b2, boolean b3)
	{
		float f5 = 0.0625F;
		if (b1)
			Shape11A.render(f5);
		if (b2)
			Shape4A.render(f5);
		if (b3)
			Shape14A.render(f5);
	}

	public void renderTop()
	{
		float f5 = 0.0625F;
		Shape5.render(f5);
		Shape6.render(f5);
		Shape3.render(f5);
	}

	public void renderTopLight(boolean b1, boolean b2)
	{
		float f5 = 0.0625F;
		if (b1)
			Shape9A.render(f5);
		if (b2)
			Shape7A.render(f5);
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