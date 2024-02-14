package nonamecrackers2.crackerslib.common.config.preset;

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
import nonamecrackers2.crackerslib.common.event.RegisterConfigPresetsEvent;

public class ConfigPresets
{
	public static @Nullable Map<String, Multimap<ModConfig.Type, ConfigPreset>> presetsByType;
	private static final Logger LOGGER = LogManager.getLogger("crackerslib/ConfigPresets");
	
	public static @Nullable Multimap<ModConfig.Type, ConfigPreset> getPresetsForModId(String id)
	{
		Objects.requireNonNull(presetsByType, "Presets have not yet been gathered!");
		return presetsByType.get(id);
	}
	
	public static void gatherPresets()
	{
		if (presetsByType != null)
			throw new IllegalStateException("Presets have already been gathered!");
		ImmutableMap.Builder<String, Multimap<ModConfig.Type, ConfigPreset>> presetsBuilder = ImmutableMap.builder();
		List<RegisterConfigPresetsEvent> postedEvents = Lists.newArrayList();
		ModLoader.get().runEventGenerator(mod -> {
			var event = new RegisterConfigPresetsEvent(mod.getModId());
			postedEvents.add(event);
			return event;
		});
		postedEvents.forEach(e -> {
			var presets = e.getPresets();
			if (!presets.isEmpty())
				presetsBuilder.put(e.getModId(), presets);
		});
		presetsByType = presetsBuilder.build();
		LOGGER.debug("Gathered presets for {} mod(s)", presetsByType.size());
	}
}
