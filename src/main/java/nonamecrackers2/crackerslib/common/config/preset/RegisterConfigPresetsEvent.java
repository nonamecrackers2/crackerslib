package nonamecrackers2.crackerslib.common.config.preset;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.IModBusEvent;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;

public class RegisterConfigPresetsEvent extends Event implements IModBusEvent
{
	private final Multimap<ModConfig.Type, ConfigPreset> presets = Multimaps.newSetMultimap(Maps.newEnumMap(ModConfig.Type.class), Sets::newHashSet);
	private final ImmutableList.Builder<String> excludedConfigOptions = ImmutableList.builder();
	private final String modid;
	
	public RegisterConfigPresetsEvent(String modid)
	{
		this.modid = modid;
	}
	
	public void registerPreset(ModConfig.Type type, ConfigPreset preset)
	{
		this.presets.put(type, preset);
	}
	
	protected Multimap<ModConfig.Type, ConfigPreset> buildPresets()
	{
		return ImmutableMultimap.copyOf(this.presets);
	}
	
	protected List<String> buildExcludedConfigOptions()
	{
		return this.excludedConfigOptions.build();
	}
	
	public RegisterConfigPresetsEvent exclude(String path)
	{
		this.excludedConfigOptions.add(path);
		return this;
	}
	
	public RegisterConfigPresetsEvent exclude(ForgeConfigSpec.ConfigValue<?> value)
	{
		return this.exclude(ConfigHelper.DOT_JOINER.join(value.getPath()));
	}
	
	public String getModId()
	{
		return this.modid;
	}
}
