package nonamecrackers2.crackerslib.client.gui.widget.config;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.ConfigEntry;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;

public class ConfigOptionList extends ContainerObjectSelectionList<ConfigOptionList.Entry>
{
	private static final int ROW_HEIGHT = 30;
	private final List<ConfigListItem> items = Lists.newArrayList();
	private final List<ConfigCategory> categories = Lists.newArrayList();
	private String lastSearch = "";
	private final Runnable valuesChangedResponder;
	private final String modid;
	
	public ConfigOptionList(Minecraft mc, String modid, int width, int height, int top, int bottom, Runnable valuesChangedResponder)
	{
		super(mc, width, height, top, bottom, ROW_HEIGHT);
		this.modid = modid;
		this.setRenderBackground(false);
		this.setRenderTopAndBottom(true);
		this.valuesChangedResponder = valuesChangedResponder;
	}
	
	public String getModid()
	{
		return this.modid;
	}
	
	public <T> void addConfigValue(ForgeConfigSpec.ConfigValue<T> value, ConfigOptionList.ConfigEntryBuilder<T> itemBuilder)
	{
		ConfigCategory category = this.getOrCreateCategoryFor(value);
		if (category != null)
			category.addChild(itemBuilder.build(this.minecraft, this.modid, value, this.valuesChangedResponder));
		else
			this.items.add(itemBuilder.build(this.minecraft, this.modid, value, this.valuesChangedResponder));
	}
	
	private @Nullable ConfigCategory getOrCreateCategoryFor(ForgeConfigSpec.ConfigValue<?> value)
	{
		List<String> rawPath = value.getPath();
		rawPath.remove(rawPath.size() - 1);
		String beginning = rawPath.get(0);
		rawPath.remove(0);
		if (!rawPath.isEmpty())
		{
			String current = beginning;
			ConfigCategory prevCategory = null;
			for (int i = 0; i < rawPath.size(); i++)
			{
				String str = rawPath.get(i);
				current += "." + str;
				ConfigCategory category = this.getCategoryByPath(current);
				if (category == null)
				{
					category = new ConfigCategory(this.minecraft, this.modid, current, this);
					this.categories.add(category);
					if (prevCategory == null)
						this.items.add(category);
					else
						prevCategory.addCategory(category);
				}
				prevCategory = category;
			}
			return prevCategory;
		}
		else
		{
			return null;
		}
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
	
	public void buildList()
	{
		this.buildList("");
	}
	
	public void buildList(String text)
	{
		this.clearEntries();
		Collections.sort(this.items);
		List<ConfigListItem> items = Lists.newArrayList();
		for (ConfigListItem item : this.items)
		{
			items.add(item);
			if (item instanceof ConfigCategory category && category.isExpanded())
				items.addAll(category.getChildren());
		}
		for (ConfigListItem item : items)
		{
			var entry = new ConfigOptionList.Entry(item);
			if (text.isEmpty() || item.matchesSearch(text))
				this.addEntry(entry);
		}
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
	
	public ConfigPreset getMatchingPreset(List<ConfigPreset> presets)
	{
		ConfigPreset empty = null;
		for (ConfigPreset preset : presets)
		{
			if (!preset.getValues().isEmpty())
			{
				boolean matches = true;
				for (ConfigListItem item : this.items)
				{
					if (!item.matchesPreset(preset))
					{
						matches = false;
						break;
					}
				}
				if (matches)
					return preset;
			}
			else
			{
				empty = preset;
			}
		}
		if (empty == null)
			throw new NullPointerException("This shouldn't happen! Could not find matching preset.");
		return empty;
	}
	
	public void setFromPreset(ConfigPreset preset)
	{
		for (ConfigListItem item : this.items)
			item.setFromPreset(preset);
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
			stack.setColor(0.15F, 0.15F, 0.15F, 1.0F);
			stack.blit(Screen.BACKGROUND_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
			stack.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
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
	public static interface ConfigEntryBuilder<T>
	{
		ConfigEntry<T, ?> build(Minecraft mc, String modid, ForgeConfigSpec.ConfigValue<T> value, Runnable responder);
	}
}
