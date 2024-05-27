package nonamecrackers2.crackerslib.common.config;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;

public abstract class ConfigHelper
{
	public static final Splitter DOT_SPLITTER = Splitter.on(".");
	public static final Joiner DOT_JOINER = Joiner.on('.');
	protected final ModConfigSpec.Builder builder;
	protected final String modid;

	protected ConfigHelper(ModConfigSpec.Builder builder, String modid)
	{
		this.builder = builder;
		this.modid = modid;
	}
	
	protected <T> ModConfigSpec.ConfigValue<T> createValue(T value, String name, boolean restart, String description)
	{
		return this.defaultProperties(name, description, restart, value).define(name, value);
	}
	
	protected ModConfigSpec.ConfigValue<Double> createRangedDoubleValue(double value, double min, double max, String name, boolean restart, String description)
	{
		return this.defaultProperties(name, description, restart, value).defineInRange(name, value, min, max);
	}
	
	protected ModConfigSpec.ConfigValue<Integer> createRangedIntValue(int value, int min, int max, String name, boolean restart, String description)
	{
		return this.defaultProperties(name, description, restart, value).defineInRange(name, value, min, max);
	}
	
	protected <T extends Enum<T>> ModConfigSpec.ConfigValue<T> createEnumValue(T value, String name, boolean restart, String description)
	{
		return this.defaultProperties(name, description, restart, value).defineEnum(name, value);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> ModConfigSpec.ConfigValue<List<? extends T>> createListValue(Class<T> valueClass, Supplier<List<? extends T>> value, Predicate<T> validator, String name, boolean restart, String description)
	{
		return this.defaultProperties(name, description, restart, null).defineListAllowEmpty(split(name), value, obj -> {
			return valueClass.isAssignableFrom(obj.getClass()) && validator.test((T)obj);
		});
	}
	
	protected <T> ModConfigSpec.Builder defaultProperties(String name, String desc, boolean restart, @Nullable T defaultValue)
	{
		if (restart)
		{
			this.builder.worldRestart().comment(desc);
			this.builder.comment("Requires restart.");
		}
		else
		{
			this.builder.comment(desc + ".");
		}
		if (defaultValue != null)
			this.builder.comment("Default: " + defaultValue.toString());
		this.builder.translation("gui." + this.modid + ".config." + name + ".description");
		return this.builder;
	}
	
	private static List<String> split(String path)
    {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }
	
	public static Map<String, ModConfigSpec.ConfigValue<?>> getAllValues(ModConfigSpec spec)
	{
		return searchForValues("", spec.getValues().valueMap()).entrySet().stream().map(e -> {
			return Map.entry(e.getKey(), (ModConfigSpec.ConfigValue<?>)e.getValue());
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	public static Map<String, ModConfigSpec.ValueSpec> getAllSpecs(ModConfigSpec spec)
	{
		return searchForValues("", spec.getSpec().valueMap()).entrySet().stream().map(e -> {
			return Map.entry(e.getKey(), (ValueSpec)e.getValue());
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	private static Map<String, Object> searchForValues(String previousPath, Map<String, Object> values)
	{
		Map<String, Object> map = Maps.newHashMap();
		for (var entry : values.entrySet())
		{
			String path = entry.getKey();
			if (!previousPath.isEmpty())
				path = previousPath + "." + path;
			if (entry.getValue() instanceof UnmodifiableConfig next)
				map.putAll(searchForValues(path, next.valueMap()));
			else
				map.put(path, entry.getValue());
		}
		return map;
	}
}
