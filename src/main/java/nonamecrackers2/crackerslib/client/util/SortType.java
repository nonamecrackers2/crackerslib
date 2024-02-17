package nonamecrackers2.crackerslib.client.util;

import java.util.Comparator;
import java.util.List;

import net.minecraft.network.chat.Component;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigCategory;
import nonamecrackers2.crackerslib.client.gui.widget.config.ConfigListItem;
import nonamecrackers2.crackerslib.client.gui.widget.config.entry.ConfigEntry;

public enum SortType
{
	A_TO_Z("gui.crackerslib.button.sorting.a-z.tooltip", Comparator.naturalOrder()),
	Z_TO_A("gui.crackerslib.button.sorting.z-a.tooltip", Comparator.reverseOrder());
	
	private final Component name;
	private final Comparator<ConfigListItem> sorter;
	
	private SortType(String translationKey, Comparator<ConfigListItem> sorter)
	{
		this.name = Component.translatable(translationKey);
		this.sorter = sorter;
	}
	
	public Component getName()
	{
		return this.name;
	}
	
	public void sortList(List<ConfigListItem> list)
	{
		list.sort(this.sorter.thenComparing((first, second) -> {
			if (first instanceof ConfigCategory && second instanceof ConfigEntry)
				return 1;
			else if (first instanceof ConfigEntry && second instanceof ConfigCategory)
				return -1;
			else
				return 0;
		}));
	}
}
