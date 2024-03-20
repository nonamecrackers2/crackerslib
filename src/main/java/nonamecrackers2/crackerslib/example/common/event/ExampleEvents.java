package nonamecrackers2.crackerslib.example.common.event;

import com.google.common.collect.Lists;

import net.minecraft.network.chat.Component;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.common.command.ConfigCommandBuilder;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPreset;
import nonamecrackers2.crackerslib.common.config.preset.RegisterConfigPresetsEvent;
import nonamecrackers2.crackerslib.example.client.event.common.config.ExampleConfig;

public class ExampleEvents
{
	public static void registerPresetsEvent(RegisterConfigPresetsEvent event)
	{
		event.exclude(ExampleConfig.CLIENT.exampleListInteger);
		event.registerPreset(ModConfig.Type.SERVER, ConfigPreset.builder(Component.literal("Another Example"))
				.setDescription(Component.literal("Just another epic example preset"))
				.setPreset(ExampleConfig.CLIENT.exampleEnum, ExampleConfig.ExampleEnum.GOING)
				.build()
		);
		event.registerPreset(ModConfig.Type.SERVER, ConfigPreset.builder(Component.literal("Example"))
				.setDescription(Component.literal("Just an example preset"))
				.setPreset(ExampleConfig.CLIENT.exampleBoolean, false)
				.setPreset(ExampleConfig.CLIENT.exampleDouble, 0.5D)
				.setPreset(ExampleConfig.CLIENT.exampleInteger, 90)
				.setPreset(ExampleConfig.CLIENT.exampleString, "Test preset FTW!")
				.setPreset(ExampleConfig.CLIENT.exampleListDouble, Lists.newArrayList(100.0D, 110.0D, 120.0D))
				.build()
		);
	}
	
	@SubscribeEvent
	public static void onServerStarted(ServerStartedEvent event)
	{
		ConfigCommandBuilder.builder(event.getServer().getCommands().getDispatcher(), "crackerslib").addSpec(ModConfig.Type.SERVER, ExampleConfig.CLIENT_SPEC).register();
	}
}
