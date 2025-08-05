package nonamecrackers2.crackerslib.common.config;

import java.util.Collection;
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
import net.neoforged.neoforge.common.ModConfigSpec.RestartType;
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
	
	protected <T> ModConfigSpec.ConfigValue<T> createValue(T value, String name, RestartType restartType, String description)
	{
		return this.defaultProperties(name, description, restartType, value).define(name, value);
	}
	
	protected ModConfigSpec.ConfigValue<Double> createRangedDoubleValue(double value, double min, double max, String name, RestartType restartType, String description)
	{
		return this.defaultProperties(name, description, restartType, value).defineInRange(name, value, min, max);
	}
	
	protected ModConfigSpec.ConfigValue<Integer> createRangedIntValue(int value, int min, int max, String name, RestartType restartType, String description)
	{
		return this.defaultProperties(name, description, restartType, value).defineInRange(name, value, min, max);
	}
	
	protected ModConfigSpec.ConfigValue<Long> createRangedLongValue(long value, long min, long max, String name, RestartType restartType, String description)
	{
		return this.defaultProperties(name, description, restartType, value).defineInRange(name, value, min, max);
	}
	
	protected <T extends Enum<T>> ModConfigSpec.ConfigValue<T> createEnumValue(T value, String name, RestartType restartType, String description)
	{
		return this.defaultProperties(name, description, restartType, value).defineEnum(name, value);
	}
	
	protected <T extends Enum<T>> ModConfigSpec.ConfigValue<T> createEnumValue(T value, String name, RestartType restartType, String description, @SuppressWarnings("unchecked") T... valid)
	{
		return this.defaultProperties(name, description, restartType, value).defineEnum(name, value, valid);
	}
	
	protected <T extends Enum<T>> ModConfigSpec.ConfigValue<T> createEnumValue(T value, String name, RestartType restartType, String description, Collection<T> valid)
	{
		
		return this.defaultProperties(name, description, restartType, value).defineEnum(name, value, valid);
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Enum<T>> ModConfigSpec.ConfigValue<T> createEnumValue(T value, String name, RestartType restartType, String description, Predicate<T> validator)
	{
		return this.defaultProperties(name, description, restartType, value).defineEnum(name, value, obj -> {
			return value.getDeclaringClass().isAssignableFrom(obj.getClass()) && validator.test((T)obj);
		});
	}
	
	protected <T> ModConfigSpec.ConfigValue<List<? extends T>> createListValue(Class<T> valueClass, Supplier<List<? extends T>> value, Predicate<T> validator, String name, RestartType restartType, String description, T newValue)
	{
		return this.createListValueWithNewValueSupplier(valueClass, value, validator, name, restartType, description, () -> newValue);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> ModConfigSpec.ConfigValue<List<? extends T>> createListValueWithNewValueSupplier(Class<T> valueClass, Supplier<List<? extends T>> value, Predicate<T> validator, String name, RestartType restartType, String description, Supplier<T> newValue)
	{
		return this.defaultProperties(name, description, restartType, null).defineListAllowEmpty(split(name), value, newValue, obj -> {
			return valueClass.isAssignableFrom(obj.getClass()) && validator.test((T)obj);
		});
	}
	
	protected <T> ModConfigSpec.Builder defaultProperties(String name, String desc, RestartType restartType, @Nullable T defaultValue)
	{
		if (restartType != RestartType.NONE)
		{
			switch (restartType)
			{
			case WORLD:
			{
				this.builder.worldRestart();
				break;
			}
			case GAME:
			{
				this.builder.gameRestart();
				break;
			}
			default:
			}
			this.builder.comment(desc);
			this.builder.comment("Requires restart of " + restartType.toString());
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
	
	@SuppressWarnings("deprecation")
	public static Map<String, ModConfigSpec.ConfigValue<?>> getAllValues(ModConfigSpec spec)
	{
		return searchForValues("", spec.getValues().valueMap()).entrySet().stream().map(e -> {
			return Map.entry(e.getKey(), (ModConfigSpec.ConfigValue<?>)e.getValue());
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	@SuppressWarnings("deprecation")
	public static Map<String, ModConfigSpec.ValueSpec> getAllSpecs(ModConfigSpec spec)
	{
		return searchForValues("", spec.getSpec().valueMap()).entrySet().stream().map(e -> {
			return Map.entry(e.getKey(), (ValueSpec)e.getValue());
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	@SuppressWarnings("deprecation")
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
