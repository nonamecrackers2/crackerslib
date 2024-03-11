package nonamecrackers2.crackerslib.client.gui;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.event.impl.AddConfigEntryToMenuEvent;
import nonamecrackers2.crackerslib.client.gui.widget.CollapseButton;
import nonamecrackers2.crackerslib.client.gui.widget.SortButton;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigCategory;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigListItem;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigOptionList;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.BooleanConfigEntry;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.DoubleConfigEntry;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.EnumConfigEntry;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.IntegerConfigEntry;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.ListConfigEntry;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.StringConfigEntry;
import nonamecrackers2.crackerslib.client.util.EditBoxAccessor;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPresets;

public class ConfigScreen extends Screen
{
	private static final Logger LOGGER = LogManager.getLogger("crackerslib/ConfigScreen");
	private static final Component CUSTOM_PRESET_TITLE = Component.translatable("config.crackerslib.preset.custom.title");
	private static final Component CUSTOM_PRESET_DESCRIPTION = Component.translatable("config.crackerslib.preset.custom.description").withStyle(ChatFormatting.GRAY);
	private static final Component HOLD_SHIFT = Component.translatable("gui.crackerslib.button.preset.holdShift").withStyle(ChatFormatting.DARK_GRAY);
	private static final int TITLE_HEIGHT = 12;
	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 20;
	private static final int EXIT_BUTTON_OFFSET = 26;
	private final String modid;
	private final ForgeConfigSpec spec;
	private final Consumer<ConfigOptionList> itemGenerator;
	private final Screen homeScreen;
	private final List<ConfigPreset> presets;
	private ConfigOptionList list;
	private Button exit;
	private Button changePreset;
	private Button reset;
	private @Nullable ConfigPreset preset;
	private ConfigListItem currentHovered;
	private @Nullable List<Component> currentHoveredTooltip;
	private EditBox searchBox;
	
	public ConfigScreen(String modid, ForgeConfigSpec spec, ModConfig.Type type, Consumer<ConfigOptionList> itemGenerator, Screen homeScreen)
	{
		super(Component.translatable("gui.crackerslib.screen." + type.extension() + "Options.title"));
		this.modid = modid;
		this.spec = spec;
		this.itemGenerator = itemGenerator;
		this.homeScreen = homeScreen;
		this.presets = Lists.newArrayList(ConfigPreset.defaultPreset());
		var presets = ConfigPresets.getPresetsForModId(this.modid);
		if (presets != null)
		{
			for (ConfigPreset preset : presets.get(type))
				this.presets.add(preset);
		}
	}
	
	public static ConfigScreen makeScreen(String modid, ForgeConfigSpec spec, ModConfig.Type type, Screen homeScreen)
	{
		return new ConfigScreen(modid, spec, type, list -> {
			buildConfigList(modid, type, list, filterValues(modid, type, "", spec.getValues().valueMap()), "", Optional.empty());
		}, homeScreen);
	}
	
