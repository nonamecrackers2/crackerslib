package nonamecrackers2.crackerslib.client.event;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.IModBusEvent;
import nonamecrackers2.crackerslib.client.config.ConfigHomeScreenFactory;

public class RegisterConfigScreensEvent extends Event implements IModBusEvent
{
	public RegisterConfigScreensEvent.Builder builder(String modid, ConfigHomeScreenFactory factory)
	{
		return new RegisterConfigScreensEvent.Builder(modid, factory);
	}
	
	public class Builder
	{
		private final Map<ModConfig.Type, ForgeConfigSpec> specsByType = Maps.newEnumMap(ModConfig.Type.class);
		private final String modid;
		private final ConfigHomeScreenFactory factory;
		
		private Builder(String modid, ConfigHomeScreenFactory factory)
		{
			this.modid = modid;
			this.factory = factory;
		}
		
		public Builder addSpec(ModConfig.Type type, ForgeConfigSpec spec)
		{
			if (this.specsByType.containsKey(type))
				throw new IllegalArgumentException("Type is already registered");
			this.specsByType.put(type, spec);
			return this;
		}
		
		public void register()
		{
			ModList.get().getModContainerById(this.modid).ifPresentOrElse(mod -> {
				ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> {
					return new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> {
						return this.factory.build(this.modid, this.specsByType, mc.level != null, mc.hasSingleplayerServer(), screen);
					});
				});
			}, () -> {
				throw new IllegalArgumentException("Unknown mod with id '" + this.modid + "'");
			});
		}
	}
}
