package nonamecrackers2.crackerslib;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import nonamecrackers2.crackerslib.client.event.CrackersLibClientEvents;
import nonamecrackers2.crackerslib.client.event.impl.RegisterConfigScreensEvent;
import nonamecrackers2.crackerslib.client.gui.ConfigMenuButtons;
import nonamecrackers2.crackerslib.common.compat.CompatHelper;
import nonamecrackers2.crackerslib.common.config.CrackersLibConfig;
import nonamecrackers2.crackerslib.common.config.preset.ConfigPresets;
import nonamecrackers2.crackerslib.common.event.CrackersLibDataEvents;
import nonamecrackers2.crackerslib.common.init.CrackersLibCommandArguments;
import nonamecrackers2.crackerslib.example.client.event.ExampleClientEvents;
import nonamecrackers2.crackerslib.example.common.config.ExampleConfig;
import nonamecrackers2.crackerslib.example.common.event.ExampleEvents;

@Mod(CrackersLib.MODID)
public class CrackersLib
{
	public static final String MODID = "crackerslib";
	
	public CrackersLib()
	{
		ModContainer container = ModLoadingContext.get().getActiveContainer();
		IEventBus modBus = container.getEventBus();
		modBus.addListener(this::commonSetup);
		modBus.addListener(this::clientSetup);
		modBus.addListener(CrackersLibDataEvents::gatherClientData);
		modBus.addListener(CrackersLibClientEvents::registerPictureInPictureRenderersEvent);
		container.registerConfig(ModConfig.Type.CLIENT, CrackersLibConfig.CLIENT_SPEC);
		container.registerConfig(ModConfig.Type.SERVER, ExampleConfig.SERVER_SPEC);
		CrackersLibCommandArguments.register(modBus);
	}
	
	public void clientSetup(final FMLClientSetupEvent event)
	{
		IEventBus modBus = ModLoadingContext.get().getActiveContainer().getEventBus();
		modBus.addListener(CrackersLibClientEvents::registerConfigScreen);
		modBus.addListener(ExampleClientEvents::registerConfigMenuButton);
		IEventBus forgeBus = NeoForge.EVENT_BUS;
		forgeBus.register(CrackersLibClientEvents.class);
//		forgeBus.register(ExampleClientEvents.class);
		event.enqueueWork(() -> {
			ModLoader.runEventGenerator(mod -> {
				return new RegisterConfigScreensEvent(mod.getModId());
			});
			ConfigMenuButtons.gatherButtonFactories();
		});
	}
	
	public void commonSetup(final FMLCommonSetupEvent event)
	{
		IEventBus modBus = ModLoadingContext.get().getActiveContainer().getEventBus();
		modBus.addListener(ExampleEvents::registerPresetsEvent);
		IEventBus forgeBus = NeoForge.EVENT_BUS;
		forgeBus.addListener(ExampleEvents::registerCommands);
		event.enqueueWork(() -> {
			ConfigPresets.gatherPresets();
			CompatHelper.checkForLoaded();
		});
	}
	
	public static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}
