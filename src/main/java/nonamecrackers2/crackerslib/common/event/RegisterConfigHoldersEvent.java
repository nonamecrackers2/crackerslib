package nonamecrackers2.crackerslib.common.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import nonamecrackers2.crackerslib.common.config.ConfigHolder;

public class RegisterConfigHoldersEvent extends Event implements IModBusEvent
{
//	private static final Logger LOGGER = LogManager.getLogger();
	private final Multimap<String, ConfigHolder> configsByModid = Multimaps.newSetMultimap(Maps.newHashMap(), Sets::newHashSet);
	private final Multimap<String, ForgeConfigSpec> specs = Multimaps.newSetMultimap(Maps.newHashMap(), Sets::newHashSet);
	
	public <T extends ConfigHolder> void registerConfig(String modid, ForgeConfigSpec spec, ConfigHolder config)
	{
		this.configsByModid.put(modid, config);
		this.specs.put(modid, spec);
	}
	
	public Multimap<String, ConfigHolder> getConfigs()
	{
		return ImmutableMultimap.copyOf(this.configsByModid);
	}
	
	public Multimap<String, ForgeConfigSpec> getSpecs()
	{
		return ImmutableMultimap.copyOf(this.specs);
	}
}
