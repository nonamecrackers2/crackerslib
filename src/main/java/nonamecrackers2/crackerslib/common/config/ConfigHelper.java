package nonamecrackers2.crackerslib.common.config;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.common.ForgeConfigSpec;

public abstract class ConfigHelper
{
	public static final Splitter DOT_SPLITTER = Splitter.on(".");
	public static final Joiner DOT_JOINER = Joiner.on('.');
	protected final String modid;

	protected ConfigHelper(String modid)
	{
		this.modid = modid;
	}
	
	@Deprecated
	@ScheduledForRemoval
	protected <T> ForgeConfigSpec.ConfigValue<T> createValue(ForgeConfigSpec.Builder builder, T value, String name, ReloadType type, String... description)
	{
		return this.createValue(builder, value, name, type != ReloadType.NONE, StringUtils.join(description, " "));
	}
	
	protected <T> ForgeConfigSpec.ConfigValue<T> createValue(ForgeConfigSpec.Builder builder, T value, String name, boolean restart, String description)
	{
		return this.defaultProperties(builder, name, description, restart, value).define(name, value);
	}
	
	@Deprecated
	@ScheduledForRemoval
	protected ForgeConfigSpec.ConfigValue<Double> createRangedDoubleValue(ForgeConfigSpec.Builder builder, double value, double min, double max, String name, ReloadType type, String... description)
	{
		return this.createRangedDoubleValue(builder, value, min, max, name, type != ReloadType.NONE, StringUtils.join(description, " "));
	}
	
	protected ForgeConfigSpec.ConfigValue<Double> createRangedDoubleValue(ForgeConfigSpec.Builder builder, double value, double min, double max, String name, boolean restart, String description)
	{
		return this.defaultProperties(builder, name, description, restart, value).defineInRange(name, value, min, max);
	}
	
	@Deprecated
	@ScheduledForRemoval
	protected ForgeConfigSpec.ConfigValue<Integer> createRangedIntValue(ForgeConfigSpec.Builder builder, int value, int min, int max, String name, ReloadType type, String... description)
	{
		return this.createRangedIntValue(builder, value, min, max, name, type != ReloadType.NONE, StringUtils.join(description, " "));
	}
	
	protected ForgeConfigSpec.ConfigValue<Integer> createRangedIntValue(ForgeConfigSpec.Builder builder, int value, int min, int max, String name, boolean restart, String description)
	{
		return this.defaultProperties(builder, name, description, restart, value).defineInRange(name, value, min, max);
	}
	
	@Deprecated
	@ScheduledForRemoval
	protected <T extends Enum<T>> ForgeConfigSpec.ConfigValue<T> createEnumValue(ForgeConfigSpec.Builder builder, T value, String name, ReloadType type, String... description)
	{
		return this.createEnumValue(builder, value, name, type != ReloadType.NONE, StringUtils.join(description, " "));
	}
	
	protected <T extends Enum<T>> ForgeConfigSpec.ConfigValue<T> createEnumValue(ForgeConfigSpec.Builder builder, T value, String name, boolean restart, String description)
	{
		return this.defaultProperties(builder, name, description, restart, value).defineEnum(name, value);
	}
	
	@Deprecated
	@ScheduledForRemoval
	protected <T> ForgeConfigSpec.ConfigValue<List<? extends T>> createListValue(ForgeConfigSpec.Builder builder, Class<T> valueClass, Supplier<List<? extends T>> value, Predicate<T> validator, String name, ReloadType type, String description)
	{
		return this.createListValue(builder, valueClass, value, validator, name, type != ReloadType.NONE, description);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> ForgeConfigSpec.ConfigValue<List<? extends T>> createListValue(ForgeConfigSpec.Builder builder, Class<T> valueClass, Supplier<List<? extends T>> value, Predicate<T> validator, String name, boolean restart, String description)
	{
		return this.defaultProperties(builder, name, description, restart, null).defineListAllowEmpty(split(name), value, obj -> {
			return valueClass.isAssignableFrom(obj.getClass()) && validator.test((T)obj);
		});
	}
	
	protected <T> ForgeConfigSpec.Builder defaultProperties(ForgeConfigSpec.Builder builder, String name, String desc, boolean restart, @Nullable T defaultValue)
	{
		if (restart)
			builder.worldRestart().comment(desc + ". Requires restart.");
		else
			builder.comment(desc + ".");
		if (defaultValue != null)
			builder.comment("Default: " + defaultValue.toString());
		builder.translation("gui." + this.modid + ".config." + name + ".description");
		return builder;
	}
	
	private static List<String> split(String path)
    {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }
	
	public static Map<String, ForgeConfigSpec.ConfigValue<?>> getAllValues(ForgeConfigSpec spec)
	{
		return searchForValues("", spec.getValues().valueMap()).entrySet().stream().map(e -> {
			return Map.entry(e.getKey(), (ForgeConfigSpec.ConfigValue<?>)e.getValue());
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
