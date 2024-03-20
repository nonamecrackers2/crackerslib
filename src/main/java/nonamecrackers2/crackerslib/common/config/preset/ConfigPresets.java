package nonamecrackers2.crackerslib.common.config.preset;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigPresets
{
	public static @Nullable Map<String, ConfigPresets.Presets> presetsByMod;
	private static final Logger LOGGER = LogManager.getLogger("crackerslib/ConfigPresets");
	
	public static @Nullable ConfigPresets.Presets getPresetsForModId(String id)
	{
		Objects.requireNonNull(presetsByMod, "Presets have not yet been gathered!");
		return presetsByMod.get(id);
	}
	
	public static void gatherPresets()
	{
		if (presetsByMod != null)
			throw new IllegalStateException("Presets have already been gathered!");
		ImmutableMap.Builder<String, ConfigPresets.Presets> presetsBuilder = ImmutableMap.builder();
		List<RegisterConfigPresetsEvent> postedEvents = Lists.newArrayList();
		ModLoader.get().runEventGenerator(mod -> {
			var event = new RegisterConfigPresetsEvent(mod.getModId());
			postedEvents.add(event);
			return event;
		});
		postedEvents.forEach(e -> {
			var presets = e.buildPresets();
			var excluded = e.buildExcludedConfigOptions();
			if (!presets.isEmpty())
				presetsBuilder.put(e.getModId(), new ConfigPresets.Presets(presets, excluded));
		});
		presetsByMod = presetsBuilder.build();
		LOGGER.debug("Gathered presets for {} mod(s)", presetsByMod.size());
	}
	
	public static class Presets
	{
		private final Multimap<ModConfig.Type, ConfigPreset> presetsByType;
		private final List<String> excludedConfigOptions;
		
		Presets(Multimap<ModConfig.Type, ConfigPreset> presetsByType, List<String> excludedConfigOptions)
		{
			this.presetsByType = presetsByType;
			this.excludedConfigOptions = excludedConfigOptions;
		}
		
		public Collection<ConfigPreset> getPresetsForType(ModConfig.Type type)
		{
			return this.presetsByType.get(type);
		}
		
		public List<String> getExcludedConfigOptions()
		{
			return this.excludedConfigOptions;
		}
	}
}
