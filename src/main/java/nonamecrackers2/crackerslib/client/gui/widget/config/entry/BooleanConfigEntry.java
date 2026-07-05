package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BooleanConfigEntry extends ConfigEntry<Boolean, CycleButton<Boolean>>
{
	private static final List<Boolean> BOOLEAN_OPTIONS = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
	private static final Component ON = CommonComponents.OPTION_ON.copy().withStyle(ChatFormatting.GREEN);
	private static final Component OFF = CommonComponents.OPTION_OFF.copy().withStyle(ChatFormatting.RED);
	
	public BooleanConfigEntry(Minecraft mc, String modid, ModConfig.Type type, String path, ModConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, type, path, spec, onValueUpdated);
	}

	@Override
	protected CycleButton<Boolean> buildWidget(int x, int y, int width, int height)
	{
		return CycleButton.builder(b -> b == Boolean.TRUE ? ON : OFF, this.value.get())
				.withValues(BOOLEAN_OPTIONS)
				.displayOnlyValue()
				.create(x + 6, y, 60, 20, this.name, (_, _) -> {
			this.getValueUpdatedResponder().run();
		});
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
