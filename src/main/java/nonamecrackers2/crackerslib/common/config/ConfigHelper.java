package nonamecrackers2.crackerslib.common.config;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;

public abstract class ConfigHelper
{
	private static final Splitter DOT_SPLITTER = Splitter.on(".");
	protected final String modid;

	protected ConfigHelper(String modid)
	{
		this.modid = modid;
	}
	
	protected <T> ForgeConfigSpec.ConfigValue<T> createValue(ForgeConfigSpec.Builder builder, T value, String name, ReloadType type, String... description)
	{
		String desc = StringUtils.join(description, " ");
		if (type != ReloadType.NONE)
			builder.worldRestart();
		return builder.comment("DEFAULT=" + value + ". " + desc + ". Requires reload of: " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").define(name, value);
	}
	
	protected ForgeConfigSpec.ConfigValue<Double> createRangedDoubleValue(ForgeConfigSpec.Builder builder, double value, double min, double max, String name, ReloadType type, String... description)
	{
		String desc = StringUtils.join(description, " ");
		if (type != ReloadType.NONE)
			builder.worldRestart();
		return builder.comment("DEFAULT=" + value + ". " + desc + ". Requires reload of: " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").defineInRange(name, value, min, max);
	}
	
	protected ForgeConfigSpec.ConfigValue<Integer> createRangedIntValue(ForgeConfigSpec.Builder builder, int value, int min, int max, String name, ReloadType type, String... description)
	{
		String desc = StringUtils.join(description, " ");
		if (type != ReloadType.NONE)
			builder.worldRestart();
		return builder.comment("DEFAULT=" + value + ". " + desc + ". Requires reload of: " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").defineInRange(name, value, min, max);
	}
	
	protected <T extends Enum<T>> ForgeConfigSpec.ConfigValue<T> createEnumValue(ForgeConfigSpec.Builder builder, T value, String name, ReloadType type, String... description)
	{
		String desc = StringUtils.join(description, " ");
		if (type != ReloadType.NONE)
			builder.worldRestart();
		return builder.comment("DEFAULT=" + value + ". " + desc + ". Requires reload of: " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").defineEnum(name, value);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> ForgeConfigSpec.ConfigValue<List<? extends T>> createListValue(ForgeConfigSpec.Builder builder, Class<T> valueClass, Supplier<List<? extends T>> value, Predicate<T> validator, String name, ReloadType type, String description)
	{
		if (type != ReloadType.NONE)
			builder.worldRestart();
		return builder.comment(description + ". Requires reload of: " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").defineListAllowEmpty(split(name), value, obj -> {
			return valueClass.isAssignableFrom(obj.getClass()) && validator.test((T)obj);
		});
	}
	
	private static List<String> split(String path)
    {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }
}
