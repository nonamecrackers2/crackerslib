package nonamecrackers2.crackerslib.client.gui;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.fml.ModLoader;
import nonamecrackers2.crackerslib.client.event.impl.ConfigMenuButtonEvent;

public class ConfigMenuButtons
{
	private static @Nullable Map<String, ConfigMenuButtons.Factory> factoriesByModId;
	private static final Logger LOGGER = LogManager.getLogger("crackerslib/ConfigPresets");
	
	public static @Nullable ConfigMenuButtons.Factory getButtonFactory(String modid)
	{
		Objects.requireNonNull(factoriesByModId, "Button factories have not been registered yet!");
		return factoriesByModId.get(modid);
	}
	
	public static void gatherButtonFactories()
	{
		if (factoriesByModId != null)
			throw new IllegalStateException("Config menu button factories have already been gathered!");
		ImmutableMap.Builder<String, ConfigMenuButtons.Factory> factories = ImmutableMap.builder();
		List<ConfigMenuButtonEvent> postedEvents = Lists.newArrayList();
		ModLoader.get().runEventGenerator(mod -> {
			var event = new ConfigMenuButtonEvent(mod.getModId());
			postedEvents.add(event);
			return event;
		});
		postedEvents.forEach(e -> {
			if (e.getFactory() != null)
				factories.put(e.getModId(), e.getFactory());
		});
		factoriesByModId = factories.build();
		LOGGER.debug("Registered {} config menu buttons", factoriesByModId.size());
	}
	
	@FunctionalInterface
	public static interface Factory
	{
		AbstractButton makeButton(Button.OnPress onButtonPress);
	}
}
