package nonamecrackers2.crackerslib.client.config;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;

@FunctionalInterface
public interface ConfigHomeScreenFactory
{
	public ConfigHomeScreen build(String modid, Map<ModConfig.Type, ForgeConfigSpec> specs, boolean isWorldLoaded, boolean hasSinglePlayerServer, @Nullable Screen previous);
}
