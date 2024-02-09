package nonamecrackers2.crackerslib.client.event;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;

public class RegisterConfigScreensEvent extends Event implements IModBusEvent
{
	public void registerConfigScreen(String modid, RegisterConfigScreensEvent.ConfigHomeScreenFactory factory)
	{
		ModList.get().getModContainerById(modid).ifPresentOrElse(mod -> {
			ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> {
				return new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> {
					return factory.build(modid, mc.level != null, mc.hasSingleplayerServer(), screen);
				});
			});
		}, () -> {
			throw new IllegalArgumentException("Unknown mod with id '" + modid + "'");
		});;
	}
	
	@FunctionalInterface
	public static interface ConfigHomeScreenFactory
	{
		public ConfigHomeScreen build(String modid, boolean isWorldLoaded, boolean hasSinglePlayerServer, @Nullable Screen previous);
	}
}
