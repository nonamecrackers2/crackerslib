package nonamecrackers2.crackerslib.common.config.preset;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;

public record ConfigPreset(Map<String, Object> values, Component name, @Nullable Component description)
{
	public boolean hasValue(String path)
	{
		return this.values.containsKey(path);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T getValue(String path)
	{
		return (T)this.values.get(path);
	}
	
	public boolean isDefault()
	{
		return this.values.isEmpty();
	}
	
	public List<Component> getTooltip(boolean hasShiftDown)
	{
		List<Component> text = Lists.newArrayList();
		text.add(Component.literal(this.name().getString()));
		if (!hasShiftDown)
		{
			text.add(Component.translatable("gui.crackerslib.button.preset.holdShift").withStyle(ChatFormatting.DARK_GRAY));
		}
		else
		{
			if (this.description() != null)
			{
				String[] components = this.description().getString().split("\n");
				for (int i = 0; i < components.length; i++)
					text.add(Component.literal(components[i].trim()).withStyle(ChatFormatting.GRAY));
			}
			text.add(Component.translatable("config.crackerslib.preset.note").withStyle(ChatFormatting.GRAY));
		}
		return text;
	}
	
	public static ConfigPreset.Builder builder(Component name)
	{
		return new ConfigPreset.Builder(name);
	}
	
	public static ConfigPreset defaultPreset()
	{
		return new ConfigPreset(ImmutableMap.of(), Component.translatable("config.crackerslib.preset.default.title"), Component.translatable("config.crackerslib.preset.default.description"));
	}
	
	public static class Builder
	{
		private final Map<String, Object> values = Maps.newHashMap();
		private final Component name;
		private @Nullable Component description;
		
		private Builder(Component name)
		{
			this.name = name;
		}
		
		public Builder setDescription(Component desc)
		{
			this.description = desc;
			return this;
		}
		
		public <T> Builder setPreset(ForgeConfigSpec.ConfigValue<T> config, T value)
		{
			return this.setPreset(ConfigHelper.DOT_JOINER.join(config.getPath()), value);
		}
		
		public Builder setPreset(String path, Object value)
		{
			this.values.put(path, value);
			return this;
		}
		
		public ConfigPreset build()
		{
			return new ConfigPreset(ImmutableMap.copyOf(this.values), this.name, this.description);
		}
	}
}
