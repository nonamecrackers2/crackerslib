package nonamecrackers2.crackerslib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import nonamecrackers2.crackerslib.CrackersLib;

public class CollapseButton extends AbstractButton
{
	private static final ResourceLocation ICON = CrackersLib.id("textures/gui/config/collapse.png");
	private static final Component NAME = Component.translatable("gui.crackerslib.button.collapse.title");
	private static final Component TOOLTIP = Component.translatable("gui.crackerslib.button.collapse.description");
	private final Runnable onPressed;
	
	public CollapseButton(int x, int y, Runnable onPressed)
	{
		super(x, y, 20, 20, NAME);
		this.onPressed = onPressed;
	}
	
	@Override
	public void onPress()
	{
		this.onPressed.run();
	}
	
	@Override
	public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick)
	{
		Minecraft minecraft = Minecraft.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		int i = this.getYImage(this.isHoveredOrFocused());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.blit(stack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
		this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		this.renderBg(stack, minecraft, mouseX, mouseY);
		RenderSystem.setShaderTexture(0, ICON);
		blit(stack, this.x, this.y, this.getWidth(), this.getHeight(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), 256, 256);
		if (this.isHovered)
			minecraft.screen.renderTooltip(stack, TOOLTIP, mouseX, mouseY);
	}
	
	@Override
	public void updateNarration(NarrationElementOutput pNarrationElementOutput)
	{
		this.defaultButtonNarrationText(pNarrationElementOutput);
	}
}
