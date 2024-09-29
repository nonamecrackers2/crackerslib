package nonamecrackers2.crackerslib.client.event.impl;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import nonamecrackers2.crackerslib.client.config.ConfigHomeScreenFactory;

public class RegisterConfigScreensEvent extends Event implements IModBusEvent
{
	private final String modid;
	
	public RegisterConfigScreensEvent(String modid)
	{
		this.modid = modid;
	}
	
	public RegisterConfigScreensEvent.Builder builder(ConfigHomeScreenFactory factory)
	{
		return new RegisterConfigScreensEvent.Builder(this.modid, factory);
	}
	
	public class Builder
	{
		private final Map<ModConfig.Type, ModConfigSpec> specsByType = Maps.newEnumMap(ModConfig.Type.class);
		private final String modid;
		private final ConfigHomeScreenFactory factory;
		
		private Builder(String modid, ConfigHomeScreenFactory factory)
		{
			this.modid = modid;
			this.factory = factory;
		}
		
		public Builder addSpec(ModConfig.Type type, ModConfigSpec spec)
		{
			if (this.specsByType.containsKey(type))
				throw new IllegalArgumentException("Type is already registered");
			this.specsByType.put(type, spec);
			return this;
		}
		
		public void register()
		{
			//I hope you like lambdas
			ModList.get().getModContainerById(this.modid).ifPresentOrElse(mod -> 
			{
				mod.registerExtensionPoint(IConfigScreenFactory.class, (container, screen) -> 
				{
					Minecraft mc = Minecraft.getInstance();
					return this.factory.build(this.modid, this.specsByType, mc.level != null, mc.hasSingleplayerServer(), screen);
				});
			}, () -> {
				throw new IllegalArgumentException("Unknown mod with id '" + this.modid + "'");
			});
		}
	}
}
