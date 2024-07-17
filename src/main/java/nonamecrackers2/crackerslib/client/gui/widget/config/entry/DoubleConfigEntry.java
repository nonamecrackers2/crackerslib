package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class DoubleConfigEntry extends NumberConfigEntry<Double>
{
	public DoubleConfigEntry(Minecraft mc, String modid, ModConfig.Type type, String path, ForgeConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, type, path, spec, onValueUpdated);
	}
	
	@Override
	protected Double parseValue(String contents) throws NumberFormatException
	{
		return Double.parseDouble(contents);
	}
}
