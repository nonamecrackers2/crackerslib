package nonamecrackers2.crackerslib.client.gui.threed;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import nonamecrackers2.crackerslib.client.gui.Popup;

public class TestScreen3D extends Screen3D
{
	private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
	private float blockX;
	private float blockXO;
	private float blockZ;
	private float blockZO;
	private float angle;
	private boolean showStartUp = true;
	
	public TestScreen3D()
	{
		super(CommonComponents.EMPTY, 5.0F, 1000.0F);
		this.renderOrigin(true);
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		if (this.showStartUp)
		{
			Popup.<Runnable>createOptionListPopup(this, list -> {
				list.addObject(Component.literal("Default Popup"), () -> Popup.createInfoPopup(this, 200, Component.literal("Hello there!")));
				list.addObject(Component.literal("Text Input Popup"), () -> Popup.createTextFieldPopup(this, val -> {}, 200, Component.literal("Hello there!"), val -> Identifier.isValidPath(val)));
				list.addObject(Component.literal("Yes No Popup"), () -> Popup.createYesNoPopupWithCancel(this, () -> {}, () -> {}, 200, Component.literal("Hello there!")));
			}, val -> {
				val.run();
			}, 200, 50, Component.literal("Hello there! This is the test debug screen. Click an option to test."));
			this.showStartUp = false;
		}
		
		super.extractRenderState(graphics, pMouseX, pMouseY, pPartialTick);
	}
	
	@Override
	protected void render3D(PoseStack stack, SubmitNodeCollector collector, int mouseX, int mouseY, float partialTick)
	{
		stack.pushPose();
		stack.translate(Mth.lerp(partialTick, this.blockXO, this.blockX) - 0.5D, 0.0D, Mth.lerp(partialTick, this.blockZO, this.blockZ) - 0.5D);
		BlockModelRenderState blockState = new BlockModelRenderState();
		this.minecraft.getBlockModelResolver().update(blockState, Blocks.GRASS_BLOCK.defaultBlockState(), BLOCK_DISPLAY_CONTEXT);
		blockState.submit(stack, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
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
