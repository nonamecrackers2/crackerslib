package nonamecrackers2.crackerslib.common.command.argument;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigArgument implements ArgumentType<String>
{
	private static final DynamicCommandExceptionType INVALID_VALUE = new DynamicCommandExceptionType(o -> {
		return Component.translatable("argument.crackerslib.config.invalidValue", o);
	});
	private final List<String> availableOptions;
	
	public ConfigArgument(List<String> availableOptions)
	{
		this.availableOptions = availableOptions;
	}
	
	protected List<String> getAvailableOptions()
	{
		return this.availableOptions;
	}
	
	@Override
	public String parse(StringReader reader) throws CommandSyntaxException
	{
		String name = reader.readUnquotedString();
		for (String option : this.getAvailableOptions())
		{
			if (option.contains(name) || option.equals(name))
				return option;
		}
		throw INVALID_VALUE.create(name);
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		return SharedSuggestionProvider.suggest(this.getAvailableOptions(), builder);
	}
	
	public static <T> ForgeConfigSpec.ConfigValue<T> get(CommandContext<CommandSourceStack> context, String argName, ForgeConfigSpec spec)
	{
		String path = context.getArgument(argName, String.class);
		return spec.getValues().get(path);
	}
	
	public static ConfigArgument arg(Map<String, ForgeConfigSpec.ValueSpec> allValues, Class<?> arg)
	{
		return new ConfigArgument(allValues.entrySet().stream().filter(e -> {
			return e.getValue().getDefault() instanceof Enum<?> enub ? enub.getDeclaringClass().isAssignableFrom(arg) : e.getValue().getDefault().getClass().isAssignableFrom(arg);
		}).map(Map.Entry::getKey).toList());
	}
	
	public static ConfigArgument any(Map<String, ForgeConfigSpec.ValueSpec> allValues)
	{
		return new ConfigArgument(allValues.entrySet().stream().map(Map.Entry::getKey).toList());
	}
	
	public static class Serializer implements ArgumentTypeInfo<ConfigArgument, ConfigArgument.Serializer.Template>
	{
		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer)
		{
			buffer.writeCollection(template.availableOptions, FriendlyByteBuf::writeUtf);
		}

		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf buffer)
		{
			return new Template(buffer.readList(FriendlyByteBuf::readUtf));
		}

		@Override
		public void serializeToJson(Template template, JsonObject object)
		{
			JsonArray array = new JsonArray();
			for (var option : template.availableOptions)
				array.add(option);
			object.add("available_options", array);
		}
		
		@Override
		public Template unpack(ConfigArgument argument)
		{
			return new Template(argument.getAvailableOptions());
		}
		
		public final class Template implements ArgumentTypeInfo.Template<ConfigArgument>
		{
			public final List<String> availableOptions;
			
			private Template(List<String> availableOptions)
			{
				this.availableOptions = availableOptions;
			}
			
			@Override
			public ConfigArgument instantiate(CommandBuildContext context)
			{
				return new ConfigArgument(this.availableOptions);
			}
			
			@Override
			public ArgumentTypeInfo<ConfigArgument, ?> type()
			{
				return Serializer.this;
			}
		}
	}
}
