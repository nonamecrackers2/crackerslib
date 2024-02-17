package nonamecrackers2.crackerslib.client.gui.widget.config;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import nonamecrackers2.crackerslib.client.util.SortType;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;

public class ConfigCategory implements ConfigListItem
{
	private final Minecraft mc;
	private final List<ConfigListItem> children = Lists.newArrayList();
	private final List<ConfigCategory> categories = Lists.newArrayList();
	private final String path;
	private final ConfigOptionList list;
	private final String modid;
	private final Component name;
	private Component displayName;
	private Button expand;
	private boolean isExpanded;
	private int x;
	private SortType sortType = SortType.A_TO_Z;
	
	public ConfigCategory(Minecraft mc, String modid, String path, ConfigOptionList list)
	{
		this.mc = mc;
		this.modid = modid;
		this.path = path;
		this.list = list;
		this.name = Component.translatable("gui." + this.modid + ".config.category." + ConfigListItem.extractNameFromPath(path) + ".title").withStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.YELLOW));
	}
	
	public void setSorting(SortType sorting)
	{
		this.sortType = sorting;
	}
	
	public void addChild(ConfigListItem item)
	{
		if (!this.children.contains(item))
			this.children.add(item);
	}
	
	@Override
	public void init(List<AbstractWidget> widgets, int x, int y, int width, int height)
	{
		Component buttonText;
		if (this.isExpanded)
			buttonText = Component.literal("-").withStyle(ChatFormatting.RED);
		else
			buttonText = Component.literal("+").withStyle(ChatFormatting.GREEN);
		this.expand = Button.builder(buttonText, b -> 
		{
			this.isExpanded = !this.isExpanded;
			this.list.rebuildList();
		}).bounds(x + 6, y, 20, 20).build();
		widgets.add(this.expand);
//		this.sortType.sortList(this.children);
		if (!this.isExpanded)
			this.children.forEach(child -> child.init(Lists.newArrayList(), x + 20, y, width, height));
		this.x = x;
		
		this.displayName = ConfigListItem.shortenText(this.name, width - this.expand.getWidth() - x - 5);
	}

	@Override
	public void render(GuiGraphics stack, int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks)
	{
		this.expand.setY(y + height / 2 - this.expand.getHeight() / 2);
		this.expand.render(stack, mouseX, mouseY, partialTicks);
		stack.drawString(this.mc.font, this.displayName, x + 5 + (this.expand.getX() - x) + this.expand.getWidth(), y + height / 2 - this.mc.font.lineHeight / 2, 0xFFFFFFFF);
	}

	@Override
	public void onSavedAndClosed()
	{
		this.children.forEach(child -> child.onSavedAndClosed());
	}

	@Override
	public void resetValue()
	{
		this.children.forEach(child -> child.resetValue());
	}

	@Override
	public boolean isValueReset()
	{
		return this.children.stream().allMatch(ConfigListItem::isValueReset);
	}

	@Override
	public boolean matchesPreset(ConfigPreset preset)
	{
		return this.children.stream().allMatch(child -> child.matchesPreset(preset));
	}

	@Override
	public void setFromPreset(ConfigPreset preset)
	{
		this.children.forEach(child -> child.setFromPreset(preset));
	}

	@Override
	public Tooltip getTooltip(ConfigPreset preset)
	{
		return null;
	}
	
	public String getPath()
	{
		return this.path;
	}
	
	public boolean isExpanded()
	{
		return this.isExpanded;
	}
	
	public void setExpanded(boolean flag)
	{
		this.isExpanded = flag;
	}
	
	public List<ConfigListItem> gatherChildren(String search, boolean expandOrContractCategories)
	{
		this.sortType.sortList(this.children);
		List<ConfigListItem> items = Lists.newArrayList();
		for (ConfigListItem item : this.children)
		{
			if (search.isEmpty() || item.matchesSearch(search))
			{
				items.add(item);
				if (item instanceof ConfigCategory category && category.isExpanded())
					items.addAll(category.gatherChildren(search, expandOrContractCategories));
			}
		}
		return items;
	}
	
	public List<ConfigListItem> getImmediateChildren()
	{
		return this.children;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	@Override
	public int compareTo(ConfigListItem item)
	{
		if (item instanceof ConfigCategory category)
			return this.path.compareTo(category.path);
		else
			return 0;
	}
	
	@Override
	public boolean matchesSearch(String text)
	{
		return this.children.stream().anyMatch(c -> c.matchesSearch(text));
	}
	
	public void addCategory(ConfigCategory category)
	{
		this.categories.add(category);
		this.children.add(category);
	}
}
