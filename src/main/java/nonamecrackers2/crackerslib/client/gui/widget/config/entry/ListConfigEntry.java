package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class ListConfigEntry extends ConfigEntry<List<?>, EditBox>
{
	protected static final String PATH_SPLITTER = ", ";
	protected static final Joiner JOINER = Joiner.on(PATH_SPLITTER);
	protected static final Splitter SPLITTER = Splitter.on(PATH_SPLITTER);
	private final ListConfigEntry.ValueParser<?> parser;
	
	public ListConfigEntry(Minecraft mc, String modid, String path, ForgeConfigSpec spec, Runnable onValueUpdated, ListConfigEntry.ValueParser<?> parser)
	{
		super(mc, modid, path, spec, onValueUpdated);
		this.parser = parser;
	}
	
	@Override
	protected EditBox buildWidget(int x, int y, int width, int height)
	{
		EditBox box = new EditBox(this.mc.font, x + 6, y + height / 2 - 10, 200, 20, CommonComponents.EMPTY);
		if (this.value.getDefault().size() > 0)
			box.setHint(Component.literal(String.valueOf(this.value.getDefault().get(0))).withStyle(ChatFormatting.DARK_GRAY));
		box.setMaxLength(500);
		box.setValue(this.compileListToString(this.value.get()));
		box.setResponder(value -> {
			try
			{
				this.getValueUpdatedResponder().run();
				var val = this.compileValuesFromString(value);
				if (this.valueSpec.test(val))
					this.widget.setTextColor(0xFFFFFFFF);
				else
					this.widget.setTextColor(ChatFormatting.RED.getColor());
			}
			catch (NumberFormatException e)
			{
				this.widget.setTextColor(ChatFormatting.RED.getColor());
			}
		});
		return box;
	}
	
	protected String compileListToString(List<?> values)
	{
		return JOINER.join(values);
	}
	
	protected List<?> compileValuesFromString(String values) throws NumberFormatException
	{
		List<Object> valuesList = Lists.newArrayList();
		for (String val : SPLITTER.split(values))
			valuesList.add(this.parser.parse(val));
		return valuesList;
	}
	
	@Override
	protected List<?> getCurrentValue()
	{
		try {
			return this.compileValuesFromString(this.widget.getValue());
		} catch (NumberFormatException e) {
			return this.value.get();
		}
	}

	@Override
	protected void setCurrentValue(List<?> value)
	{
		this.widget.setValue(this.compileListToString(value));
	}
	
	@FunctionalInterface
	public static interface ValueParser<T>
	{
		T parse(String val) throws NumberFormatException;
	}
}
