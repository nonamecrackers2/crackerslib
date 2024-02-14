package nonamecrackers2.crackerslib.common.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.common.config.preset.LegacyConfigPreset;
import nonamecrackers2.crackerslib.common.util.AttributeModifierSnapshot;

@Deprecated
public class ConfigHolder
{
	private static Multimap<String, ConfigHolder> configsByModid;
	private static Multimap<String, ForgeConfigSpec> specsByModId;
	
	private static final Logger LOGGER = LogManager.getLogger("crackerslib/ConfigHolder");
	private static final Splitter DOT_SPLITTER = Splitter.on(".");
	protected final Map<ForgeConfigSpec.ConfigValue<?>, ReloadType> values = Maps.newHashMap();
	protected final Map<ForgeConfigSpec.ConfigValue<?>, Class<?>> listValueClasses = Maps.newHashMap();
	protected final List<AttributeModifierSnapshot> attributeModifiers = Lists.newArrayList();
	protected final List<LegacyConfigPreset> presets = Lists.newArrayList();
	protected final List<ForgeConfigSpec.ConfigValue<?>> presetExcluded = Lists.newArrayList();
	protected final List<ForgeConfigSpec.ConfigValue<?>> guiHidden = Lists.newArrayList();
	private final String modid;
	public final LegacyConfigPreset custom;
	private final ModConfig.Type type;
	
	protected ConfigHolder(ModConfig.Type type, String modid)
	{
		this.modid = modid;
		this.custom = LegacyConfigPreset.Builder.empty().build(Component.translatable("config." + modid + ".preset.custom.title"), CrackersLib.id("custom")).withDescription(Component.translatable("config." + modid + ".preset.custom.description"));
		this.putPresets(this.custom);
		this.type = type;
	}
	
	public String getModId()
	{
		return this.modid;
	}
	
	public List<ForgeConfigSpec.ConfigValue<?>> getValues()
	{
		return ImmutableList.copyOf(this.values.keySet());
	}
	
	protected <T> ForgeConfigSpec.ConfigValue<T> addConfigValue(ForgeConfigSpec.ConfigValue<T> value, ReloadType type)
	{
		if (!this.values.containsKey(value))
			this.values.put(value, type);
		else
			throw new IllegalArgumentException("Duplicate config value: " + value);
		return value;
	}
	
	protected void putPresets(LegacyConfigPreset... presets)
	{
		for (int i = 0; i < presets.length; i++)
			this.presets.add(presets[i]);
	}
	
	public List<LegacyConfigPreset> getPresets()
	{
		return Lists.newArrayList(this.presets);
	}
	
	protected void addAttributeConfigValue(Supplier<Attribute> attribute, String id, String description, AttributeModifier.Operation operation, ForgeConfigSpec.ConfigValue<Double> value)
	{
		AttributeModifierSnapshot snapshot = new AttributeModifierSnapshot(attribute, UUID.fromString(id), description, value, operation);
		this.attributeModifiers.add(snapshot);
	}
	
	public ImmutableList<AttributeModifierSnapshot> getAttributeConfigs()
	{
		return ImmutableList.copyOf(this.attributeModifiers);
	}
	
