package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.client.gui.widget.CyclableButton;

public class BooleanConfigEntry extends ConfigEntry<Boolean, CyclableButton<Boolean>>
{
	public BooleanConfigEntry(Minecraft mc, String modid, ForgeConfigSpec.ConfigValue<Boolean> value, Runnable responder)
	{
		super(mc, modid, value, responder);
	}

	@Override
	protected CyclableButton<Boolean> buildWidget(int x, int y, int width, int height)
	{
		var button = new CyclableButton<>(x + 6, y, 60, Lists.newArrayList(Boolean.FALSE, Boolean.TRUE), this.value.get(), value ->
		{
			Component message;
			if (value)
				message = Component.literal("ON").withStyle(ChatFormatting.GREEN);
			else
				message = Component.literal("OFF").withStyle(ChatFormatting.RED);
			return message;
		});
		button.setResponder(val -> this.getValueUpdatedResponder().run());
		return button;
	}
	
	@Override
	protected Boolean getCurrentValue()
	{
		return this.widget.getValue();
	}
	
	@Override
	protected void setCurrentValue(Boolean value)
	{
		this.widget.setValue(value);
	}
}
