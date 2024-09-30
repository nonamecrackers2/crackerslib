package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class LongConfigEntry extends NumberConfigEntry<Long>
{
	public LongConfigEntry(Minecraft mc, String modid, ModConfig.Type type, String path, ModConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, type, path, spec, onValueUpdated);
	}
	
	@Override
	protected Long parseValue(String contents) throws NumberFormatException
	{
		return Long.parseLong(contents);
	}
}
