package nonamecrackers2.crackerslib.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
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
		this.setTooltip(Tooltip.create(TOOLTIP));
	}
	
	@Override
	public void onPress()
	{
		this.onPressed.run();
	}
	
	@Override
	public void renderWidget(GuiGraphics stack, int mouseX, int mouseY, float partialTick)
	{
		super.renderWidget(stack, mouseX, mouseY, partialTick);
		stack.blit(ICON, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), 256, 256);
	}
	
	@Override
	public void renderString(GuiGraphics stack, Font pFont, int pColor) {}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
	{
		this.defaultButtonNarrationText(pNarrationElementOutput);
	}
}
