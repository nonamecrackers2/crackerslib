package nonamecrackers2.crackerslib.common.config.preset;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.ForgeConfigSpec;

public record ConfigPreset(Map<String, Object> values, Component name, @Nullable Component description)
{
	private static final Joiner JOINER = Joiner.on('.');
	
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
	
	public Component getTooltip(boolean hasShiftDown)
	{
		MutableComponent tooltip = Component.literal(this.name().getString());
		if (!hasShiftDown)
		{
			tooltip.append("\n");
			tooltip.append(Component.translatable("gui.crackerslib.button.preset.holdShift").withStyle(ChatFormatting.DARK_GRAY));
		}
		else
		{
			if (this.description() != null)
			{
				String[] components = this.description().getString().split("\n");
				for (int i = 0; i < components.length; i++)
				{
					tooltip.append("\n");
					tooltip.append(Component.literal(components[i].trim()).withStyle(ChatFormatting.GRAY));
				}
			}
			tooltip.append("\n");
			tooltip.append(Component.translatable("config.crackerslib.preset.note").withStyle(ChatFormatting.GRAY));
		}
		return tooltip;
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
			return this.setPreset(JOINER.join(config.getPath()), value);
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
