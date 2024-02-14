package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigListItem;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;

public abstract class ConfigEntry<T, W extends AbstractWidget> implements ConfigListItem
{
	protected final Minecraft mc;
//	protected final ConfigHolder config;
	protected final ForgeConfigSpec.ConfigValue<T> value;
	protected final ForgeConfigSpec.ValueSpec valueSpec;
	protected final ForgeConfigSpec spec;
//	protected final T defaultValue;
	protected final boolean requiresRestart;
	protected final String path;
	protected final Component name;
	protected final Component description;
	protected final @Nullable Component restartText;
	private final Runnable onValueUpdated;
	protected W widget;
	protected Component displayName;
	
	public ConfigEntry(Minecraft mc, String modid, String path, ForgeConfigSpec spec, Runnable onValueUpdated)
	{
		this.mc = mc;
		this.path = path;
		this.value = spec.getValues().getRaw(path);
		this.valueSpec = spec.getRaw(path);
		this.requiresRestart = this.valueSpec.needsWorldRestart();
		this.spec = spec;
		this.name = Component.translatable("gui." + modid + ".config." + ConfigListItem.extractNameFromPath(path) + ".title");
		String key = this.valueSpec.getTranslationKey();
		if (key.isEmpty())
			this.description = Component.literal(this.valueSpec.getComment());
		else
			this.description = Component.translatable(key);
		this.onValueUpdated = onValueUpdated;
		if (this.requiresRestart)
			this.restartText = Component.translatable("gui.crackerslib.screen.config.requiresRestart").withStyle(ChatFormatting.RED);
		else
			this.restartText = null;
	}
	
	protected Runnable getValueUpdatedResponder()
	{
		return this.onValueUpdated;
	}
	
//	public ForgeConfigSpec.ConfigValue<T> getConfigValue()
//	{
//		return this.value;
//	}
	
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
	public void setFromPreset(ConfigPreset preset)
	{
		if (preset.hasValue(this.path))
			this.setCurrentValue(preset.getValue(this.path));
		else if (preset.isDefault())
			this.setCurrentValue(this.value.getDefault());
	}
	
	@Override
	public boolean isValueReset()
	{
		return this.value.get().equals(this.getCurrentValue());
	}
	
	@Override
	public boolean matchesPreset(ConfigPreset preset)
	{
		if (preset.hasValue(this.path))
			return preset.getValue(this.path).equals(this.getCurrentValue());
		else if (preset.isDefault())
			return this.value.getDefault().equals(this.getCurrentValue());
		else
			return true;
	}
	
	@Override
	public void onSavedAndClosed()
	{
		var current = this.getCurrentValue();
		if (this.valueSpec.test(current))
			this.value.set(current);
	}
	
	@Override
	public void init(List<AbstractWidget> widgets, int x, int y, int width, int height)
	{
		if (this.widget == null)
			this.widget = this.buildWidget(x, y, width, height);
		widgets.add(this.widget);
		int allowedWidth = width - this.widget.getWidth() - x - 5;
		if (this.requiresRestart)
			allowedWidth -= this.mc.font.width(this.restartText);
		this.displayName = ConfigListItem.shortenText(this.name, allowedWidth);
	}
	
	@Override
	public void render(GuiGraphics stack, int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks)
	{
		Component component = this.displayName;
		if (this.widget.isFocused())
			component = component.copy().withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.YELLOW));
		stack.drawString(this.mc.font, component, x + 5 + (this.widget.getX() - x) + this.widget.getWidth(), y + height / 2 - this.mc.font.lineHeight / 2, 0xFFFFFFFF);
		this.widget.setY(y + height / 2 - this.widget.getHeight() / 2);
		this.widget.render(stack, mouseX, mouseY, partialTicks);
		if (this.restartText != null)
			stack.drawString(this.mc.font, this.restartText, x + width - this.mc.font.width(this.restartText) - 5, y + height / 2 - this.mc.font.lineHeight / 2, 0xFFFFFFFF);
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
		if (this.requiresRestart)
		{
			comment.append("\n");
			comment.append(Component.translatable("gui.crackerslib.screen.config.requiresRestart").withStyle(ChatFormatting.YELLOW));
		}
		return Tooltip.create(comment);
	}
	
	@Override
	public int compareTo(ConfigListItem item)
	{
		if (item instanceof ConfigEntry<?, ?> entry)
			return this.path.compareTo(entry.path);
		else
			return -1;
	}
	
	@Override
	public boolean matchesSearch(String text)
	{
		String lowerCase = text.toLowerCase();
		return this.path.toLowerCase().replace("_", " ").contains(lowerCase) || this.getName().getString().toLowerCase().contains(lowerCase);
	}
	
//	public static ConfigEntry<Integer, EditBox> integerVal(Minecraft mc, ForgeConfigSpec.ConfigValue<Integer> value)
//	{
//		return new ConfigEntry<>(mc, value, (val, x, y, width, height) -> 
//		{
//			EditBox box = new EditBox(mc.font, x + 5, y + height / 2 - 10, 50, 20, Component.empty());
//			box.setValue(String.valueOf(val));
//			return box;
//		}, widget -> {
//			try {
//				return Integer.parseInt(widget.getValue());
//			} catch (NumberFormatException e) {
//				return value.get();
//			}
//		}, (widget, val) -> {
//			try 
//			{
//				if (ConfigHolder.isValid(val, Integer.parseInt(widget.getValue())))
//					widget.setTextColor(0xFFFFFFFF);
//				else
//					widget.setTextColor(ChatFormatting.RED.getColor());
//			}
//			catch (NumberFormatException e)
//			{
//				widget.setTextColor(ChatFormatting.RED.getColor());
//			}
//		});
//	}
//	
//	public static ConfigEntry<Double, EditBox> doubleVal(Minecraft mc, ForgeConfigSpec.ConfigValue<Double> value)
//	{
//		return new ConfigEntry<>(mc, value, (val, x, y, width, height) -> 
//		{
//			EditBox box = new EditBox(mc.font, x + 5, y + height / 2 - 10, 50, 20, Component.empty());
//			box.setValue(String.valueOf(val));
//			return box;
//		}, widget -> {
//			try {
//				return Double.parseDouble(widget.getValue());
//			} catch (NumberFormatException e) {
//				return value.get();
//			}
//		}, (widget, val) -> {
//			try 
//			{
//				if (ConfigHolder.isValid(val, Double.parseDouble(widget.getValue())))
//					widget.setTextColor(0xFFFFFFFF);
//				else
//					widget.setTextColor(ChatFormatting.RED.getColor());
//			}
//			catch (NumberFormatException e)
//			{
//				widget.setTextColor(ChatFormatting.RED.getColor());
//			}
//		});
//	}
//	
//	public static ConfigEntry<Boolean, CyclableButton<Boolean>> booleanVal(Minecraft mc, ForgeConfigSpec.ConfigValue<Boolean> value)
//	{
//		return new ConfigEntry<>(mc, value, (val, x, y, width, height) -> {
//			return new CyclableButton<>(x + 6, y, 60, Lists.newArrayList(Boolean.FALSE, Boolean.TRUE), val);
//		}, widget -> {
//			return widget.getValue();
//		}, (widget, val) -> {
//		});
//	}
//	
//	@FunctionalInterface
//	public static interface WidgetFactory<T, W extends AbstractWidget>
//	{
//		W make(T value, int x, int y, int width, int height);
//	}
}
