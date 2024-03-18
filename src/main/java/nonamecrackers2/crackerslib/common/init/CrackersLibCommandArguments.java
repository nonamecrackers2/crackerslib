package nonamecrackers2.crackerslib.common.init;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.common.command.argument.ConfigArgument;

public class CrackersLibCommandArguments
{
	private static final DeferredRegister<ArgumentTypeInfo<?, ?>> TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, CrackersLib.MODID);
	
	public static final RegistryObject<ConfigArgument.Serializer> CONFIG_ARGUMENT = TYPES.register("config", () -> ArgumentTypeInfos.registerByClass(ConfigArgument.class, new ConfigArgument.Serializer()));
	
	public static void register(IEventBus modBus)
	{
		TYPES.register(modBus);
	}
}
