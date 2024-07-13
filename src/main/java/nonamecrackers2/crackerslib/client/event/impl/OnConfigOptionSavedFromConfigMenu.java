package nonamecrackers2.crackerslib.client.event.impl;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.gui.ConfigScreen;
import nonamecrackers2.crackerslib.common.event.impl.OnConfigOptionSaved;

/**
 * Fired when a config option is saved/modified using a config menu.
 */
public class OnConfigOptionSavedFromConfigMenu<T> extends OnConfigOptionSaved<T>
{
	private final @Nullable ConfigScreen configScreen;
	private final @Nullable Screen homeScreen;
	
	public OnConfigOptionSavedFromConfigMenu(String modid, ModConfig.Type type, ForgeConfigSpec.ConfigValue<T> config, T newValue, boolean didValueChange, @Nullable ConfigScreen configScreen, @Nullable Screen homeScreen)
	{
		super(modid, type, OnConfigOptionSaved.Source.CONFIG_SCREEN, config, newValue, didValueChange);
		this.configScreen = configScreen;
		this.homeScreen = homeScreen;
	}

	public @Nullable ConfigScreen getConfigScreen()
	{
		return this.configScreen;
	}
	
	public @Nullable Screen getHomeScreen()
	{
		return this.homeScreen;
	}
}
