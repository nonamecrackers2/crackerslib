package nonamecrackers2.crackerslib.common.config;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.CrackersLib;

public class CrackersLibConfig
{
	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;
	
	static
	{
		var clientPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT = clientPair.getLeft();
		CLIENT_SPEC = clientPair.getRight();
	}
	
	public static class ClientConfig extends ConfigHelper
	{
		public final ForgeConfigSpec.ConfigValue<List<? extends String>> hiddenConfigMenuButtons;
		
		public ClientConfig(ForgeConfigSpec.Builder builder)
		{
			super(CrackersLib.MODID);
			
			this.hiddenConfigMenuButtons = this.createListValue(builder, String.class, () -> {
				return Lists.newArrayList("example_mod_id");
			}, v -> {
				return true;
			}, "hiddenConfigMenuButtons", ReloadType.NONE, "A list of mod ids that cannot have their registered config menu buttons appear in the options screen");
		}
	}
}
