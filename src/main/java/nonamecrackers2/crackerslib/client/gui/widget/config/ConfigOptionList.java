package nonamecrackers2.crackerslib.client.gui.widget.config;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.ConfigEntry;
import nonamecrackers2.crackerslib.client.util.SortType;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;

public class ConfigOptionList extends ContainerObjectSelectionList<ConfigOptionList.Entry>
{
	private static final Component NO_CONFIG_OPTIONS = Component.translatable("gui.crackerslib.config.noAvailableOptions");
	private static final int ROW_HEIGHT = 30;
	private final List<ConfigListItem> items = Lists.newArrayList();
	private final List<ConfigCategory> categories = Lists.newArrayList();
	private String lastSearch = "";
	private SortType sortType = SortType.A_TO_Z;
	private final Runnable valuesChangedResponder;
	private final String modid;
	private final ForgeConfigSpec spec;
	
	public ConfigOptionList(Minecraft mc, String modid, ForgeConfigSpec spec, int width, int height, int top, int bottom, Runnable valuesChangedResponder)
	{
		super(mc, width, height, top, bottom, ROW_HEIGHT);
		this.modid = modid;
		this.spec = spec;
		this.setRenderBackground(false);
		this.setRenderTopAndBottom(true);
		this.valuesChangedResponder = valuesChangedResponder;
	}
	
	public String getModid()
	{
		return this.modid;
	}
	
	public <T> void addConfigValue(String path, ConfigOptionList.ConfigEntryBuilder itemBuilder, Optional<ConfigCategory> category)
	{
		category.ifPresentOrElse(c -> {
			c.addChild(itemBuilder.build(this.minecraft, this.modid, path, this.spec, this.valuesChangedResponder));
		}, () -> {
			this.items.add(itemBuilder.build(this.minecraft, this.modid, path, this.spec, this.valuesChangedResponder));
		});
	}
	
	public ConfigCategory makeCategory(String path, Optional<ConfigCategory> previousCategory)
	{
		var category = new ConfigCategory(this.minecraft, this.modid, path, this);
		previousCategory.ifPresentOrElse(c -> {
			c.addChild(category);
		}, () -> {
			this.items.add(category);
		});
		this.categories.add(category);
		return category;
	}
	
	private @Nullable ConfigCategory getCategoryByPath(String path)
	{
		for (ConfigCategory category : this.categories)
		{
			if (path.equals(category.getPath()))
				return category;
		}
		return null;
	}
	
	public void setSorting(SortType sorting)
	{
		this.sortType = sorting;
	}
	
	public void collapseAllCategories()
	{
		for (ConfigCategory category : this.categories)
			category.setExpanded(false);
		this.rebuildList();
	}
	
	public void buildList()
	{
		this.buildList("", false);
	}
	
	public void rebuildList()
	{
		this.buildList(this.getLastSearchingFor(), false);
	}
	
	public void buildList(String text, boolean expandOrContractCategories)
	{
		this.clearEntries();
		this.sortType.sortList(this.items);
		List<ConfigListItem> items = Lists.newArrayList();
		for (ConfigCategory category : this.categories)
		{
			category.setSorting(this.sortType);
			if (expandOrContractCategories)
				category.setExpanded(!text.isBlank());
		}
		for (ConfigListItem item : this.items)
		{
			if (text.isEmpty() || item.matchesSearch(text))
			{
				items.add(item);
				if (item instanceof ConfigCategory category && category.isExpanded())
					items.addAll(category.gatherChildren(text, expandOrContractCategories));
			}
		}
		for (ConfigListItem item : items)
			this.addEntry(new ConfigOptionList.Entry(item));
		this.lastSearch = text;
	}

	public void onClosed()
	{
		for (ConfigListItem item : this.items)
			item.onSavedAndClosed();
	}
	
	public void resetValues()
	{
		for (ConfigListItem item : this.items)
			item.resetValue();
	}
	
