package nonamecrackers2.crackerslib.client.config;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;

@FunctionalInterface
public interface ConfigHomeScreenFactory
{
	public ConfigHomeScreen build(String modid, Map<ModConfig.Type, ModConfigSpec> specs, boolean isWorldLoaded, boolean hasSinglePlayerServer, @Nullable Screen previous);
}
