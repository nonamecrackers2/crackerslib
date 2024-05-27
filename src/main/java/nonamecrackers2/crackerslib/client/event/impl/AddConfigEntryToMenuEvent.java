package nonamecrackers2.crackerslib.client.event.impl;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;

public class AddConfigEntryToMenuEvent extends Event implements ICancellableEvent
{
	private final String modid;
	private final ModConfig.Type type;
	private final String path;
	
	public AddConfigEntryToMenuEvent(String modid, ModConfig.Type type, String path)
	{
		this.modid = modid;
		this.type = type;
		this.path = path;
	}
	
	public String getModId()
	{
		return this.modid;
	}
	
	public ModConfig.Type getType()
	{
		return this.type;
	}
	
	public String getValuePath()
	{
		return this.path;
	}
	
	public boolean isValue(ModConfigSpec.ConfigValue<?> value)
	{
		return this.path.equals(ConfigHelper.DOT_JOINER.join(value.getPath()));
	}
}
