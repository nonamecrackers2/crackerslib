package nonamecrackers2.crackerslib.common.init;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.common.command.argument.ConfigArgument;

public class CrackersLibCommandArguments
{
	private static final DeferredRegister<ArgumentTypeInfo<?, ?>> TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, CrackersLib.MODID);
	
	public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ConfigArgument.Serializer> CONFIG_ARGUMENT = TYPES.register("config", () -> ArgumentTypeInfos.registerByClass(ConfigArgument.class, new ConfigArgument.Serializer()));
	
	public static void register(IEventBus modBus)
	{
		TYPES.register(modBus);
	}
}