	protected <T> ForgeConfigSpec.ConfigValue<T> createValue(ForgeConfigSpec.Builder builder, T value, String name, ReloadType type, String... description)
	{
		String desc = StringUtils.join(description, " ");
		return this.addConfigValue(builder.comment("DEFAULT=" + value + ". " + desc + ". " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").define(name, value), type);
	}
	
	protected ForgeConfigSpec.ConfigValue<Double> createRangedDoubleValue(ForgeConfigSpec.Builder builder, double value, double min, double max, String name, ReloadType type, String... description)
	{
		String desc = StringUtils.join(description, " ");
		return this.addConfigValue(builder.comment("DEFAULT=" + value + ". " + desc + ". " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").defineInRange(name, value, min, max), type);
	}
	
	protected ForgeConfigSpec.ConfigValue<Integer> createRangedIntValue(ForgeConfigSpec.Builder builder, int value, int min, int max, String name, ReloadType type, String... description)
	{
		String desc = StringUtils.join(description, " ");
		return this.addConfigValue(builder.comment("DEFAULT=" + value + ". " + desc + ". " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").defineInRange(name, value, min, max), type);
	}
	
	protected <T extends Enum<T>> ForgeConfigSpec.ConfigValue<T> createEnumValue(ForgeConfigSpec.Builder builder, T value, String name, ReloadType type, String... description)
	{
		String desc = StringUtils.join(description, " ");
		return this.addConfigValue(builder.comment("DEFAULT=" + value + ". " + desc + ". " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").defineEnum(name, value), type);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> ForgeConfigSpec.ConfigValue<List<? extends T>> createListValue(ForgeConfigSpec.Builder builder, Class<T> valueClass, Supplier<List<? extends T>> value, Predicate<T> validator, String name, ReloadType type, String description)
	{
		var config = this.addConfigValue(builder.comment(description + ". " + type.toString()).translation("gui." + this.modid + ".config." + name + ".description").defineListAllowEmpty(split(name), value, obj -> {
			return valueClass.isAssignableFrom(obj.getClass()) && validator.test((T)obj);
		}), type);
		this.listValueClasses.put(config, valueClass);
		return config;
	}
	
	protected ForgeConfigSpec.ConfigValue<Double> createAttributeValue(ForgeConfigSpec.Builder builder, Supplier<Attribute> attribute, String id, String modifierDesc, AttributeModifier.Operation operation, double value, String name, ReloadType type, String... description)
	{
		ForgeConfigSpec.ConfigValue<Double> configValue = this.createValue(builder, value, name, type, description);
		this.addAttributeConfigValue(attribute, id, modifierDesc, operation, configValue);
		return configValue;
	}
	
	protected void excludeFromPresets(ForgeConfigSpec.ConfigValue<?> value)
	{
		if (!this.presetExcluded.contains(value))
			this.presetExcluded.add(value);
	}
	
	public boolean shouldExclude(ForgeConfigSpec.ConfigValue<?> value)
	{
		return this.presetExcluded.contains(value);
	}
	
	protected void excludeFromGui(ForgeConfigSpec.ConfigValue<?> value)
	{
		if (!this.guiHidden.contains(value))
			this.guiHidden.add(value);
	}
	
	public boolean shouldHideFromGui(ForgeConfigSpec.ConfigValue<?> value)
	{
		return this.guiHidden.contains(value);
	}
	
	public ModConfig.Type getType()
	{
		return this.type;
	}
	
	public @Nullable ReloadType reloadTypeFor(ForgeConfigSpec.ConfigValue<?> value)
	{
		return this.values.get(value);
	}
	
	public <T> boolean isValid(ForgeConfigSpec.ConfigValue<T> config, T val)
	{
		return this.getValueSpec(config).test(val);
	}

	public @Nullable ForgeConfigSpec.ValueSpec getValueSpec(ForgeConfigSpec.ConfigValue<?> value)
	{
		Objects.requireNonNull(specsByModId, "Config wrapper system not initalized!");
		for (ForgeConfigSpec spec : specsByModId.get(this.modid))
		{
			if (spec.getRaw(value.getPath()) instanceof ValueSpec valueSpec)
				return valueSpec;
		}
		return null;
	}
	
	public @Nullable Class<?> getListClassForValue(ForgeConfigSpec.ConfigValue<List<?>> value)
	{
		return this.listValueClasses.get(value);
	}
	
	protected void makeDefaultPreset()
	{
		this.putPresets(
				LegacyConfigPreset.Builder.of(this)
				.build(Component.translatable("config.crackerslib.preset.default.title"), CrackersLib.id("default"))
				.withDescription(Component.translatable("config.crackerslib.preset.default.description"))
		);
	}
	
	public static String getName(ForgeConfigSpec.ConfigValue<?> value)
	{
		return StringUtils.join(value.getPath(), ".");
	}
	
//	public static ReloadType getReloadType(String modid, ForgeConfigSpec.ConfigValue<?> value)
//	{
//		for (var config : configsByModid.get(modid))
//		{
//			var type = config.reloadTypeFor(value);
//			if (type != null)
//				return type;
//		}
//		throw new NullPointerException("Value '" + value + "' is not registered in any config!");
//	}
	
//	public static Class<?> getValueClassOfListValue(String modid, ForgeConfigSpec.ConfigValue<List<?>> value)
//	{
//		for (var config : configsByModid.get(modid))
//		{
//			Class<?> clazz = config.getListClassForValue(value);
//			if (clazz != null)
//				return clazz;
//		}
//		throw new NullPointerException("Value '" + value + "' is not registered in any config!");
//	}
	
	public static Class<?> getValuesClass(ForgeConfigSpec.ConfigValue<?> value)
	{
		return value.getDefault().getClass();
	}
	
////	public static <T> boolean isValid(String modid, ForgeConfigSpec.ConfigValue<T> config, T val)
////	{
////		return getValueSpec(modid, config).test(val);
////	}
//	
////	public static @Nullable ForgeConfigSpec.ValueSpec getValueSpec(String modid, ForgeConfigSpec.ConfigValue<?> value)
////	{
////		Objects.requireNonNull(specsByModId, "Config wrapper system not initalized!");
////		for (ForgeConfigSpec spec : specsByModId.get(modid))
////		{
////			if (spec.getRaw(value.getPath()) instanceof ValueSpec valueSpec)
////				return valueSpec;
////		}
////		return null;
////	}
	
	public static <T> void setToDefault(ForgeConfigSpec.ConfigValue<T> value)
	{
		value.set(value.getDefault());
	}
	
    private static List<String> split(String path)
    {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }
    
    public static Collection<ConfigHolder> getConfigs(String modid)
    {
    	return configsByModid.get(modid);
    }
    
    public static Optional<ConfigHolder> getConfig(String modid, ModConfig.Type type)
    {
    	for (var config : getConfigs(modid))
    	{
    		if (config.getType().equals(type))
    			return Optional.of(config);
    	}
    	return Optional.empty();
    }
    
//    public static void initiateConfigHolders()
//    {
//    	if (configsByModid != null)
//    		throw new IllegalStateException("Configs have already been built!");
//    	IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
//		var event = new RegisterConfigHoldersEvent();
//		modBus.post(event);
//		configsByModid = event.getConfigs();
//		specsByModId = event.getSpecs();
//		LOGGER.info("Registered {} config wrapper(s)", configsByModid.size());
//    }
}
