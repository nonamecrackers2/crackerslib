package nonamecrackers2.crackerslib.client.gui.widget;

import java.util.function.Consumer;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.client.util.SortType;

public class SortButton extends AbstractButton
{
	private static final Identifier SORT_ICONS = CrackersLib.id("textures/gui/config/sort.png");
	private static final Component NAME = Component.translatable("gui.crackerslib.button.sorting.title");
	private final Consumer<SortType> onPressed;
	private SortType type = SortType.A_TO_Z;
	
	public SortButton(int x, int y, Consumer<SortType> onPressed)
	{
		super(x, y, 20, 20, NAME);
		this.onPressed = onPressed;
		this.setTooltip(this.buildTooltip());
	}
	
	@Override
	public void onPress(InputWithModifiers input)
	{
		int next = this.type.ordinal() + 1;
		if (next >= SortType.values().length)
			next = 0;
		this.type = SortType.values()[next];
		this.onPressed.accept(this.type);
		this.setTooltip(this.buildTooltip());
	}
	
	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
	{
		this.extractDefaultSprite(graphics);
		float texY = 0.0F;
		if (this.type == SortType.Z_TO_A)
			texY = 20.0F;
		graphics.blit(RenderPipelines.GUI_TEXTURED, SORT_ICONS, this.getX(), this.getY(), 0.0F, texY, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), 256, 256);
	}
	
	@Override
	protected void extractDefaultLabel(ActiveTextCollector output) {}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
	{
		this.defaultButtonNarrationText(pNarrationElementOutput);
	}
	
	public Tooltip buildTooltip()
	{
		Component text = NAME.copy().append(" ").append(this.type.getName());
		return Tooltip.create(text);
	}
}
