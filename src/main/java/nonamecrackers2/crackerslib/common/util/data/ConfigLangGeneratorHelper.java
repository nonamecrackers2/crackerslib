package nonamecrackers2.crackerslib.common.util.data;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.common.data.LanguageProvider;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;

public class ConfigLangGeneratorHelper
{
	private static final Logger LOGGER = LogManager.getLogger("crackerslib/ConfigLangGeneratorHelper");
	
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
		int totalIgnored = forValues(modid, spec.getSpec().valueMap(), provider, removeDefaultInfo);
		if (totalIgnored > 0)
			LOGGER.info("Ignored {} entry(s) as they were already defined", totalIgnored);
	}
	
	private static int forValues(String modid, Map<String, Object> values, LanguageProvider provider, boolean removeDefaultInfo)
	{
		int totalIgnored = 0;
		for (var entry : values.entrySet())
		{
			String name = entry.getKey();
			if (entry.getValue() instanceof ValueSpec spec)
			{
				String properTitle = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), " "));
				totalIgnored += tryAdd("gui." + modid + ".config." + name + ".title", properTitle, provider);
				String desc = spec.getComment();
				if (removeDefaultInfo)
					desc = desc.replaceFirst("\\n.*?(?=\\n)", "");
				totalIgnored += tryAdd(spec.getTranslationKey(), desc, provider);
			}
			else if (entry.getValue() instanceof UnmodifiableConfig category)
			{
				String[] split = name.split("_");
				for (int i = 0; i < split.length; i++)
					split[i] = StringUtils.capitalize(split[i]);
				String properTitle = StringUtils.join(split, " ");
				totalIgnored += tryAdd("gui." + modid + ".config.category." + name + ".title", properTitle, provider);
				totalIgnored += forValues(modid, category.valueMap(), provider, removeDefaultInfo);
			}
		}
		return totalIgnored;
	}
	
	private static int tryAdd(String key, String entry, LanguageProvider provider)
	{
		try {
			provider.add(key, entry);
			return 0;
		} catch (IllegalStateException e) {
			return 1;
		}
	}
}
