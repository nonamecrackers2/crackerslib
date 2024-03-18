package nonamecrackers2.crackerslib.example.common.event;

import com.google.common.collect.Lists;

import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.common.command.ConfigCommandBuilder;
import nonamecrackers2.crackerslib.common.config.CrackersLibConfig;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;
import nonamecrackers2.crackerslib.common.event.impl.RegisterConfigPresetsEvent;
import nonamecrackers2.crackerslib.example.client.event.common.config.ExampleConfig;

public class ExampleEvents
{
	public static void registerPresetsEvent(RegisterConfigPresetsEvent event)
	{
		event.registerPreset(ModConfig.Type.CLIENT, ConfigPreset.builder(Component.literal("Test"))
				.setDescription(Component.literal("Just a test preset"))
				.setPreset(ExampleConfig.CLIENT.exampleBoolean, false)
				.setPreset(ExampleConfig.CLIENT.exampleDouble, 0.5D)
				.setPreset(ExampleConfig.CLIENT.exampleInteger, 90)
				.setPreset(ExampleConfig.CLIENT.exampleString, "Test preset FTW!")
				.setPreset(ExampleConfig.CLIENT.exampleListDouble, Lists.newArrayList(100.0D, 110.0D, 120.0D))
				.build()
		);
	}
	
	@SubscribeEvent
	public static void reigsterCommandsEvent(RegisterCommandsEvent event)
	{
		ConfigCommandBuilder.builder(event.getDispatcher(), "crackerslib").addSpec(ModConfig.Type.COMMON, ExampleConfig.CLIENT_SPEC).addSpec(ModConfig.Type.CLIENT, CrackersLibConfig.CLIENT_SPEC).register();
	}
}
