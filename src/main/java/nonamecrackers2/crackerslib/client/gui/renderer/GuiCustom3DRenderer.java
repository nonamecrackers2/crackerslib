package nonamecrackers2.crackerslib.client.gui.renderer;

import org.joml.Vector3fc;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import nonamecrackers2.crackerslib.client.accessor.PictureInPictureRendererAccessor;
import nonamecrackers2.crackerslib.client.gui.renderer.state.GuiCustom3DState;
import nonamecrackers2.crackerslib.mixin.MixinPictureInPictureRenderer;

public class GuiCustom3DRenderer extends PictureInPictureRenderer<GuiCustom3DState>
{
	@Override
	public Class<GuiCustom3DState> getRenderStateClass()
	{
		return GuiCustom3DState.class;
	}

	@Override
	public void prepare(GuiCustom3DState renderState, GuiRenderState guiRenderState, FeatureRenderDispatcher featureRenderDispatcher, int guiScale)
	{
		((PictureInPictureRendererAccessor)this).setFarPlane(renderState.farPlane());
		
		super.prepare(renderState, guiRenderState, featureRenderDispatcher, guiScale);
	}
	
	@Override
	protected void renderToTexture(GuiCustom3DState renderState, PoseStack stack, SubmitNodeCollector submitNodeCollector)
	{
		Minecraft mc = Minecraft.getInstance();
		mc.gameRenderer.lighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
		Vector3fc trans = renderState.translation();
		stack.mulPose(renderState.rotation());
		stack.translate(trans.x(), trans.y(), trans.z());
		
		renderState.render().render(stack, submitNodeCollector, renderState.mouseX(), renderState.mouseY(), renderState.partialTick());
	}

	@Override
	protected float getTranslateY(int height, int guiScale)
	{
		return height / 2.0F;
	}
	
	@Override
	protected String getTextureLabel()
	{
		return "custom 3D";
	}
}
