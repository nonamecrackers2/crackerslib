package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.common.ModConfigSpec;

public class DoubleConfigEntry extends NumberConfigEntry<Double>
{
	public DoubleConfigEntry(Minecraft mc, String modid, String path, ModConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, path, spec, onValueUpdated);
	}
	
	@Override
	protected Double parseValue(String contents) throws NumberFormatException
	{
		return Double.parseDouble(contents);
	}
}
