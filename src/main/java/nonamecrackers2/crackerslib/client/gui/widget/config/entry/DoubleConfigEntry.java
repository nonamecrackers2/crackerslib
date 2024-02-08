package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;

public class DoubleConfigEntry extends NumberConfigEntry<Double>
{
	public DoubleConfigEntry(Minecraft mc, String modid, ForgeConfigSpec.ConfigValue<Double> value, Runnable responder)
	{
		super(mc, modid, value, responder);
	}
	
	@Override
	protected Double parseValue(String contents) throws NumberFormatException
	{
		return Double.parseDouble(contents);
	}
}
