package nonamecrackers2.crackerslib.common.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import nonamecrackers2.crackerslib.CrackersLib;
import nonamecrackers2.crackerslib.common.config.CrackersLibConfig;
import nonamecrackers2.crackerslib.common.util.data.ConfigLangGeneratorHelper;

public class CrackersLibLangProvider extends LanguageProvider
{
	public CrackersLibLangProvider(PackOutput output)
	{
		super(output, CrackersLib.MODID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
		ConfigLangGeneratorHelper.langForSpec(CrackersLib.MODID, CrackersLibConfig.CLIENT_SPEC, this, true);
		this.add("config.crackerslib.preset.note", "NOTE: Changing the preset may override other values, make backups!");
		this.add("config.crackerslib.preset.custom.title", "Custom");
		this.add("config.crackerslib.preset.default.title", "Default");
		this.add("config.crackerslib.preset.default.description", "Default settings.");
		this.add("config.crackerslib.preset.custom.description", "Custom values defined by the user. Changing any value defaulted by a preset will change it to custom.");
		this.add("gui.crackerslib.button.preset.title", "Preset");
		this.add("gui.crackerslib.button.preset.holdShift", "Hold SHIFT to see a description");
		this.add("gui.crackerslib.button.reset.title", "Reset");
		this.add("gui.crackerslib.button.exit.title", "Exit");
		this.add("gui.crackerslib.button.exitAndSave.title", "Save and Exit");
		this.add("gui.crackerslib.button.sorting.title", "Sorting");
		this.add("gui.crackerslib.button.sorting.a-z.tooltip", "A-Z");
		this.add("gui.crackerslib.button.sorting.z-a.tooltip", "Z-A");
		this.add("gui.crackerslib.button.collapse.title", "Collapse");
		this.add("gui.crackerslib.button.collapse.description", "Collapse all categories");
		this.add("gui.crackerslib.screen.config.home.title", "CrackersLib Config");
		this.add("gui.crackerslib.screen.config.discord", "Discord Server");
		this.add("gui.crackerslib.screen.config.discord.info", "Join the official Discord server!");
		this.add("gui.crackerslib.screen.config.patreon", "Patreon");
		this.add("gui.crackerslib.screen.config.patreon.info", "Support us on Patreon!");
		this.add("gui.crackerslib.screen.config.github", "GitHub");
		this.add("gui.crackerslib.screen.config.github.info", "Report bugs/provide feedback here!");
		this.add("gui.crackerslib.screen.config.search", "Search");
		this.add("gui.crackerslib.screen.config.requiresRestart", "Requires restart");
		this.add("gui.crackerslib.screen.clientOptions.title", "Client Options");
		this.add("gui.crackerslib.screen.clientOptions.info", "Includes various config options for the client with varying purposes, such as performance and personal preference.");
		this.add("gui.crackerslib.screen.commonOptions.title", "Common Options");
		this.add("gui.crackerslib.screen.commonOptions.info", "Includes config options across all worlds.");
		this.add("gui.crackerslib.screen.serverOptions.title", "World Options");
		this.add("gui.crackerslib.screen.serverOptions.notInWorld.info", "Includes config options unique for each world. Can only be accessed while in a world.");
		this.add("gui.crackerslib.screen.serverOptions.inWorld.info", "Includes config options unique for this world.");
		this.add("gui.crackerslib.config.noAvailableOptions", "No config options found.");
		this.add("argument.crackerslib.config.invalidValue", "Could not find config value `%s`");
		this.add("commands.crackerslib.setConfig.set.fail", "Value did not change or is invalid.");
		this.add("commands.crackerslib.setConfig.set.success", "Successfully set '%s' to '%s'");
		this.add("commands.crackerslib.setConfig.set.note", "Note: '%s' requires a restart to take effect.");
		this.add("commands.crackerslib.getConfig.get", "The value of '%s' is '%s'");
		this.add("commands.crackerslib.setDefault.success", "Set '%s' to its default: '%s'");
	}
}
