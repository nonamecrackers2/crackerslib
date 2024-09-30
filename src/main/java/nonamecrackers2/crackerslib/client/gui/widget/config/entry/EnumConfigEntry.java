package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import nonamecrackers2.crackerslib.client.gui.widget.CyclableButton;

public class EnumConfigEntry<T extends Enum<T>> extends ConfigEntry<T, CyclableButton<T>>
{
	private final Class<T> enumClass;
	
	public EnumConfigEntry(Minecraft mc, String modid, ModConfig.Type type, String path, ModConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, type, path, spec, onValueUpdated);
		this.enumClass = this.value.getDefault().getDeclaringClass();
	}

	@Override
	protected CyclableButton<T> buildWidget(int x, int y, int width, int height)
	{
		var button = new CyclableButton<>(x + 6, y, 100, Arrays.stream(this.enumClass.getEnumConstants()).filter(v -> this.valueSpec.test(v)).toList(), this.value.get());
		button.setResponder(val -> this.getValueUpdatedResponder().run());
		return button;
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
