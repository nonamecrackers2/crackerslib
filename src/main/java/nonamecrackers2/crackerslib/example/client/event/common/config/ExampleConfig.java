package nonamecrackers2.crackerslib.example.client.event.common.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;
import nonamecrackers2.crackerslib.common.config.ReloadType;

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
	
	public static class ClientConfig extends ConfigHelper
	{
		public final ForgeConfigSpec.ConfigValue<Boolean> exampleBoolean;
		public final ForgeConfigSpec.ConfigValue<Integer> exampleInteger;
		public final ForgeConfigSpec.ConfigValue<Double> exampleDouble;
		public final ForgeConfigSpec.ConfigValue<String> exampleString;
		public final ForgeConfigSpec.ConfigValue<ExampleConfig.ExampleEnum> exampleEnum;
		public final ForgeConfigSpec.ConfigValue<List<? extends String>> exampleListString;
		public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> exampleListInteger;
		public final ForgeConfigSpec.ConfigValue<List<? extends Double>> exampleListDouble;
		
		public ClientConfig(ForgeConfigSpec.Builder builder)
		{
			super(CrackersLib.MODID);
			
			this.exampleBoolean = this.createValue(builder, true, "exampleBoolean", ReloadType.NONE, "A simple boolean config value");
			
			builder.comment("Numbers").push("numbers");
			
			this.exampleInteger = this.createRangedIntValue(builder, 10, 0, 100, "exampleInteger", ReloadType.WORLD, "A simple ranged integer value");
			
			this.exampleDouble = this.createRangedDoubleValue(builder, 0.5D, 0.0D, 1.0D, "exampleDouble", ReloadType.GAME, "A simple ranged value with decimals");
			
			builder.pop();
			
			builder.comment("Extra").push("extra");
			
			this.exampleString = this.createValue(builder, "hello!", "exampleString", ReloadType.NONE, "A simple string value");
			
			builder.pop();
			
			this.exampleEnum = this.createEnumValue(builder, ExampleConfig.ExampleEnum.HEY, "exampleEnum", ReloadType.NONE, "A simple enum config value");
			
			builder.comment("Lists").push("list");
			
			this.exampleListString = this.createListValue(builder, String.class, () -> {
				return Lists.newArrayList("heres", "some", "default", "values");
			}, val -> {
				return StringUtils.isAllLowerCase(val); //Example value validator
			}, "exampleListString", ReloadType.NONE, "An example list of strings that must all be lowercase");
			
			this.exampleListInteger = this.createListValue(builder, Integer.class, () -> {
				return Lists.newArrayList(2, 3, 4, 5);
			}, val -> {
				return val >= 2;
			}, "exampleListInteger", ReloadType.NONE, "An example list of integers that must be greater than or equal to 2");
			
			builder.comment("Category Example").push("category_example");
			
			this.exampleListDouble = this.createListValue(builder, Double.class, () -> {
				return Lists.newArrayList(0.0, 1.0, 2.0, 3.0);
			}, val -> true, "exampleListDouble", ReloadType.NONE, "An example list of doubles");
			
			builder.pop();
			
			builder.pop();
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
