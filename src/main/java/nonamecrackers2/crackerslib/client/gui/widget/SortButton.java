package nonamecrackers2.crackerslib.client.gui.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.client.util.SortType;

public class SortButton extends AbstractButton
{
	private static final ResourceLocation SORT_ICONS = CrackersLib.id("textures/gui/config/sort.png");
	private static final Component NAME = Component.translatable("gui.crackerslib.button.sorting.title");
	private final Consumer<SortType> onPressed;
	private SortType type = SortType.A_TO_Z;
	private Component tooltip;
	
	public SortButton(int x, int y, Consumer<SortType> onPressed)
	{
		super(x, y, 20, 20, NAME);
		this.onPressed = onPressed;
		this.tooltip = this.buildTooltip();
	}
	
	@Override
	public void onPress()
	{
		int next = this.type.ordinal() + 1;
		if (next >= SortType.values().length)
			next = 0;
		this.type = SortType.values()[next];
		this.onPressed.accept(this.type);
		this.tooltip = this.buildTooltip();
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
		float texY = 0.0F;
		if (this.type == SortType.Z_TO_A)
			texY = 20.0F;
		RenderSystem.setShaderTexture(0, SORT_ICONS);
		blit(stack, this.x, this.y, this.getWidth(), this.getHeight(), 0.0F, texY, this.getWidth(), this.getHeight(), 256, 256);
		if (this.isHovered)
			minecraft.screen.renderTooltip(stack, this.tooltip, mouseX, mouseY);
	}
	
	@Override
	public void updateNarration(NarrationElementOutput pNarrationElementOutput)
	{
		this.defaultButtonNarrationText(pNarrationElementOutput);
	}
	
	public Component buildTooltip()
	{
		return NAME.copy().append(" ").append(this.type.getName());
	}
}
