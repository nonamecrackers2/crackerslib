package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;

public class IntegerConfigEntry extends NumberConfigEntry<Integer>
{
	public IntegerConfigEntry(Minecraft mc, String modid, ForgeConfigSpec.ConfigValue<Integer> value, Runnable responder)
	{
		super(mc, modid, value, responder);
	}

	@Override
	protected Integer parseValue(String contents) throws NumberFormatException
	{
		return Integer.parseInt(contents);
	}
}
