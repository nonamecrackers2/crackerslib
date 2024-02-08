package nonamecrackers2.crackerslib.client.gui.widget.config;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;

public interface ConfigListItem extends Comparable<ConfigListItem>
{
	void init(List<AbstractWidget> widgets, int x, int y, int width, int height);
	
	void render(GuiGraphics stack, int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks);
	
	void onSavedAndClosed();
	
	void resetValue();
	
	boolean isValueReset();
	
	boolean matchesPreset(ConfigPreset preset);
	
	void setFromPreset(ConfigPreset preset);
	
	@Nullable Tooltip getTooltip(ConfigPreset preset);
	
	boolean matchesSearch(String text);
}
