package nonamecrackers2.crackerslib.common.command;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.command.EnumArgument;
import nonamecrackers2.crackerslib.common.command.argument.ConfigArgument;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;

/**
 * Creates config commands for modifying config options in game
 */
public class ConfigCommandBuilder
{
	private final Map<ModConfig.Type, ForgeConfigSpec> specs = Maps.newEnumMap(ModConfig.Type.class);
	private final LiteralArgumentBuilder<CommandSourceStack> argumentBuilder;
	private final CommandDispatcher<CommandSourceStack> dispatcher;
	
	public ConfigCommandBuilder(LiteralArgumentBuilder<CommandSourceStack> argumentBuilder, CommandDispatcher<CommandSourceStack> dispatcher)
	{
		this.argumentBuilder = argumentBuilder;
		this.dispatcher = dispatcher;
	}
	
	public static ConfigCommandBuilder builder(CommandDispatcher<CommandSourceStack> dispatcher, String rootName)
	{
		return new ConfigCommandBuilder(Commands.literal(rootName).requires(src -> src.hasPermission(2)), dispatcher);
	}
	
	public ConfigCommandBuilder addSpec(ModConfig.Type type, ForgeConfigSpec spec)
	{
		if (this.specs.containsKey(type))
			throw new IllegalArgumentException("Spec '" + type + "' already registered.");
		this.specs.put(type, spec);
		return this;
	}
	
	public void register()
	{
		var root = Commands.literal("config");
		for (var entry : this.specs.entrySet())
		{
			ModConfig.Type type = entry.getKey();
			ForgeConfigSpec spec = entry.getValue();
			var specArgument = Commands.literal(type.extension());
			addArgumentsForSpec(spec, specArgument);
			root.then(specArgument);
		}
		this.argumentBuilder.then(root);
		this.dispatcher.register(this.argumentBuilder);
	}
	
