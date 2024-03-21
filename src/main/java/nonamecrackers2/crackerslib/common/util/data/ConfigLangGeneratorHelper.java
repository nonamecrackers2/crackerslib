package nonamecrackers2.crackerslib.common.util.data;

import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.common.data.LanguageProvider;

public class ConfigLangGeneratorHelper
{
	/**
	 * Generates lang entries for config values, using its <b>camel case</b> name and config comment defined using a
	 * {@link ForgeConfigSpec.Builder} for use in the config menu system.
	 * 
	 * Use in conjunction with {@link LanguageProvider}
	 * 
	 * @param modid
	 * @param spec
	 * @param provider
	 * @param infoType Used to control what extra information will appear in the configs description. Typically, Forge appends
	 * extra info such as a config values range, allowed values, etc. as new lines on to a config comment, 
	 * which typically isn't always wanted in the description.
	 */
	public static void langForSpec(String modid, ForgeConfigSpec spec, LanguageProvider provider, ConfigLangGeneratorHelper.Info infoType)
	{
		forValues(modid, spec.getSpec().valueMap(), provider, infoType);
	}
	
	/**
	 * Generates lang entries for config values, using its <b>camel case</b> name and config comment defined using a
	 * {@link ForgeConfigSpec.Builder} for use in the config menu system.
	 * 
	 * Use in conjunction with {@link LanguageProvider}
	 * 
	 * @param modid
	 * @param spec
	 * @param provider
	 */
	public static void langForSpec(String modid, ForgeConfigSpec spec, LanguageProvider provider)
	{
		langForSpec(modid, spec, provider, ConfigLangGeneratorHelper.Info.ALL);
	}
	
	private static void forValues(String modid, Map<String, Object> values, LanguageProvider provider, ConfigLangGeneratorHelper.Info infoType)
	{
		for (var entry : values.entrySet())
		{
			String name = entry.getKey();
			if (entry.getValue() instanceof ValueSpec spec)
			{
				String properTitle = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), " "));
				tryAdd("gui." + modid + ".config." + name + ".title", properTitle, provider);
				String desc = spec.getComment();
				String finalDesc = desc;
				Pattern pattern = infoType.getPattern();
				if (pattern != null)
					finalDesc = pattern.matcher(desc).replaceAll("");
				tryAdd(spec.getTranslationKey(), finalDesc, provider);
			}
			else if (entry.getValue() instanceof UnmodifiableConfig category)
			{
				String[] split = name.split("_");
				for (int i = 0; i < split.length; i++)
					split[i] = StringUtils.capitalize(split[i]);
				String properTitle = StringUtils.join(split, " ");
				tryAdd("gui." + modid + ".config.category." + name + ".title", properTitle, provider);
				forValues(modid, category.valueMap(), provider, infoType);
			}
		}
	}
	
	private static void tryAdd(String key, String entry, LanguageProvider provider)
	{
		try {
			provider.add(key, entry);
		} catch (IllegalStateException e) {}
	}
	
	public static enum Info
	{
		ALL(null),
		ONLY_RANGE("(?!\\nRange)\\n.*?(?=\\nRange|$)"),
		NONE_EXTRA("\\n.*");
		
		private final Pattern pattern;
		
		private Info(@Nullable String regex)
		{
			if (regex != null)
				this.pattern = Pattern.compile(regex, Pattern.MULTILINE);
			else
				this.pattern = null;
		}
		
		public @Nullable Pattern getPattern()
		{
			return this.pattern;
		}
	}
}
