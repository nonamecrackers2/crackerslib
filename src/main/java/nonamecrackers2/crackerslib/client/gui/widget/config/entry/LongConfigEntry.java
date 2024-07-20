package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class LongConfigEntry extends NumberConfigEntry<Long>
{
	public LongConfigEntry(Minecraft mc, String modid, ModConfig.Type type, String path, ForgeConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, type, path, spec, onValueUpdated);
	}
	
	@Override
	protected Long parseValue(String contents) throws NumberFormatException
	{
		return Long.parseLong(contents);
	}
}
