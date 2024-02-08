package nonamecrackers2.crackerslib;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nonamecrackers2.crackerslib.common.config.ConfigHolder;
import nonamecrackers2.crackerslib.common.test.ExampleConfig;

@Mod(CrackersLib.MODID)
public class CrackersLib
{
	public static final String MODID = "crackerslib";
	
	public CrackersLib()
	{
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(ExampleConfig::registerConfig);
		modBus.addListener(this::commonSetup);
		ModLoadingContext context = ModLoadingContext.get();
		context.registerConfig(ModConfig.Type.CLIENT, ExampleConfig.CLIENT_SPEC);
	}
	
	public void commonSetup(final FMLCommonSetupEvent event)
	{
		event.enqueueWork(() -> {
			ConfigHolder.initiateConfigHolders();
		});
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MODID, path);
	}
}
