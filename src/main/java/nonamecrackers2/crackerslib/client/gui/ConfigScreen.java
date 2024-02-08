package nonamecrackers2.crackerslib.client.gui;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigListItem;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigOptionList;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.BooleanConfigEntry;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.DoubleConfigEntry;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.EnumConfigEntry;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.IntegerConfigEntry;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.ListConfigEntry;
import nonamecrackers2.crackerslib.common.config.ConfigHolder;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;

public class ConfigScreen extends Screen
{
	private static final int TITLE_HEIGHT = 12;
	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 20;
	private static final int EXIT_BUTTON_OFFSET = 26;
	private final ConfigHolder config;
	private final Consumer<ConfigOptionList> itemGenerator;
	private final boolean isWorldLoaded;
	private final boolean hasSinglePlayerServer;
	private final @Nullable Screen previous;
	private final List<ConfigPreset> presets;
	private ConfigOptionList list;
	private Button exit;
	private Button changePreset;
	private Button reset;
	private ConfigPreset preset;
	private ConfigListItem currentHovered;
	private Tooltip currentHoveredTooltip;
	private EditBox searchBox;
	
	public ConfigScreen(ConfigHolder config, Consumer<ConfigOptionList> itemGenerator, boolean isWorldLoaded, boolean hasSinglePlayerServer, @Nullable Screen previous)
	{
		super(Component.translatable("gui.crackerslib.screen." + config.getType().extension() + "Options.title"));
		this.config = config;
		this.itemGenerator = itemGenerator;
		this.isWorldLoaded = isWorldLoaded;
		this.hasSinglePlayerServer = hasSinglePlayerServer;
		this.previous = previous;
		this.presets = this.config.getPresets();
	}
	
	@Override
	protected void init()
	{
		if (this.list == null)
		{
			this.list = new ConfigOptionList(this.minecraft, this.config.getModId(), this.width, this.height, 30, this.height - 30, this::onValueChanged);
			this.itemGenerator.accept(this.list);
		}
		this.list.buildList();
		this.list.updateSize(this.width, this.height, 30, this.height - 30);
		this.addRenderableWidget(this.list);
		
		this.exit = Button.builder(Component.translatable("gui.crackerslib.button.exitAndSave.title"), button -> this.closeMenu())
				.pos((this.width - BUTTON_WIDTH / 2) / 2, this.height - EXIT_BUTTON_OFFSET)
				.size(BUTTON_WIDTH / 2, BUTTON_HEIGHT)
				.build();
		
		this.preset = this.list.getMatchingPreset(this.presets);
		
		this.changePreset = Button.builder(Component.translatable("gui.crackerslib.button.preset.title").append(": " + this.preset.getTranslationName().getString()), button -> this.changePreset())
				.pos(10, this.height - EXIT_BUTTON_OFFSET)
				.size((int)Math.round(BUTTON_WIDTH / 1.5D), BUTTON_HEIGHT)
				.tooltip(Tooltip.create(this.preset.getTooltip(false)))
				.build();
		
		this.reset = Button.builder(Component.translatable("gui.crackerslib.button.reset.title"), button -> this.resetValues())
				.pos(this.width - (int)(BUTTON_WIDTH / 1.5D) - 10, this.height - EXIT_BUTTON_OFFSET)
				.size((int)Math.round(BUTTON_WIDTH / 1.5D), BUTTON_HEIGHT)
				.build();
		this.reset.active = false;
		
		Component searchText = Component.translatable("gui.crackerslib.screen.config.search");
		this.searchBox = new EditBox(this.font, this.width - this.width / 3 - 5, 5, this.width / 3, 20, searchText);
		this.searchBox.setHint(searchText);
		this.searchBox.setResponder(text -> {
			this.list.buildList(text);
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
		this.minecraft.setScreen(new ConfigScreen.Home(this.config.getModId(), this.isWorldLoaded, this.hasSinglePlayerServer, this.previous));
	}
	
	private void resetValues()
	{
		this.list.resetValues();
		this.preset = this.list.getMatchingPreset(this.presets);
		this.changePreset.setMessage(Component.translatable("gui.crackerslib.button.preset.title").append(": " + this.preset.getTranslationName().getString()));
		this.reset.active = false;
	}
	
	private void changePreset()
	{
		int index = this.presets.indexOf(this.preset) + 1;
		if (index >= this.presets.size())
			index = 0;
		this.preset = this.presets.get(index);
		this.list.setFromPreset(this.preset);
		this.changePreset.setMessage(Component.translatable("gui.crackerslib.button.preset.title").append(": " + this.preset.getTranslationName().getString()));
		this.reset.active = !this.list.areValuesReset();
	}
	
	@Override
	public void render(GuiGraphics stack, int mouseX, int mouseY, float partialTicks)
	{
		super.render(stack, mouseX, mouseY, partialTicks);
		stack.drawCenteredString(this.font, this.title.getString(), this.width / 2, TITLE_HEIGHT, 0xFFFFFF);
		this.changePreset.setTooltip(Tooltip.create(this.preset.getTooltip(hasShiftDown())));
		ConfigListItem item = this.list.getItemAt(mouseX, mouseY);
		if (this.currentHovered != item)
		{
			this.currentHovered = item;
			if (item != null)
				this.currentHoveredTooltip = item.getTooltip(this.preset);
			else
				this.currentHoveredTooltip = null;
		}
		if (!this.children().stream().anyMatch(c -> !c.equals(this.list) && c.isMouseOver((double)mouseX, (double)mouseY)) && this.currentHoveredTooltip != null)
			stack.renderTooltip(this.font, this.currentHoveredTooltip.toCharSequence(this.minecraft), mouseX, mouseY);
	}
	
	private void onValueChanged()
	{
		this.preset = this.list.getMatchingPreset(this.presets);
		this.changePreset.setMessage(Component.translatable("gui.crackerslib.button.preset.title").append(": " + this.preset.getTranslationName().getString()));
		this.reset.active = !this.list.areValuesReset();
	}
	
//	public static boolean canAddConfigToGui(ForgeConfigSpec.ConfigValue<?> value)
//	{
//		if (value == WitherStormModConfig.CLIENT.optifineWarning)
//			return WitherStormModCompat.isOptifineLoaded();
//		else if (value == WitherStormModConfig.CLIENT.aprilFools)
//			return WitherStormMod.isAprilFools();
//		else if (value == WitherStormModConfig.CLIENT.patronCosmetic)
//			return Contributors.currentPlayerHasCosmetic();
//		else
//			return true;
//	}
}
