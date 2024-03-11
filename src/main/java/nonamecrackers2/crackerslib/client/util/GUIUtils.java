package nonamecrackers2.crackerslib.client.util;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class GUIUtils
{
	public static void openLink(String link)
	{
		Minecraft mc = Minecraft.getInstance();
		Screen current = mc.screen;
		mc.setScreen(new ConfirmLinkScreen(b -> {
			if (b)
				Util.getPlatform().openUri(link);
			mc.setScreen(current);
		}, link, true));
	}
	
	public static void renderOutline(PoseStack stack, int x, int y, int width, int height, int color)
	{
		GuiComponent.fill(stack, x, y, x + width, y + 1, color);
		GuiComponent.fill(stack, x, y + height - 1, x + width, y + height, color);
		GuiComponent.fill(stack, x, y + 1, x + 1, y + height - 1, color);
		GuiComponent.fill(stack, x + width - 1, y + 1, x + width, y + height - 1, color);
	}
	
	public static List<FormattedText> wrapTooltip(Component text, Font font, int width)
	{
		return font.getSplitter().splitLines(text, width, Style.EMPTY);
	}
}
