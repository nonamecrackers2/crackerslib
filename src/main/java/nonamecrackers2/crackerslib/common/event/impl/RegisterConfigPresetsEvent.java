package nonamecrackers2.crackerslib.common.event.impl;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.IModBusEvent;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;

public class RegisterConfigPresetsEvent extends Event implements IModBusEvent
{
	private final Multimap<ModConfig.Type, ConfigPreset> presets = Multimaps.newSetMultimap(Maps.newEnumMap(ModConfig.Type.class), Sets::newHashSet);
	private final String modid;
	
	public RegisterConfigPresetsEvent(String modid)
	{
		this.modid = modid;
	}
	
	public void register(ModConfig.Type type, ConfigPreset preset)
	{
		this.presets.put(type, preset);
	}
	
	public Multimap<ModConfig.Type, ConfigPreset> getPresets()
	{
		return ImmutableMultimap.copyOf(this.presets);
	}
	
	public String getModId()
	{
		return this.modid;
	}
}
