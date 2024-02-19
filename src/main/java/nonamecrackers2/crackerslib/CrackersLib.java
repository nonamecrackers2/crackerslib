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
import nonamecrackers2.crackerslib.common.config.CrackersLibConfig;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPresets;

@Mod(CrackersLib.MODID)
public class CrackersLib
{
	public static final String MODID = "crackerslib";
	
	public CrackersLib()
	{
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::commonSetup);
		modBus.addListener(this::clientSetup);
		ModLoadingContext context = ModLoadingContext.get();
		context.registerConfig(ModConfig.Type.CLIENT, CrackersLibConfig.CLIENT_SPEC);
	}
	
	public void clientSetup(final FMLClientSetupEvent event)
	{
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(CrackersLibClientEvents::registerConfigScreen);
		modBus.addListener(CrackersLibClientEvents::registerConfigMenuButton);
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
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
		event.enqueueWork(() -> {
			ConfigPresets.gatherPresets();
		});
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MODID, path);
	}
}
