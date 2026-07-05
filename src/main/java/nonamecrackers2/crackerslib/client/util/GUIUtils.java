package nonamecrackers2.crackerslib.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Util;

public class GUIUtils
{
	public static void openLink(String link)
	{
		Minecraft mc = Minecraft.getInstance();
		Screen current = mc.gui.screen();
		mc.gui.setScreen(new ConfirmLinkScreen(b -> {
			if (b)
				Util.getPlatform().openUri(link);
			mc.gui.setScreen(current);
		}, link, true));
	}
}
