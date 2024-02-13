package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.client.gui.widget.CyclableButton;

public class EnumConfigEntry<T extends Enum<T>> extends ConfigEntry<T, CyclableButton<T>>
{
	private final Class<T> enumClass;
	
	public EnumConfigEntry(Minecraft mc, String modid, String path, ForgeConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, path, spec, onValueUpdated);
		this.enumClass = this.value.getDefault().getDeclaringClass();
	}

	@Override
	protected CyclableButton<T> buildWidget(int x, int y, int width, int height)
	{
		var button = new CyclableButton<>(x + 6, y, 100, Arrays.asList(this.enumClass.getEnumConstants()), this.value.get());
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
