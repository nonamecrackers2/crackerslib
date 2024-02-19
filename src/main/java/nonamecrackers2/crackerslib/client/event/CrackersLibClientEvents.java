package nonamecrackers2.crackerslib.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
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

public class CrackersLibClientEvents
{
	public static void registerConfigScreen(RegisterConfigScreensEvent event)
	{
		event.builder(ConfigHomeScreen.builder(TextTitle.ofModDisplayName(CrackersLib.MODID))
				.crackersDefault().build()
		).addSpec(ModConfig.Type.CLIENT, CrackersLibConfig.CLIENT_SPEC).register();
	}
	
//	public static void registerConfigMenuButton(ConfigMenuButtonEvent event)
//	{
//		event.defaultButtonWithSingleCharacter('C', 0xFFF5D442);
//	}
	
	@SubscribeEvent
	public static void initGui(ScreenEvent.Init.Pre event)
	{
		if (event.getScreen() instanceof OptionsScreen screen)
		{
			Minecraft mc = Minecraft.getInstance();
			GridLayout layout = new GridLayout().rowSpacing(4);
			GridLayout.RowHelper rowHelper = layout.createRowHelper(1);
			ModList.get().forEachModInOrder(mod -> 
			{
				if (!CrackersLibConfig.CLIENT.hiddenConfigMenuButtons.get().contains(mod.getModId()))
				{
					ConfigScreenHandler.getScreenFactoryFor(mod.getModInfo()).ifPresent(factory -> 
					{
						var buttonFactory = ConfigMenuButtons.getButtonFactory(mod.getModId());
						if (buttonFactory != null)
						{
							var button = rowHelper.addChild(buttonFactory.makeButton(action -> {
								mc.setScreen(factory.apply(mc, screen));
							}));
							button.setWidth(20);
							button.setHeight(20);
							button.setTooltip(Tooltip.create(Component.literal(mod.getModInfo().getDisplayName())));
						}
					});
				}
			});
			layout.arrangeElements();
			FrameLayout.alignInRectangle(layout, screen.width / 2 - 180, screen.height / 6 + 42, 20, 200, 0.5F, 0.0F);
			layout.visitWidgets(event::addListener);
		}
	}
}