	private static void addArgumentsForSpec(ForgeConfigSpec spec, LiteralArgumentBuilder<CommandSourceStack> specArgument)
	{
		Map<String, ForgeConfigSpec.ValueSpec> allValues = ConfigHelper.getAllSpecs(spec);
		var setArg = Commands.literal("set")
				.then(
						Commands.argument("double", ConfigArgument.arg(allValues, Double.class))
						.then(
								Commands.argument("value", DoubleArgumentType.doubleArg())
								.executes(ctx -> set(ctx, "double", DoubleArgumentType::getDouble, spec))
						)
						.then(
								Commands.literal("default")
								.executes(ctx -> setDefault(ctx, "double", spec))
						)
				)
				.then(
						Commands.argument("boolean", ConfigArgument.arg(allValues, Boolean.class))
						.then(
								Commands.argument("value", BoolArgumentType.bool())
								.executes(ctx -> set(ctx, "boolean", BoolArgumentType::getBool, spec))
						)
						.then(
								Commands.literal("default")
								.executes(ctx -> setDefault(ctx, "boolean", spec))
						)
				)
				.then(
						Commands.argument("integer", ConfigArgument.arg(allValues, Integer.class))
						.then(
								Commands.argument("value", IntegerArgumentType.integer())
								.executes(ctx -> set(ctx, "integer", IntegerArgumentType::getInteger, spec))
						)
						.then(
								Commands.literal("default")
								.executes(ctx -> setDefault(ctx, "integer", spec))
						)
				)
				.then(
						Commands.argument("string", ConfigArgument.arg(allValues, String.class))
						.then(
								Commands.argument("value", StringArgumentType.greedyString())
								.executes(ctx -> set(ctx, "string", StringArgumentType::getString, spec))
						)
						.then(
								Commands.literal("default")
								.executes(ctx -> setDefault(ctx, "string", spec))
						)
				);
		//Auto register the command arguments for custom enums (really hacky)
		for (@SuppressWarnings("rawtypes") Class<Enum> clazz : gatherEnumValueClasses(allValues))
		{
			String name = clazz.getSimpleName();
			setArg.then(
					Commands.argument(name, ConfigArgument.arg(allValues, clazz))
					.then(
							Commands.argument("value", EnumArgument.enumArgument(clazz))
							.executes(ctx -> set(ctx, name, (ctx1, arg) -> ctx1.getArgument(arg, clazz), spec))
					)
					.then(
							Commands.literal("default")
							.executes(ctx -> setDefault(ctx, name, spec))
					)
			);
		}
		specArgument.then(
				Commands.literal("get")
				.then(
						Commands.argument("value", ConfigArgument.any(allValues))
						.executes(ctx -> get(ctx, spec))
				)
		);
		specArgument.then(setArg);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<Class<Enum>> gatherEnumValueClasses(Map<String, ForgeConfigSpec.ValueSpec> allValues)
	{
		List<Class<Enum>> list = Lists.newArrayList();
		for (var value : allValues.values())
		{
			Object obj = value.getDefault();
			if (obj instanceof Enum enu && !list.contains(enu.getDeclaringClass()))
				list.add(enu.getDeclaringClass());
		}
		return list;
	}
	
	private static <T> int set(CommandContext<CommandSourceStack> context, String arg, BiFunction<CommandContext<CommandSourceStack>, String, T> valueGetter, ForgeConfigSpec spec) throws CommandSyntaxException
	{
		CommandSourceStack source = context.getSource();
		ForgeConfigSpec.ConfigValue<T> config = ConfigArgument.get(context, arg, spec);
		T value = valueGetter.apply(context, "value");
		ValueSpec valueSpec = spec.getRaw(config.getPath());
		if (!config.get().equals(value) && valueSpec.test(value))
		{
			config.set(value);
			String joinedPath = ConfigHelper.DOT_JOINER.join(config.getPath());
			source.sendSuccess(() -> Component.translatable("commands.crackerslib.setConfig.set.success", joinedPath, value), true);
			if (valueSpec.needsWorldRestart())
			{
				source.sendSuccess(() -> Component.translatable("commands.crackerslib.setConfig.set.note", joinedPath).withStyle(ChatFormatting.GRAY), false);
				return 2;
			}
			else
			{
				return 1;
			}
		}
		else
		{
			source.sendFailure(Component.translatable("commands.crackerslib.setConfig.set.fail"));
			return 0;
		}
	}
	
	private static int get(CommandContext<CommandSourceStack> context, ForgeConfigSpec spec)
	{
		ForgeConfigSpec.ConfigValue<Object> config = ConfigArgument.get(context, "value", spec);
		Object val = config.get();
		context.getSource().sendSuccess(() -> Component.translatable("commands.crackerslib.getConfig.get", ConfigHelper.DOT_JOINER.join(config.getPath()), config.get()), false);
		if (val instanceof Integer integer)
			return integer;
		else if (val instanceof Boolean bool)
			return bool ? 1 : 0;
		else if (val instanceof Double decimal)
			return (int)(decimal * 10.0D);
		else if (val instanceof Enum<?> enu)
			return enu.ordinal();
		else
			return -1;
	}
	
	public static int setDefault(CommandContext<CommandSourceStack> context, String arg, ForgeConfigSpec spec)
	{
		CommandSourceStack source = context.getSource();
		ForgeConfigSpec.ConfigValue<Object> config = ConfigArgument.get(context, arg, spec);
		ValueSpec valueSpec = spec.getRaw(config.getPath());
		if (config.get() != config.getDefault())
		{
			config.set(config.getDefault());
			String name = ConfigHelper.DOT_JOINER.join(config.getPath());
			source.sendSuccess(() -> Component.translatable("commands.crackerslib.setDefault.success", name, config.get()), true);
			if (valueSpec.needsWorldRestart())
			{
				source.sendSuccess(() -> Component.translatable("commands.crackerslib.setConfig.set.note", name).withStyle(ChatFormatting.GRAY), false);
				return 2;
			}
			else
			{
				return 1;
			}
		}
		else
		{
			source.sendFailure(Component.translatable("commands.crackerslib.setConfig.set.fail"));
			return 0;
		}
	}
}
