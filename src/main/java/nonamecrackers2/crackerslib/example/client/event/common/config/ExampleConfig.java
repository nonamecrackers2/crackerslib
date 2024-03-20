package nonamecrackers2.crackerslib.example.client.event.common.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;

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
			super(builder, CrackersLib.MODID);
			
			this.exampleBoolean = this.createValue(true, "exampleBoolean", false, "A simple boolean config value");
			
			builder.comment("Numbers").push("numbers");
			
			this.exampleInteger = this.createRangedIntValue(10, 0, 100, "exampleInteger", true, "A simple ranged integer value");
			
			this.exampleDouble = this.createRangedDoubleValue(0.5D, 0.0D, 1.0D, "exampleDouble", true, "A simple ranged value with decimals");
			
			builder.pop();
			
			builder.comment("Extra").push("extra");
			
			this.exampleString = this.createValue("hello!", "exampleString", false, "A simple string value");
			
			builder.pop();
			
			this.exampleEnum = this.createEnumValue(ExampleConfig.ExampleEnum.HEY, "exampleEnum", false, "A simple enum config value");
			
			builder.comment("Lists").push("list");
			
			this.exampleListString = this.createListValue(String.class, () -> {
				return Lists.newArrayList("heres", "some", "default", "values");
			}, val -> {
				return StringUtils.isAllLowerCase(val); //Example value validator
			}, "exampleListString", false, "An example list of strings that must all be lowercase");
			
			this.exampleListInteger = this.createListValue(Integer.class, () -> {
				return Lists.newArrayList(2, 3, 4, 5);
			}, val -> {
				return val >= 2;
			}, "exampleListInteger", false, "An example list of integers that must be greater than or equal to 2");
			
			builder.comment("Category Example").push("category_example");
			
			this.exampleListDouble = this.createListValue(Double.class, () -> {
				return Lists.newArrayList(0.0, 1.0, 2.0, 3.0);
			}, val -> true, "exampleListDouble", false, "An example list of doubles");
			
			builder.pop();
			
			builder.pop();
		}
	}
	
	public static enum ExampleEnum
	{
		HEY,
		HOWS,
		IT,
		GOING;
	}
}
