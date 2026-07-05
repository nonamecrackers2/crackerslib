package nonamecrackers2.crackerslib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import nonamecrackers2.crackerslib.client.accessor.PictureInPictureRendererAccessor;

@Mixin(PictureInPictureRenderer.class)
public class MixinPictureInPictureRenderer implements PictureInPictureRendererAccessor
{
	@Unique
	private float farplane = 1000.0F;
	
	@Override
	public void setFarPlane(float dist)
	{
		this.farplane = dist;
	}

	@Override
	public float getFarPlaneDist()
	{
		return this.farplane;
	}

	@ModifyConstant(method = "prepareTexturesAndProjection", constant = @Constant(floatValue = 1000.0F))
	private float crackerslib$adjustableOrthoFarPlane_prepareTexturesAndProjection(float old)
	{
		return this.farplane;
	}
	
	@ModifyConstant(method = "prepareTexturesAndProjection", constant = @Constant(floatValue = -1000.0F))
	private float crackerslib$adjustableOrthoNearPlane_prepareTexturesAndProjection(float old)
	{
		return -this.farplane;
	}
}