	private static Map<String, Object> filterValues(String modid, ModConfig.Type type, String previousPath, Map<String, Object> values)
	{
		return values.entrySet().stream().map(entry -> {
			var path = entry.getKey();
			if (!previousPath.isEmpty())
				path = previousPath + "." + path;
			return Map.entry(path, entry.getValue());
		}).filter(entry -> {
			return !MinecraftForge.EVENT_BUS.post(new AddConfigEntryToMenuEvent(modid, type, entry.getKey()));
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	private static void buildConfigList(String modid, ModConfig.Type type, ConfigOptionList list, Map<String, Object> values, String previousPath, Optional<ConfigCategory> category)
	{
		for (var entry : values.entrySet())
		{
			String path = entry.getKey();
			Object obj = entry.getValue();
			if (obj instanceof UnmodifiableConfig next)
			{
				var nextValues = filterValues(modid, type, path, next.valueMap());
				if (!nextValues.isEmpty())
				{
					ConfigCategory nextCategory = list.makeCategory(path, category);
					buildConfigList(modid, type, list, nextValues, path, Optional.of(nextCategory));
				}
			}
			else if (obj instanceof ForgeConfigSpec.ConfigValue<?> value)
			{
				if (!MinecraftForge.EVENT_BUS.post(new AddConfigEntryToMenuEvent(modid, type, entry.getKey())))
				{
					var clazz = value.getDefault().getClass();
					if (Integer.class.isAssignableFrom(clazz))
						list.addConfigValue(path, IntegerConfigEntry::new, category);
					else if (Double.class.isAssignableFrom(clazz))
						list.addConfigValue(path, DoubleConfigEntry::new, category);
					else if (Boolean.class.isAssignableFrom(clazz))
						list.addConfigValue(path, BooleanConfigEntry::new, category);
					else if (Enum.class.isAssignableFrom(clazz))
						list.addConfigValue(path, EnumConfigEntry::new, category);
					else if (String.class.isAssignableFrom(clazz))
						list.addConfigValue(path, StringConfigEntry::new, category);
					else if (tryToAddListEntry(list, clazz, path, value, category)) {}
					else
						LOGGER.warn("Unknown config GUI entry for type '{}'", clazz);
				}
			}
		}
	}
	
	protected static boolean tryToAddListEntry(ConfigOptionList list, Class<?> valueClass, String path, ForgeConfigSpec.ConfigValue<?> value, Optional<ConfigCategory> category)
	{
		if (List.class.isAssignableFrom(valueClass))
		{
			List<?> listValue = (List<?>)value.getDefault();
			if (listValue.size() > 0)
			{
				Class<?> clazz = listValue.get(0).getClass();
				if (String.class.isAssignableFrom(clazz))
					putListEntry(list, path, category, v -> v);
				else if (Double.class.isAssignableFrom(clazz))
					putListEntry(list, path, category, Double::parseDouble);
				else if (Float.class.isAssignableFrom(clazz))
					putListEntry(list, path, category, Float::parseFloat);
				else if (Integer.class.isAssignableFrom(clazz))
					putListEntry(list, path, category, Integer::parseInt);
				else
					return false;
				return true;
			}
			else
			{
				LOGGER.info("Could not determine generic type for empty list config value");
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	private static void putListEntry(ConfigOptionList list, String path, Optional<ConfigCategory> category, ListConfigEntry.ValueParser<?> parser)
	{
		list.addConfigValue(path, (mc, modid, p, s, r) -> {
			return new ListConfigEntry(mc, modid, p, s, r, parser);
		}, category);
	}
	
	@Override
	protected void init()
	{
		if (this.list == null)
		{
			this.list = new ConfigOptionList(this.minecraft, this.modid, this.spec,  this.width, this.height, 30, this.height - 30, this::onValueChanged);
			this.itemGenerator.accept(this.list);
		}
		this.list.buildList();
		this.list.updateSize(this.width, this.height, 30, this.height - 30);
		this.addRenderableWidget(this.list);
		
		this.exit = new Button((this.width - BUTTON_WIDTH / 2) / 2, this.height - EXIT_BUTTON_OFFSET, BUTTON_WIDTH / 2, BUTTON_HEIGHT, Component.translatable("gui.crackerslib.button.exitAndSave.title"), button -> this.closeMenu());
		
		this.preset = this.list.getMatchingPreset(this.presets);
		
		this.changePreset = new Button(10, this.height - EXIT_BUTTON_OFFSET, (int)Math.round(BUTTON_WIDTH / 1.5D), BUTTON_HEIGHT, Component.translatable("gui.crackerslib.button.preset.title").append(": ").append(this.getPresetName()), button -> this.changePreset(), (b, p, x, y) -> {
			this.renderComponentTooltip(p, this.getPresetTooltip(hasShiftDown()), x, y);
		});
		
		this.reset = new Button(this.width - (int)(BUTTON_WIDTH / 1.5D) - 10, this.height - EXIT_BUTTON_OFFSET, (int)Math.round(BUTTON_WIDTH / 1.5D), BUTTON_HEIGHT, Component.translatable("gui.crackerslib.button.reset.title"), button -> this.resetValues());
		this.reset.active = false;
		
		this.addRenderableWidget(new SortButton(5, 5, type -> {
			this.list.setSorting(type);
			this.list.rebuildList();
		}));
		
		this.addRenderableWidget(new CollapseButton(30, 5, () -> {
			this.list.collapseAllCategories();
		}));
		
		Component searchText = Component.translatable("gui.crackerslib.screen.config.search").withStyle(ChatFormatting.DARK_GRAY);
		this.searchBox = new EditBox(this.font, this.width - this.width / 3 - 5, 5, this.width / 3, 20, searchText);
		((EditBoxAccessor)this.searchBox).setHint(searchText);
		this.searchBox.setResponder(text -> {
			this.list.buildList(text, true);
			this.list.setScrollAmount(0.0D);
		});
		this.searchBox.setMaxLength(100);
		this.setInitialFocus(this.searchBox);
		
		this.addRenderableWidget(this.exit);
		this.addRenderableWidget(this.changePreset);
		this.addRenderableWidget(this.reset);
		this.addRenderableWidget(this.searchBox);
	}
	
	private void closeMenu()
	{
		this.list.onClosed();
		this.minecraft.setScreen(this.homeScreen);
	}
	
	private void resetValues()
	{
		this.list.resetValues();
		this.preset = this.list.getMatchingPreset(this.presets);
		this.changePreset.setMessage(Component.translatable("gui.crackerslib.button.preset.title").append(": ").append(this.getPresetName()));
		this.reset.active = false;
	}
	
	private void changePreset()
	{
		int next = this.presets.indexOf(this.preset) + 1;
		if (next >= this.presets.size())
			next = 0;
		this.preset = this.presets.get(next);
		if (this.preset != null)
			this.list.setFromPreset(this.preset);
		this.changePreset.setMessage(Component.translatable("gui.crackerslib.button.preset.title").append(": ").append(this.getPresetName()));
		this.reset.active = !this.list.areValuesReset();
	}
	
	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
	{
		super.render(stack, mouseX, mouseY, partialTicks);
		drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, TITLE_HEIGHT, 0xFFFFFF);
		ConfigListItem item = this.list.getItemAt(mouseX, mouseY);
		if (this.currentHovered != item)
		{
			this.currentHovered = item;
			if (item != null)
				this.currentHoveredTooltip = item.getTooltip(this.preset);
			else
				this.currentHoveredTooltip = null;
		}
		//TODO: Test tooltip width
		if (!this.children().stream().anyMatch(c -> !c.equals(this.list) && c.isMouseOver((double)mouseX, (double)mouseY)) && this.currentHoveredTooltip != null)
			this.renderComponentTooltip(stack, this.currentHoveredTooltip, mouseX, mouseY);
	}
	
	private void onValueChanged()
	{
		this.preset = this.list.getMatchingPreset(this.presets);
		this.changePreset.setMessage(Component.translatable("gui.crackerslib.button.preset.title").append(": ").append(this.getPresetName()));
		this.reset.active = !this.list.areValuesReset();
	}
	
	private List<Component> getPresetTooltip(boolean shiftDown)
	{
		if (this.preset != null)
			return this.preset.getTooltip(shiftDown);
		else
			return makeCustomPresetTooltip(shiftDown);
	}
	
	private Component getPresetName()
	{
		if (this.preset != null)
			return this.preset.name();
		else
			return CUSTOM_PRESET_TITLE;
	}
	
	private static List<Component> makeCustomPresetTooltip(boolean shiftDown)
	{
		List<Component> text = Lists.newArrayList();
		text.add(CUSTOM_PRESET_TITLE);
		if (shiftDown)
			text.add(CUSTOM_PRESET_DESCRIPTION);
		else
			text.add(HOLD_SHIFT);
		return text;
	}
}