	public boolean areValuesReset()
	{
		for (ConfigListItem item : this.items)
		{
			if (!item.isValueReset())
				return false;
		}
		return true;
	}
	
	public @Nullable ConfigPreset getMatchingPreset(List<ConfigPreset> presets, Predicate<String> excluded)
	{
		main:
		for (ConfigPreset preset : presets)
		{
			for (ConfigListItem item : this.items)
			{
				if (!item.matchesPreset(preset, excluded))
					continue main;
			}
			return preset;
		}
		return null;
	}
	
	public void setFromPreset(ConfigPreset preset, Predicate<String> excluded)
	{
		for (ConfigListItem item : this.items)
			item.setFromPreset(preset, excluded);
	}
	
	@Override
	public int getRowWidth()
	{
		return this.getWidth() - 40;
	}
	
	@Override
	protected int getScrollbarPosition()
	{
		return this.getLeft() + this.getWidth() - 5;
	}
	
	@Override
	protected void renderBackground(GuiGraphics stack)
	{
		if (this.minecraft.level != null)
		{
			 stack.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
		}
		else
		{
			RenderSystem.setShaderColor(0.15F, 0.15F, 0.15F, 1.0F);
			stack.blit(Screen.BACKGROUND_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
	
	@Override
	public void render(GuiGraphics stack, int mouseX, int mouseY, float partialTick)
	{
		super.render(stack, mouseX, mouseY, partialTick);
		if (this.children().isEmpty())
			stack.drawCenteredString(this.minecraft.font, NO_CONFIG_OPTIONS, this.width / 2, this.height / 2, 0xFFFFFFFF);
	}

	public @Nullable ConfigListItem getItemAt(int mouseX, int mouseY)
	{
		var entry = this.getEntryAtPosition((double)mouseX, (double)mouseY);
		if (entry != null && entry.children.stream().anyMatch(w -> !w.isHovered()))
			return entry.item;
		else
			return null;
	}
	
	private @Nullable ConfigCategory getCategoryFor(ConfigListItem item)
	{
		for (ConfigCategory category : this.categories)
		{
			if (category.getImmediateChildren().contains(item))
				return category;
		}
		return null;
	}
	
	public String getLastSearchingFor()
	{
		return this.lastSearch;
	}
	
	public class Entry extends ContainerObjectSelectionList.Entry<ConfigOptionList.Entry>
	{
		private final List<AbstractWidget> children = Lists.newArrayList();
		private final ConfigListItem item;
		private final int x;
		
		public Entry(ConfigListItem item)
		{
			this.item = item;
			ConfigCategory category = ConfigOptionList.this.getCategoryFor(item);
			int x = (ConfigOptionList.this.getWidth() - ConfigOptionList.this.getRowWidth()) / 2;
			if (category != null)
				x = category.getX() + 20;
			this.item.init(this.children, x, ConfigOptionList.this.getTop(), ConfigOptionList.this.getRowWidth(), ConfigOptionList.this.itemHeight);
			this.x = x;
		}
		
		@Override
		public List<? extends GuiEventListener> children()
		{
			return this.children;
		}

		@Override
		public List<? extends NarratableEntry> narratables()
		{
			return this.children;
		}

		@Override
		public void render(GuiGraphics stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean selected, float partialTicks)
		{
			stack.renderOutline(this.x, top, width - (this.x - left), height, 0xAAFFFFFF);
			if (this.x > left)
			{
				stack.fill(this.x - 20, top + height / 2, this.x - 4, top + height / 2 + 1, 0x55FFFFFF);
				stack.fill(this.x - 20, top - height / 2 - 3, this.x - 19, top + height / 2, 0x55FFFFFF);
			}
			this.item.render(stack, left, top, width, height, mouseX, mouseY, partialTicks);
		}
	}
	
	@FunctionalInterface
	public static interface ConfigEntryBuilder
	{
		ConfigEntry<?, ?> build(Minecraft mc, String modid, String path, ForgeConfigSpec spec, Runnable onValueUpdated);
	}
}
