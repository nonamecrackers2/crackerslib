package nonamecrackers2.crackerslib.common.test;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.common.config.ConfigHolder;
import nonamecrackers2.crackerslib.common.config.ReloadType;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;
import nonamecrackers2.crackerslib.common.event.RegisterConfigHoldersEvent;

public class ExampleConfig
{
	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;
	
	static
	{
		var clientPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT = clientPair.getLeft();
		CLIENT_SPEC = clientPair.getRight();
	}
	
	public static void registerConfig(RegisterConfigHoldersEvent event)
	{
		event.registerConfig(CrackersLib.MODID, CLIENT_SPEC, CLIENT);
	}
	
	public static class ClientConfig extends ConfigHolder
	{
		public final ForgeConfigSpec.ConfigValue<Boolean> exampleBoolean;
		public final ForgeConfigSpec.ConfigValue<Integer> exampleInteger;
		public final ForgeConfigSpec.ConfigValue<Double> exampleDouble;
		public final ForgeConfigSpec.ConfigValue<ExampleConfig.ExampleEnum> exampleEnum;
		public final ForgeConfigSpec.ConfigValue<List<? extends String>> exampleListString;
		
		public ClientConfig(ForgeConfigSpec.Builder builder)
		{
			super(ModConfig.Type.CLIENT, CrackersLib.MODID);
			
			builder.comment("Example Client Config").push("client");
			
			this.exampleBoolean = this.createValue(builder, true, "exampleBoolean", ReloadType.NONE, "A simple boolean config value");
			
			this.exampleInteger = this.createRangedIntValue(builder, 10, 0, 100, "exampleInteger", ReloadType.WORLD, "A simple ranged integer value");
			
			this.exampleDouble = this.createRangedDoubleValue(builder, 0.5D, 0.0D, 1.0D, "exampleDouble", ReloadType.GAME, "A simple ranged value with decimals");
			
			this.exampleEnum = this.createEnumValue(builder, ExampleConfig.ExampleEnum.HEY, "exampleEnum", ReloadType.NONE, "A simple enum config value");
			
			this.exampleListString = this.createListValue(builder, String.class, () -> {
				return Lists.newArrayList("heres", "some", "default", "values");
			}, val -> {
				return StringUtils.isAllLowerCase(val); //Example value validator
			}, "exampleListString", ReloadType.NONE, "An example of a list of strings that must all be lowercase");
			
			builder.pop();
			
			ConfigPreset examplePreset = ConfigPreset.Builder.of(this)
					.setConfigPreset(this.exampleEnum, ExampleConfig.ExampleEnum.GOING)
					.setConfigPreset(this.exampleDouble, 1.0D)
					.setConfigPreset(this.exampleBoolean, true)
					.build(Component.literal("Example Preset"), CrackersLib.id("example"))
					.withDescription(Component.literal("A simple example preset"));
			this.putPresets(examplePreset);
			
			this.makeDefaultPreset();
		}
	}
	
	private static enum ExampleEnum
	{
		HEY,
		HOWS,
		IT,
		GOING;
	}
}
