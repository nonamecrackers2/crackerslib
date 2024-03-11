package nonamecrackers2.crackerslib.client.util;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;

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
}
