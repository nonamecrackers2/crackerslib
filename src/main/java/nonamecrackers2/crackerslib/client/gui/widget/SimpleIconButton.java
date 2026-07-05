package nonamecrackers2.crackerslib.client.gui.widget;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SimpleIconButton extends AbstractButton
{
	private final Identifier icon;
	private final Runnable onPressed;
	
	public SimpleIconButton(Component name, Component description, Identifier icon, int x, int y, Runnable onPressed)
	{
		super(x, y, 20, 20, name);
		this.icon = icon;
		this.onPressed = onPressed;
		this.setTooltip(Tooltip.create(description));
	}
	
	@Override
	public void onPress(InputWithModifiers modifiers)
	{
		this.onPressed.run();
	}
	
	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
	{
		this.extractDefaultSprite(graphics);
		graphics.blit(RenderPipelines.GUI_TEXTURED, this.icon, this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), 256, 256);
	}
	
	@Override
	protected void extractDefaultLabel(ActiveTextCollector output) {}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
	{
		this.defaultButtonNarrationText(pNarrationElementOutput);
	}
}
