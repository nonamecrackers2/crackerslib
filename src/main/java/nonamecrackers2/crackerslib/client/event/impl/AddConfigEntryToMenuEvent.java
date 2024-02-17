package nonamecrackers2.crackerslib.client.event.impl;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;

@Cancelable
public class AddConfigEntryToMenuEvent extends Event
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
	
	public boolean isValue(ForgeConfigSpec.ConfigValue<?> value)
	{
		return this.path.equals(ConfigHelper.DOT_JOINER.join(value.getPath()));
	}
}
