package nonamecrackers2.crackerslib.client.event;

import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;
import nonamecrackers2.crackerslib.common.test.ExampleConfig;

public class ExampleEvents
{
	public static void registerConfigScreen(RegisterConfigScreensEvent event)
	{
		event.builder(CrackersLib.MODID, ConfigHomeScreen::new).addSpec(ModConfig.Type.CLIENT, ExampleConfig.CLIENT_SPEC).register();
	}
}
