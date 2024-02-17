package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class NumberConfigEntry<T extends Number> extends ConfigEntry<T, EditBox>
{
	public NumberConfigEntry(Minecraft mc, String modid, String path, ForgeConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, path, spec, onValueUpdated);
	}

	@Override
	protected EditBox buildWidget(int x, int y, int width, int height)
	{
		EditBox box = new EditBox(this.mc.font, x + 6, y + height / 2 - 10, 60, 20, CommonComponents.EMPTY);
		box.setValue(String.valueOf(this.value.get()));
		box.setResponder(value -> {
			try
			{
				this.getValueUpdatedResponder().run();
				var val = this.parseValue(value);
				if (this.valueSpec.test(val))//ConfigHolder.isValid(this.modid, this.value, val))
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
	
	@Override
	protected T getCurrentValue()
	{
		try {
			return this.parseValue(this.widget.getValue());
		} catch (NumberFormatException e) {
			return this.value.get();
		}
	}
	
	@Override
	protected void setCurrentValue(T value)
	{
		this.widget.setValue(String.valueOf(value));
	}
	
	protected abstract T parseValue(String contents) throws NumberFormatException;
}
