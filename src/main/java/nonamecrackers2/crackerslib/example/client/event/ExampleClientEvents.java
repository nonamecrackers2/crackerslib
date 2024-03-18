package nonamecrackers2.crackerslib.example.client.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.client.event.impl.AddConfigEntryToMenuEvent;
import nonamecrackers2.crackerslib.client.event.impl.ConfigMenuButtonEvent;
import nonamecrackers2.crackerslib.client.event.impl.OnConfigScreenOpened;
import nonamecrackers2.crackerslib.client.event.impl.RegisterConfigScreensEvent;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;
import nonamecrackers2.crackerslib.client.gui.title.TextTitle;
import nonamecrackers2.crackerslib.example.client.event.common.config.ExampleConfig;

public class ExampleClientEvents
{
	public static void registerConfigScreen(RegisterConfigScreensEvent event)
	{
		event.builder(ConfigHomeScreen.builder(TextTitle.ofModDisplayName(CrackersLib.MODID))
				.crackersDefault("https://github.com/nonamecrackers2/crackerslib/issues").build()
		).addSpec(ModConfig.Type.CLIENT, ExampleConfig.CLIENT_SPEC).register();
	}
	
	public static void registerConfigMenuButton(ConfigMenuButtonEvent event)
	{
		event.defaultButtonWithSingleCharacter('C', 0xFFF5D442);
	}
	
	@SubscribeEvent
	public static void onConfigScreenOpened(OnConfigScreenOpened event)
	{
		if (event.getModId().equals(CrackersLib.MODID) && event.getType() == ModConfig.Type.CLIENT)
			event.setInitialPath("list.category_example");
	}
	
	@SubscribeEvent
	public static void onConfigEntryAddedToMenu(AddConfigEntryToMenuEvent event)
	{
		if (event.getModId().equals(CrackersLib.MODID) && event.getType() == ModConfig.Type.CLIENT)
		{
			if (event.isValue(ExampleConfig.CLIENT.exampleString))
				event.setCanceled(true);
			else if (event.isValue(ExampleConfig.CLIENT.exampleDouble))
				event.setCanceled(true);
			else if (event.isValue(ExampleConfig.CLIENT.exampleEnum))
				event.setCanceled(true);
		}
	}
}
