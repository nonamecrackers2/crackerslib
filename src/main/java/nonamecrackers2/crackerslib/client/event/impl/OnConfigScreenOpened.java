package nonamecrackers2.crackerslib.client.event.impl;

import javax.annotation.Nullable;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ModConfig;

@Cancelable
public class OnConfigScreenOpened extends Event
{
	private final String modid;
	private final ModConfig.Type type;
	private @Nullable String initialPath;
	
	public OnConfigScreenOpened(String modid, ModConfig.Type type)
	{
		this.modid = modid;
		this.type = type;
	}
	
	public String getModId()
	{
		return this.modid;
	}
	
	public ModConfig.Type getType()
	{
		return this.type;
	}
	
	public @Nullable String getInitialPath()
	{
		return this.initialPath;
	}
	
	public void setInitialPath(String path)
	{
		this.initialPath = path;
	}
}
