package nonamecrackers2.crackerslib.client.gui.widget.config;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;
import nonamecrackers2.crackerslib.client.gui.TestScreen3D;
import nonamecrackers2.crackerslib.client.gui.title.TitleLogo;

public class CrackersLibConfigHomeMenu extends ConfigHomeScreen
{
	public CrackersLibConfigHomeMenu(String modid, Map<Type, ModConfigSpec> specs, TitleLogo title, boolean isWorldLoaded, boolean hasSinglePlayerServer, Screen previous, List<Supplier<AbstractButton>> extraButtons, int totalColumns)
	{
		super(modid, specs, title, isWorldLoaded, hasSinglePlayerServer, previous, extraButtons, totalColumns);
	}

	@Override
	protected void init()
	{
		super.init();
		
		this.addRenderableWidget(Button.builder(Component.literal("Screen 3D Test"), b -> {
			this.minecraft.setScreen(new TestScreen3D());
		}).pos(5, 5).width(100).build());
	}
}
