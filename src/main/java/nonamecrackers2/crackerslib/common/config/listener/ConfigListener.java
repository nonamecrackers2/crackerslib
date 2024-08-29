package nonamecrackers2.crackerslib.common.config.listener;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import nonamecrackers2.crackerslib.common.event.impl.OnConfigOptionSaved;

/**
 * Contains callbacks that run when a given config option is modified. A more universal method of detecting when config options are modified,
 * from either the config file directly, the config command, or config menu. Can be polled manually.
 */
public class ConfigListener
{
	private final ModConfig.Type type;
	private final String modid;
	private final List<ConfigListener.OptionListener<?>> values;
	
	private ConfigListener(ModConfig.Type type, String modid, List<ConfigListener.OptionListener<?>> values)
	{
		this.type = type;
		this.modid = modid;
		this.values = values;
	}

	private void onConfigEvent(ModConfigEvent event)
	{
		if (event instanceof ModConfigEvent.Loading)
			this.resetCache();
		else if (event instanceof ModConfigEvent.Unloading)
			this.clearCache();
		else if (event instanceof ModConfigEvent.Reloading && event.getConfig().getType() == this.type)
			this.poll();
	}
	
	private void onIndividualOptionChanged(OnConfigOptionSaved<?> event)
	{
		if (event.getModId().equals(this.modid) && event.getType() == this.type && event.didValueChange())
		{
			for (ConfigListener.OptionListener<?> listener : this.values)
			{
				if (listener.option.equals(event.getConfigOption()))
				{
					listener.callCallbackUnsafe(event.getConfigOption().get(), event.getNewValue());
					listener.updateCachedUnsafe(event.getNewValue());
				}
			}
		}
	}
	
	public void poll()
	{
		for (ConfigListener.OptionListener<?> value : this.values)
		{
			if (value.isChanged())
			{
				value.callCallback();
				value.updateCached();
			}
		}
	}
	
	public void resetCache()
	{
		for (ConfigListener.OptionListener<?> value : this.values)
			value.updateCached();
	}
	
	public void clearCache()
	{
		for (ConfigListener.OptionListener<?> value : this.values)
			value.cleartCache();
	}
	
	public static ConfigListener.Builder builder(ModConfig.Type type, String modid)
	{
		return new ConfigListener.Builder(type, modid);
	}
	
	public static class Builder
	{
		private final ModConfig.Type type;
		private final String modid;
		private final ImmutableList.Builder<ConfigListener.OptionListener<?>> values = ImmutableList.builder();
		
		private Builder(ModConfig.Type type, String modid)
		{
			this.type = type;
			this.modid = modid;
		}
		
		public <T> Builder addListener(ForgeConfigSpec.ConfigValue<T> value, BiConsumer<T, T> onChanged)
		{
			this.values.add(new ConfigListener.OptionListener<>(value, onChanged));
			return this;
		}
		
		public ConfigListener build()
		{
			return new ConfigListener(this.type, this.modid, this.values.build());
		}
		
		public ConfigListener buildAndRegister()
		{
			var listener = this.build();
			IEventBus modBus = ModList.get().getModContainerById(this.modid).map(container -> {
				if (container instanceof FMLModContainer fmlContainer)
					return fmlContainer.getEventBus();
				else
					throw new ClassCastException("Mod is not an FML mod!");
			}).orElseThrow(() -> new NullPointerException("Unknown mod with id '" + this.modid + "'"));
			modBus.addListener(listener::onConfigEvent);
			MinecraftForge.EVENT_BUS.addListener(listener::onIndividualOptionChanged);
			return listener;
		}
	}
	
	private static class OptionListener<T>
	{
		private final ForgeConfigSpec.ConfigValue<T> option;
		private final BiConsumer<T, T> onChanged;
		private @Nullable T cached;
		
		OptionListener(ForgeConfigSpec.ConfigValue<T> option, BiConsumer<T, T> onChanged)
		{
			this.option = option;
			this.onChanged = onChanged;
		}
		
		boolean isChanged()
		{
			if (this.cached == null)
				return false;
			else
				return !Objects.equals(this.cached, this.option.get());
		}
		
		void callCallback()
		{
			if (this.cached != null)
				this.onChanged.accept(this.cached, this.option.get());
		}
		
		@SuppressWarnings("unchecked")
		void callCallbackUnsafe(Object old, Object newValue)
		{
			this.onChanged.accept((T)old, (T)newValue);
		}
		
		void updateCached()
		{
			this.cached = this.option.get();
		}
		
		@SuppressWarnings("unchecked")
		void updateCachedUnsafe(Object value)
		{
			this.cached = (T)value;
		}
		
		void cleartCache()
		{
			this.cached = null;
		}
	}
}
