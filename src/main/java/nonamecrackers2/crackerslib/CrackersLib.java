package nonamecrackers2.crackerslib;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nonamecrackers2.crackerslib.client.event.CrackersLibClientEvents;
import nonamecrackers2.crackerslib.client.event.impl.RegisterConfigScreensEvent;
import nonamecrackers2.crackerslib.client.gui.ConfigMenuButtons;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPresets;
import nonamecrackers2.crackerslib.common.event.ExampleEvents;
import nonamecrackers2.crackerslib.example.client.event.ExampleClientEvents;
import nonamecrackers2.crackerslib.example.client.event.common.config.ExampleConfig;

@Mod(CrackersLib.MODID)
public class CrackersLib
{
	public static final String MODID = "crackerslib";
	
	public CrackersLib()
	{
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
//		modBus.addListener(ExampleConfig::registerConfig);
		modBus.addListener(this::commonSetup);
		modBus.addListener(this::clientSetup);
		ModLoadingContext context = ModLoadingContext.get();
		context.registerConfig(ModConfig.Type.CLIENT, ExampleConfig.CLIENT_SPEC);
	}
	
	public void clientSetup(final FMLClientSetupEvent event)
	{
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(ExampleClientEvents::registerConfigScreen);
		modBus.addListener(ExampleClientEvents::registerConfigMenuButton);
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.register(ExampleClientEvents.class);
		forgeBus.register(CrackersLibClientEvents.class);
		event.enqueueWork(() -> {
			ModLoader.get().runEventGenerator(mod -> {
				return new RegisterConfigScreensEvent(mod.getModId());
			});
			ConfigMenuButtons.gatherButtonFactories();
		});
	}
	
	public void commonSetup(final FMLCommonSetupEvent event)
	{
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(ExampleEvents::registerPresetsEvent);
		event.enqueueWork(() -> {
			ConfigPresets.gatherPresets();
		});
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MODID, path);
	}
}
