package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class IntegerConfigEntry extends NumberConfigEntry<Integer>
{
	public IntegerConfigEntry(Minecraft mc, String modid, ModConfig.Type type, String path, ForgeConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, type, path, spec, onValueUpdated);
	}

	@Override
	protected Integer parseValue(String contents) throws NumberFormatException
	{
		return Integer.parseInt(contents);
	}
}
