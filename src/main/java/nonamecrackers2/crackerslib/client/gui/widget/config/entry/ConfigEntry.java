package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.RestartType;
import net.neoforged.neoforge.common.NeoForge;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigListItem;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;
import nonamecrackers2.crackerslib.common.event.impl.OnConfigOptionSaved;

public abstract class ConfigEntry<T, W extends AbstractWidget> implements ConfigListItem
{
	protected final Minecraft mc;
	protected final String modid;
	protected final ModConfig.Type type;
	protected final ModConfigSpec.ConfigValue<T> value;
	protected final ModConfigSpec.ValueSpec valueSpec;
	protected final ModConfigSpec spec;
	protected final RestartType restartType;
	protected final String path;
	protected final Component name;
	protected final Component description;
	protected final @Nullable Component restartText;
	private final Runnable onValueUpdated;
	protected W widget;
	protected Component displayName;
	
	public ConfigEntry(Minecraft mc, String modid, ModConfig.Type type, String path, ModConfigSpec spec, Runnable onValueUpdated)
	{
		this.mc = mc;
		this.modid = modid;
		this.type = type;
		this.path = path;
		this.value = spec.getValues().getRaw(path);
		this.valueSpec = spec.getSpec().getRaw(path);
		this.restartType = this.valueSpec.restartType();
		this.spec = spec;
		this.name = Component.translatable("gui." + modid + ".config." + ConfigListItem.extractNameFromPath(path) + ".title");
		String key = this.valueSpec.getTranslationKey();
		if (key == null || key.isEmpty())
			this.description = Component.literal(this.valueSpec.getComment());
		else
			this.description = Component.translatable(key);
		this.onValueUpdated = onValueUpdated;
		if (this.restartType != RestartType.NONE)
			this.restartText = Component.translatable("gui.crackerslib.screen.config.requiresRestart", this.restartType.toString()).withStyle(ChatFormatting.RED);
		else
			this.restartText = null;
	}
	
	protected Runnable getValueUpdatedResponder()
	{
		return this.onValueUpdated;
	}
	
	public Component getName()
	{
		return this.name;
	}
	
	public Component getDescription()
	{
		return this.description;
	}
	
	protected abstract W buildWidget(int x, int y, int width, int height);
	
	protected abstract T getCurrentValue();
	
	protected abstract void setCurrentValue(T value);
	
	@Override
	public void resetValue()
	{
		this.setCurrentValue(this.value.get());
	}
	
	@Override
	public void setFromPreset(ConfigPreset preset, Predicate<String> excluded)
	{
		if (!excluded.test(this.path))
		{
			if (preset.hasValue(this.path))
				this.setCurrentValue(preset.getValue(this.path));
			else
				this.setCurrentValue(this.value.getDefault());
		}
	}
	
	@Override
	public boolean isValueReset()
	{
		return this.value.get().equals(this.getCurrentValue());
	}
	
	@Override
	public boolean matchesPreset(ConfigPreset preset, Predicate<String> excluded)
	{
		if (!excluded.test(this.path))
		{
			if (preset.hasValue(this.path))
				return preset.getValue(this.path).equals(this.getCurrentValue());
			else
				return this.value.getDefault().equals(this.getCurrentValue());
		}
		else
		{
			return true;
		}
	}
	
	@Override
	public void onSavedAndClosed()
	{
		var current = this.getCurrentValue();
		if (this.valueSpec.test(current))
		{
			OnConfigOptionSaved<T> event = new OnConfigOptionSaved<>(this.modid, this.type, OnConfigOptionSaved.Source.CONFIG_SCREEN, this.value, current, !Objects.equals(current, this.value.get()));
			NeoForge.EVENT_BUS.post(event);
			if (event.getOverrideValue() != null && this.valueSpec.test(event.getOverrideValue()))
				current = event.getOverrideValue();
			this.value.set(current);
		}
	}
	
	@Override
	public void init(List<AbstractWidget> widgets, int x, int y, int width, int height)
	{
		if (this.widget == null)
			this.widget = this.buildWidget(x, y, width, height);
		widgets.add(this.widget);
		int allowedWidth = width - this.widget.getWidth() - x - 5;
		if (this.restartType != RestartType.NONE)
			allowedWidth -= this.mc.font.width(this.restartText);
		this.displayName = ConfigListItem.shortenText(this.name, allowedWidth);
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor stack, int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks)
	{
		Component component = this.displayName;
		if (this.widget.isFocused())
			component = component.copy().withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.YELLOW));
		stack.text(this.mc.font, component, x + 5 + (this.widget.getX() - x) + this.widget.getWidth(), y + height / 2 - this.mc.font.lineHeight / 2, 0xFFFFFFFF);
		this.widget.setY(y + height / 2 - this.widget.getHeight() / 2);
		this.widget.extractRenderState(stack, mouseX, mouseY, partialTicks);
		if (this.restartText != null)
			stack.text(this.mc.font, this.restartText, x + width - this.mc.font.width(this.restartText) - 5, y + height / 2 - this.mc.font.lineHeight / 2, 0xFFFFFFFF);
	}
	
	@Override
	public @Nullable Tooltip getTooltip(ConfigPreset preset)
	{
		return this.createConfigTooltip(preset);
	}
	
	protected Tooltip createConfigTooltip(ConfigPreset preset)
	{ 
		MutableComponent comment = this.description.copy();
		comment.append("\n");
		comment.append(Component.literal(this.path).withStyle(ChatFormatting.GRAY));
		String defaultName = "Default: ";
		T object;
		if (preset != null && !preset.isDefault() && preset.hasValue(this.path))
		{
			defaultName = "Default (" + preset.name().getString() + "): ";
			object = preset.getValue(this.path);
		}
		else
		{
			object = this.value.getDefault();
		}
		comment.append("\n");
		comment.append(Component.literal(defaultName + object).withStyle(ChatFormatting.GREEN));
		if (this.restartType != RestartType.NONE)
		{
			comment.append("\n");
			comment.append(Component.translatable("gui.crackerslib.screen.config.requiresRestart", this.restartType).withStyle(ChatFormatting.YELLOW));
		}
		return Tooltip.create(comment);
	}
	
	@Override
	public int compareTo(ConfigListItem item)
	{
		if (item instanceof ConfigEntry<?, ?> entry)
			return this.path.compareTo(entry.path);
		else
			return 0;
	}
	
	@Override
	public boolean matchesSearch(String text)
	{
		String lowerCase = text.toLowerCase();
		return this.path.toLowerCase().replace("_", " ").contains(lowerCase) || this.getName().getString().toLowerCase().contains(lowerCase);
	}
}
