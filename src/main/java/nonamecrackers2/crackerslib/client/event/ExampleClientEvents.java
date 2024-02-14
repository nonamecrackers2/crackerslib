package nonamecrackers2.crackerslib.client.event;

import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;
import nonamecrackers2.crackerslib.common.test.ExampleConfig;

public class ExampleClientEvents
{
	public static void registerConfigScreen(RegisterConfigScreensEvent event)
	{
		event.builder(ConfigHomeScreen::new).addSpec(ModConfig.Type.CLIENT, ExampleConfig.CLIENT_SPEC).register();
	}
}
