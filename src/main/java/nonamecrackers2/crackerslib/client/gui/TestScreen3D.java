package nonamecrackers2.crackerslib.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.data.ModelData;

public class TestScreen3D extends Screen3D
{
	private float blockX;
	private float blockXO;
	private float blockZ;
	private float blockZO;
	private float angle;
	private boolean showStartUp = true;
	
	public TestScreen3D()
	{
		super(CommonComponents.EMPTY, 5.0F, 100.0F);
		this.renderOrigin(true);
	}
	
	@Override
	public void render(GuiGraphics stack, int pMouseX, int pMouseY, float pPartialTick)
	{
		if (this.showStartUp)
		{
			Popup.<Runnable>createOptionListPopup(this, list -> {
				list.addObject(Component.literal("Default Popup"), () -> Popup.createInfoPopup(this, 200, Component.literal("Hello there!")));
				list.addObject(Component.literal("Text Input Popup"), () -> Popup.createTextFieldPopup(this, val -> {}, 200, Component.literal("Hello there!"), val -> ResourceLocation.isValidPath(val)));
				list.addObject(Component.literal("Yes No Popup"), () -> Popup.createYesNoPopupWithCancel(this, () -> {}, () -> {}, 200, Component.literal("Hello there!")));
			}, val -> {
				val.run();
			}, 200, 50, Component.literal("Hello there! This is the test debug screen. Click an option to test."));
			this.showStartUp = false;
		}
		
		super.render(stack, pMouseX, pMouseY, pPartialTick);
	}
	
	@Override
	protected void render3D(PoseStack stack, MultiBufferSource buffers, int mouseX, int mouseY, float partialTick)
	{
		stack.pushPose();
		stack.translate(Mth.lerp(partialTick, this.blockXO, this.blockX) - 0.5D, 0.0D, Mth.lerp(partialTick, this.blockZO, this.blockZ) - 0.5D);
		this.minecraft.getBlockRenderer().renderSingleBlock(Blocks.GRASS_BLOCK.defaultBlockState(), stack, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, (RenderType)null);
		stack.popPose();
	}
	
	@Override
	public void tick()
	{
		this.blockXO = this.blockX;
		this.blockZO = this.blockZ;
		this.angle += (float)Math.PI / 16.0F;
		this.blockX = Mth.cos(this.angle);
		this.blockZ = Mth.sin(this.angle);
	}
}
