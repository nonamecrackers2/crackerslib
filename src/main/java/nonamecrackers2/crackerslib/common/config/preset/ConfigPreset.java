package nonamecrackers2.crackerslib.common.config.preset;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.common.config.ConfigHolder;
import nonamecrackers2.crackerslib.common.config.ReloadType;

public class ConfigPreset
{
	private final ConfigHolder config;
	private final Map<ForgeConfigSpec.ConfigValue<?>, Object> values;
	private final MutableComponent translatedName;
	private @Nullable MutableComponent description;
	private final ResourceLocation name;
	private final Optional<ConfigPreset> parent;
	private final boolean isDefault;
	
	private ConfigPreset(ConfigHolder config, Map<ForgeConfigSpec.ConfigValue<?>, Object> map, MutableComponent translated, ResourceLocation location, boolean isDefault, Optional<ConfigPreset> parent)
	{
		this.config = config;
		this.values = map;
		this.translatedName = translated;
		this.name = location;
		this.isDefault = isDefault;
		this.parent = parent;
	}
	
	public ConfigPreset withDescription(MutableComponent components)
	{
		this.description = components;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getPresetValue(ForgeConfigSpec.ConfigValue<T> config)
	{
		return (T)this.values.get(config);
	}
	
	public Map<ForgeConfigSpec.ConfigValue<?>, ?> getValues()
	{
		return this.values;
	}
	
	public MutableComponent getTranslationName()
	{
		return this.translatedName;
	}
	
	public ResourceLocation getName()
	{
		return this.name;
	}
	
	public Component getTooltip(boolean hasShiftDown)
	{
		MutableComponent tooltip = Component.literal(this.translatedName.getString());
		if (!hasShiftDown)
		{
			tooltip.append("\n");
			tooltip.append(Component.translatable("gui.crackerslib.button.preset.holdShift").withStyle(ChatFormatting.DARK_GRAY));
		}
		else
		{
			this.getParent().ifPresent(preset -> 
			{
				String[] parentComponents = preset.description.getString().split("\n");
				for (int i = 0; i < parentComponents.length; i++)
				{
					tooltip.append("\n");
					tooltip.append(Component.literal(parentComponents[i].trim()).withStyle(ChatFormatting.GRAY));
				}
			});
			if (this.description != null)
			{
				String[] components = this.description.getString().split("\n");
				for (int i = 0; i < components.length; i++)
				{
					tooltip.append("\n");
					tooltip.append(Component.literal(components[i].trim()).withStyle(ChatFormatting.GRAY));
				}
			}
			Set<ReloadType> reloadTypes = new HashSet<>();
			for (Entry<ForgeConfigSpec.ConfigValue<?>, ?> entry : this.getValues().entrySet())
			{
				var reloadType = ConfigHolder.getReloadType(this.config.getModId(), entry.getKey());
				if (reloadType != ReloadType.NONE)
					reloadTypes.add(reloadType);
			}
			for (ReloadType reloadType : reloadTypes)
			{
				tooltip.append("\n");
				tooltip.append(Component.literal("May require reload of: " + reloadType.toString()).withStyle(ChatFormatting.YELLOW));
			}
			tooltip.append("\n");
			tooltip.append(Component.literal(this.name.toString()).withStyle(ChatFormatting.DARK_GRAY));
			this.getParent().ifPresent(parent -> 
			{
				tooltip.append("\n");
				tooltip.append(Component.literal("parent: " + parent.getName().toString()).withStyle(ChatFormatting.DARK_GRAY));
			});
			tooltip.append("\n");
			tooltip.append(Component.translatable("config.crackerslib.preset.note").withStyle(ChatFormatting.GRAY));
		}
		return tooltip;
	}
//	
//	public boolean doValuesMatch(Map<ForgeConfigSpec.ConfigValue<?>, Object> values)
//	{
//		boolean flag = true;
//		if (!this.values.isEmpty())
//		{
//			for (var entry : values.entrySet())
//			{
//				flag = entry.getValue().equals(this.values.get(entry.getKey()));
//				if (!flag)
//					break;
//			}
//		}
//		return flag;
//	}
	
	public <T> boolean doesValueMatch(ForgeConfigSpec.ConfigValue<T> config, T value)
	{
		if (!this.values.isEmpty())
		{
			var presetValue = this.values.get(config);
			if (presetValue != null)
				return value.equals(presetValue);
			else
				return true;
		}
		return true;
	}
	
	public boolean isDefault()
	{
		return this.isDefault;
	}
	
	public ConfigHolder getAssociatedConfig()
	{
		return this.config;
	}
	
	public Optional<ConfigPreset> getParent()
	{
		return this.parent;
	}
	
	@Override
	public String toString()
	{
		return "ConfigPreset[" + this.translatedName.getString() + "]";
	}
	
	public static class Builder
	{
		private final ConfigHolder config;
		private final Map<ForgeConfigSpec.ConfigValue<?>, Object> values;
		private boolean isDefault;
		private Optional<ConfigPreset> parent = Optional.empty();
		
		private Builder(ConfigHolder config, Map<ForgeConfigSpec.ConfigValue<?>, Object> map)
		{
			this.config = config;
			this.values = map;
		}
		
		public static Builder of(ConfigHolder config)
		{
			Map<ForgeConfigSpec.ConfigValue<?>, Object> map = Maps.newHashMap();
			config.getValues().forEach(value -> {
				if (!config.shouldExclude(value))
					map.put(value, value.getDefault());
			});
			return new Builder(config, map).setDefault(true);
		}
		
		public static Builder ofPreset(ConfigPreset preset)
		{
			if (preset.config != null)
			{
				Map<ForgeConfigSpec.ConfigValue<?>, Object> map = Maps.newHashMap();
				preset.getValues().forEach((key, value) -> {
					map.put(key, value);
				});
				return new Builder(preset.getAssociatedConfig(), map).setParent(preset).setDefault(preset.isDefault());
			}
			else
			{
				throw new NullPointerException("Cannot construct ConfigPreset based off of another ConfigPreset with a null config");
			}
		}
		
		public static <T> Builder empty()
		{
			return new Builder(null, Maps.newHashMap());
		}
		
		public <T> Builder setConfigPreset(ForgeConfigSpec.ConfigValue<T> config, T value)
		{
			this.values.computeIfPresent(config, (key, old) -> value);
			return this.setDefault(false);
		}
		
		private Builder setDefault(boolean isDefault)
		{
			this.isDefault = isDefault;
			return this;
		}
		
		private Builder setParent(ConfigPreset preset)
		{
			this.parent = Optional.of(preset);
			return this;
		}
		
		public ConfigPreset build(MutableComponent translation, ResourceLocation name)
		{
			return new ConfigPreset(this.config, Maps.newLinkedHashMap(this.values), translation, name, this.isDefault, this.parent);
		}
	}
}
