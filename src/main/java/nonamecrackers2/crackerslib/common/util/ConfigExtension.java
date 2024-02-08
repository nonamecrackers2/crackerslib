package nonamecrackers2.crackerslib.common.util;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModContainer;
import nonamecrackers2.crackerslib.client.gui.ConfigScreen;

public class ConfigExtension
{
	public static void registerConfigExtension(ModContainer container)
	{
		container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> {
			return new ConfigScreen.Home(container.getModId(), mc.level != null, mc.hasSingleplayerServer());
		}));
	}
}
