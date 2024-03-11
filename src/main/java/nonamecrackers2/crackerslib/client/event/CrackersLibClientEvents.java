package nonamecrackers2.crackerslib.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.client.event.impl.ConfigMenuButtonEvent;
import nonamecrackers2.crackerslib.client.event.impl.RegisterConfigScreensEvent;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;
import nonamecrackers2.crackerslib.client.gui.ConfigMenuButtons;
import nonamecrackers2.crackerslib.client.gui.title.TextTitle;
import nonamecrackers2.crackerslib.common.config.CrackersLibConfig;
import nonamecrackers2.crackerslib.example.client.event.common.config.ExampleConfig;

public class CrackersLibClientEvents
{
	public static void registerConfigScreen(RegisterConfigScreensEvent event)
	{
		event.builder(ConfigHomeScreen.builder(TextTitle.ofModDisplayName(CrackersLib.MODID))
				.crackersDefault().build()
		).addSpec(ModConfig.Type.CLIENT, CrackersLibConfig.CLIENT_SPEC).register();
	}
	
	public static void registerConfigMenuButton(ConfigMenuButtonEvent event)
	{
		event.defaultButtonWithSingleCharacter('C', 0xFFF5D442);
	}
	
	@SubscribeEvent
	public static void initGui(ScreenEvent.Init.Post event)
	{
		if (event.getScreen() instanceof OptionsScreen screen)
		{
			Minecraft mc = Minecraft.getInstance();
			ModList.get().forEachModInOrder(mod -> 
			{
				int y = screen.height / 6 + 42;
				if (!CrackersLibConfig.CLIENT.hiddenConfigMenuButtons.get().contains(mod.getModId()))
				{
					var factory = ConfigScreenHandler.getScreenFactoryFor(mod.getModInfo()).orElse(null);
					if (factory != null)
					{
						var buttonFactory = ConfigMenuButtons.getButtonFactory(mod.getModId());
						if (buttonFactory != null)
						{
							var button = buttonFactory.makeButton(action -> {
								mc.setScreen(factory.apply(mc, screen));
							}, (b, p, tx, ty) -> {
								screen.renderTooltip(p, Component.literal(mod.getModInfo().getDisplayName()), tx, ty);
							});
							button.x = screen.width / 2 - 180;
							button.y = y;
							y += 24;
							button.setWidth(20);
							button.setHeight(20);
							event.addListener(button);
						}
					}
				}
			});
		}
	}
}
