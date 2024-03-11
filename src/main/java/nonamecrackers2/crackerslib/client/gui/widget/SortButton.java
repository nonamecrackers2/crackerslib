package nonamecrackers2.crackerslib.client.gui.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
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
	
	public SortButton(int x, int y, Consumer<SortType> onPressed)
	{
		super(x, y, 20, 20, NAME);
		this.onPressed = onPressed;
		this.setTooltip(this.buildTooltip());
	}
	
	@Override
	public void onPress()
	{
		int next = this.type.ordinal() + 1;
		if (next >= SortType.values().length)
			next = 0;
		this.type = SortType.values()[next];
		this.onPressed.accept(this.type);
		this.setTooltip(this.buildTooltip());
	}
	
	@Override
	public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTick)
	{
		super.renderWidget(stack, mouseX, mouseY, partialTick);
		float texY = 0.0F;
		if (this.type == SortType.Z_TO_A)
			texY = 20.0F;
		RenderSystem.setShaderTexture(0, SORT_ICONS);
		blit(stack, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0.0F, texY, this.getWidth(), this.getHeight(), 256, 256);
	}
	
	@Override
	public void renderString(PoseStack stack, Font pFont, int pColor) {}

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
