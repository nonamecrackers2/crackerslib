package nonamecrackers2.crackerslib.common.util.data;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.common.data.LanguageProvider;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;

public class ConfigLangGeneratorHelper
{
	/**
	 * Generates language entries for all registered config options including their titles and descriptions for use
	 * in the config menu.
	 * 
	 * @param modid
	 * @param spec
	 * @param provider
	 * @param removeDefaultInfo Remove the default info in the default config description, if you're using {@link ConfigHelper}
	 */
	public static void langForSpec(String modid, ForgeConfigSpec spec, LanguageProvider provider, boolean removeDefaultInfo)
	{
		forValues(modid, spec.getSpec().valueMap(), provider, removeDefaultInfo);
	}
	
	private static void forValues(String modid, Map<String, Object> values, LanguageProvider provider, boolean removeDefaultInfo)
	{
		for (var entry : values.entrySet())
		{
			String name = entry.getKey();
			if (entry.getValue() instanceof ValueSpec spec)
			{
				String properTitle = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), " "));
				tryAdd("gui." + modid + ".config." + name + ".title", properTitle, provider);
				String desc = spec.getComment();
				if (removeDefaultInfo)
					desc = desc.replaceFirst("\\n.*?(?=\\n)", "");
				tryAdd(spec.getTranslationKey(), desc, provider);
			}
			else if (entry.getValue() instanceof UnmodifiableConfig category)
			{
				String[] split = name.split("_");
				for (int i = 0; i < split.length; i++)
					split[i] = StringUtils.capitalize(split[i]);
				String properTitle = StringUtils.join(split, " ");
				tryAdd("gui." + modid + ".config.category." + name + ".title", properTitle, provider);
				forValues(modid, category.valueMap(), provider, removeDefaultInfo);
			}
		}
	}
	
	private static void tryAdd(String key, String entry, LanguageProvider provider)
	{
		try {
			provider.add(key, entry);
		} catch (IllegalStateException e) {}
	}
}
