package nonamecrackers2.crackerslib.client.event.impl;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Fires when a config option is changed using either commands or the config menu
 */
public class OnConfigOptionChanged extends Event
{
	private final String modid;
	private final ModConfig.Type type;
	private final OnConfigOptionChanged.Source source;
	private final ForgeConfigSpec.ConfigValue<?> config;
	
	public OnConfigOptionChanged(String modid, ModConfig.Type type, OnConfigOptionChanged.Source source, ForgeConfigSpec.ConfigValue<?> config)
	{
		this.modid = modid;
		this.type = type;
		this.source = source;
		this.config = config;
	}
	
	public String getModId()
	{
		return this.modid;
	}
	
	public ModConfig.Type getType()
	{
		return this.type;
	}
	
	public OnConfigOptionChanged.Source getSource()
	{
		return this.source;
	}
	
	public ForgeConfigSpec.ConfigValue<?> getConfigOption()
	{
		return this.config;
	}
	
	public static enum Source
	{
		CONFIG_SCREEN,
		COMMAND;
	}
}

