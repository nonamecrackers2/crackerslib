package nonamecrackers2.crackerslib.common.event.impl;

import javax.annotation.Nullable;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Fires when a config option is saved/modified using either commands or the config menu
 */
public class OnConfigOptionSaved<T> extends Event
{
	private final String modid;
	private final ModConfig.Type type;
	private final OnConfigOptionSaved.Source source;
	private final ForgeConfigSpec.ConfigValue<T> config;
	private final T newValue;
	private final boolean didValueChange;
	private @Nullable T override;
	
	public OnConfigOptionSaved(String modid, ModConfig.Type type, OnConfigOptionSaved.Source source, ForgeConfigSpec.ConfigValue<T> config, T newValue, boolean didValueChange)
	{
		this.modid = modid;
		this.type = type;
		this.source = source;
		this.config = config;
		this.newValue = newValue;
		this.didValueChange = didValueChange;
	}
	
	public String getModId()
	{
		return this.modid;
	}
	
	public ModConfig.Type getType()
	{
		return this.type;
	}
	
	public OnConfigOptionSaved.Source getSource()
	{
		return this.source;
	}
	
	public T getNewValue()
	{
		return this.newValue;
	}
	
	public ForgeConfigSpec.ConfigValue<T> getConfigOption()
	{
		return this.config;
	}
	
	public void overrideValue(T value)
	{
		this.override = value;
	}
	
	public T getOverrideValue()
	{
		return this.override;
	}
	
	public boolean didValueChange()
	{
		return this.didValueChange;
	}
	
	public static enum Source
	{
		CONFIG_SCREEN,
		COMMAND;
	}
}

