package nonamecrackers2.crackerslib.common.command;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.RestartType;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.command.EnumArgument;
import nonamecrackers2.crackerslib.common.command.argument.ConfigArgument;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;
import nonamecrackers2.crackerslib.common.event.impl.OnConfigOptionSaved;

/**
 * Creates config commands for modifying config options in game
 */
public class ConfigCommandBuilder
{
	private final String modid;
	private final Map<ModConfig.Type, ModConfigSpec> specs = Maps.newEnumMap(ModConfig.Type.class);
	private final LiteralArgumentBuilder<CommandSourceStack> argumentBuilder;
	private final CommandDispatcher<CommandSourceStack> dispatcher;
	
	public ConfigCommandBuilder(String modid, LiteralArgumentBuilder<CommandSourceStack> argumentBuilder, CommandDispatcher<CommandSourceStack> dispatcher)
	{
		this.modid = modid;
		this.argumentBuilder = argumentBuilder;
		this.dispatcher = dispatcher;
	}
	
	public static ConfigCommandBuilder builder(CommandDispatcher<CommandSourceStack> dispatcher, String modid)
	{
		return new ConfigCommandBuilder(modid, Commands.literal(modid), dispatcher);
	}
	
	public ConfigCommandBuilder addSpec(ModConfig.Type type, ModConfigSpec spec)
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
			ModConfigSpec spec = entry.getValue();
			var specArgument = Commands.literal(type.extension());
			if (type != ModConfig.Type.CLIENT)
				specArgument.requires(Commands.hasPermission(Commands.LEVEL_ADMINS));
			addArgumentsForSpec(spec, this.modid, type, specArgument);
			root.then(specArgument);
		}
		this.argumentBuilder.then(root);
		this.dispatcher.register(this.argumentBuilder);
	}
	
	private static void addArgumentsForSpec(ModConfigSpec spec, String modid, ModConfig.Type type, LiteralArgumentBuilder<CommandSourceStack> specArgument)
	{
		Map<String, ModConfigSpec.ValueSpec> allValues = ConfigHelper.getAllSpecs(spec);
		var setArg = Commands.literal("set")
				.then(
						Commands.argument("double", ConfigArgument.arg(allValues, Double.class))
						.then(
								Commands.argument("value", DoubleArgumentType.doubleArg())
								.executes(ctx -> set(ctx, "double", DoubleArgumentType::getDouble, spec, modid, type))
						)
						.then(
								Commands.literal("default")
								.executes(ctx -> setDefault(ctx, "double", spec, modid, type))
						)
				)
				.then(
						Commands.argument("boolean", ConfigArgument.arg(allValues, Boolean.class))
						.then(
								Commands.argument("value", BoolArgumentType.bool())
								.executes(ctx -> set(ctx, "boolean", BoolArgumentType::getBool, spec, modid, type))
						)
						.then(
								Commands.literal("default")
								.executes(ctx -> setDefault(ctx, "boolean", spec, modid, type))
						)
				)
				.then(
						Commands.argument("integer", ConfigArgument.arg(allValues, Integer.class))
						.then(
								Commands.argument("value", IntegerArgumentType.integer())
								.executes(ctx -> set(ctx, "integer", IntegerArgumentType::getInteger, spec, modid, type))
						)
						.then(
								Commands.literal("default")
								.executes(ctx -> setDefault(ctx, "integer", spec, modid, type))
						)
				)
				.then(
						Commands.argument("string", ConfigArgument.arg(allValues, String.class))
						.then(
								Commands.argument("value", StringArgumentType.greedyString())
								.executes(ctx -> set(ctx, "string", StringArgumentType::getString, spec, modid, type))
						)
						.then(
								Commands.literal("default")
								.executes(ctx -> setDefault(ctx, "string", spec, modid, type))
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
							.executes(ctx -> set(ctx, name, (ctx1, arg) -> ctx1.getArgument(arg, clazz), spec, modid, type))
					)
					.then(
							Commands.literal("default")
							.executes(ctx -> setDefault(ctx, name, spec, modid, type))
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
	private static List<Class<Enum>> gatherEnumValueClasses(Map<String, ModConfigSpec.ValueSpec> allValues)
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
	
	private static <T> int set(CommandContext<CommandSourceStack> context, String arg, BiFunction<CommandContext<CommandSourceStack>, String, T> valueGetter, ModConfigSpec spec, String modid, ModConfig.Type type) throws CommandSyntaxException
	{
		CommandSourceStack source = context.getSource();
		ModConfigSpec.ConfigValue<T> config = ConfigArgument.get(context, arg, spec);
		T value = valueGetter.apply(context, "value");
		ValueSpec valueSpec = spec.getSpec().getRaw(config.getPath());
		if (!valueSpec.test(value))
			return 0;
		OnConfigOptionSaved<T> event = new OnConfigOptionSaved<>(modid, type, OnConfigOptionSaved.Source.COMMAND, config, value, !Objects.equals(config.get(), value));
		NeoForge.EVENT_BUS.post(event);
		if (event.getOverrideValue() != null)
			value = event.getOverrideValue();
		if (!Objects.equals(config.get(), value) && valueSpec.test(value))
		{
			config.set(value);
			config.save();
			String joinedPath = ConfigHelper.DOT_JOINER.join(config.getPath());
			Component result = Component.translatable("commands.crackerslib.setConfig.set.success", joinedPath, value.toString());
			source.sendSuccess(() -> result, true);
			if (valueSpec.restartType() != RestartType.NONE)
			{
				source.sendSuccess(() -> Component.translatable("commands.crackerslib.setConfig.set.note", joinedPath, valueSpec.restartType().toString()).withStyle(ChatFormatting.GRAY), false);
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
	
	private static int get(CommandContext<CommandSourceStack> context, ModConfigSpec spec)
	{
		ModConfigSpec.ConfigValue<Object> config = ConfigArgument.get(context, "value", spec);
		Object val = config.get();
		context.getSource().sendSuccess(() -> Component.translatable("commands.crackerslib.getConfig.get", ConfigHelper.DOT_JOINER.join(config.getPath()), config.get().toString()), false);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int setDefault(CommandContext<CommandSourceStack> context, String arg, ModConfigSpec spec, String modid, ModConfig.Type type)
	{
		CommandSourceStack source = context.getSource();
		ModConfigSpec.ConfigValue<Object> config = ConfigArgument.get(context, arg, spec);
		ValueSpec valueSpec = spec.getSpec().getRaw(config.getPath());
		boolean flag = !Objects.equals(config.get(), config.getDefault());
		NeoForge.EVENT_BUS.post(new OnConfigOptionSaved(modid, type, OnConfigOptionSaved.Source.COMMAND, config, config.getDefault(), flag));
		if (flag)
		{
			config.set(config.getDefault());
			config.save();
			String name = ConfigHelper.DOT_JOINER.join(config.getPath());
			source.sendSuccess(() -> Component.translatable("commands.crackerslib.setDefault.success", name, config.get().toString()), true);
			if (valueSpec.restartType() != RestartType.NONE)
			{
				source.sendSuccess(() -> Component.translatable("commands.crackerslib.setConfig.set.note", name, valueSpec.restartType().toString()).withStyle(ChatFormatting.GRAY), false);
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
