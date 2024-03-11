package nonamecrackers2.crackerslib.client.gui.widget.config;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;

public interface ConfigListItem extends Comparable<ConfigListItem>
{
	void init(List<AbstractWidget> widgets, int x, int y, int width, int height);
	
	void render(PoseStack stack, int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks);
	
	void onSavedAndClosed();
	
	void resetValue();
	
	boolean isValueReset();
	
	boolean matchesPreset(ConfigPreset preset);
	
	void setFromPreset(ConfigPreset preset);
	
	@Nullable List<Component> getTooltip(@Nullable ConfigPreset preset);
	
	boolean matchesSearch(String text);
	
	public static Component shortenText(Component name, int allowedWidth)
	{
		Minecraft mc = Minecraft.getInstance();
		String text = name.getString();
		int currentSize = 0;
		int lastIndex = -1;
		for (int i = 0; i < text.length(); i++)
		{
			currentSize += mc.font.width(FormattedText.of(String.valueOf(text.charAt(i)), name.getStyle()));
			lastIndex = i;
			if (currentSize > allowedWidth)
				break;
		}
		if (lastIndex > 0)
		{
			String newText = text.substring(0, lastIndex + 1);
			if (currentSize > allowedWidth)
				newText += "...";
			return Component.literal(newText).withStyle(name.getStyle());
		}
		else
		{
			return CommonComponents.EMPTY;
		}
	}
	
	public static String extractNameFromPath(String path)
	{
		String name = path;
		int index = path.lastIndexOf('.');
		if (index > 0 && index + 1 < name.length())
			name = path.substring(index + 1);
		return name;
	}
}
