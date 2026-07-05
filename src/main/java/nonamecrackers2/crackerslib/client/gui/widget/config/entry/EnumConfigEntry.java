package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class EnumConfigEntry<T extends Enum<T>> extends ConfigEntry<T, CycleButton<T>>
{
	private final Class<T> enumClass;
	
	public EnumConfigEntry(Minecraft mc, String modid, ModConfig.Type type, String path, ModConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, type, path, spec, onValueUpdated);
		this.enumClass = this.value.getDefault().getDeclaringClass();
	}

	@Override
	protected CycleButton<T> buildWidget(int x, int y, int width, int height)
	{
		return CycleButton.<T>builder(e -> Component.literal(e.toString()), this.value.get())
				.displayOnlyValue()
				.withValues(Arrays.stream(this.enumClass.getEnumConstants()).filter(v -> this.valueSpec.test(v)).toList())
				.create(x + 6, y, 100, 20, this.name, (_, _) -> {
					this.getValueUpdatedResponder().run();
				});
	}

	@Override
	protected T getCurrentValue()
	{
		return this.widget.getValue();
	}
	
	@Override
	protected void setCurrentValue(T value)
	{
		this.widget.setValue(value);
	}
}
