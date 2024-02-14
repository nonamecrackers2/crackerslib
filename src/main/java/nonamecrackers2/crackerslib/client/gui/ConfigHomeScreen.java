package nonamecrackers2.crackerslib.client.gui;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigHomeScreen extends Screen
{
	private static final int TITLE_WIDTH = 115*2;
	private static final int TITLE_HEIGHT = 47*2;
	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 20;
	private static final int EXIT_BUTTON_OFFSET = 26;
	
	private final ResourceLocation titleLogo;
	private final String modid;
	private final Map<ModConfig.Type, ForgeConfigSpec> specs;
	
	private final boolean isWorldLoaded;
	private final boolean hasSinglePlayerServer;
	private final @Nullable Screen previous;
	
	private Button clientButton;
	private Button commonButton;
	private Button worldButton;
	private Button exit;
	
	//private Button refreshSoundsButton;
	
	private Button discordButton;
	private Button patreonButton;
	private Button githubButton;
	private Button nazaKofiButton;
	
	public ConfigHomeScreen(String modid, Map<ModConfig.Type, ForgeConfigSpec> specs, boolean isWorldLoaded, boolean hasSinglePlayerServer, @Nullable Screen previous)
	{
		super(Component.translatable("gui." + modid + ".screen.config.home.title"));
		this.titleLogo = new ResourceLocation(modid, "textures/gui/config/config_title.png");
		this.modid = modid;
		this.specs = specs;
		this.isWorldLoaded = isWorldLoaded;
		this.hasSinglePlayerServer = hasSinglePlayerServer;
		this.previous = previous;
	}
	
	@Override
	protected void init()
	{
		int ySpacing = 24;
		
		this.clientButton = Button.builder(Component.translatable("gui.crackerslib.screen.clientOptions.title"), button -> this.openConfigMenu(ModConfig.Type.CLIENT))
				.pos((this.width - 200) / 2, (this.height - 60) / 2)
				.size(200, 20)
				.tooltip(Tooltip.create(Component.translatable("gui.crackerslib.screen.clientOptions.info")))
				.build();
		
		this.commonButton = Button.builder(Component.translatable("gui.crackerslib.screen.commonOptions.title"), button -> this.openConfigMenu(ModConfig.Type.COMMON))
				.pos((this.width - 200) / 2, this.clientButton.getY() + ySpacing)
				.size(200, 20)
				.tooltip(Tooltip.create(Component.translatable("gui.crackerslib.screen.commonOptions.info")))
				.build();
		
		this.worldButton = Button.builder(Component.translatable("gui.crackerslib.screen.serverOptions.title"), button -> this.openConfigMenu(ModConfig.Type.SERVER))
				.pos((this.width - 200) / 2, this.commonButton.getY() + ySpacing)
				.size(200, 20)
				.build();
		
		this.exit = Button.builder(Component.translatable("gui.crackerslib.button.exit.title"), button -> this.onClose())
				.pos((this.width - BUTTON_WIDTH) / 2, this.height - EXIT_BUTTON_OFFSET)
				.size(BUTTON_WIDTH, BUTTON_HEIGHT)
				.build();
		
//			this.refreshSoundsButton = Button.builder(Component.translatable("gui.witherstormmod.button.refreshSounds.title"), button -> SoundManagersRefresher.INSTANCE.refresh())
//					.pos(5, 5)
//					.size(20, 20)
//					.tooltip(Tooltip.create(Component.translatable("gui.witherstormmod.button.refreshSounds.title")))
//					.build(RefreshSoundsButton::new);
		
//			this.addRenderableWidget(this.refreshSoundsButton);
//			this.refreshSoundsButton.active = this.minecraft.level != null;
		
		int socialsY = this.worldButton.getY() + ySpacing;
		
		this.discordButton = Button.builder(Component.translatable("gui.crackerslib.screen.config.discord").withStyle(Style.EMPTY.withColor(0xFF5865F2)), button -> this.openLink("https://discord.com/invite/cracker-s-modded-community-987817685293355028"))
				.pos((this.width - 200) / 2, socialsY)
				.size(98, 20)
				.tooltip(Tooltip.create(Component.translatable("gui.crackerslib.screen.config.discord.info")))
				.build();
		
		this.patreonButton = Button.builder(Component.translatable("gui.crackerslib.screen.config.patreon").withStyle(ChatFormatting.RED), button -> this.openLink("https://www.patreon.com/user?u=87070090"))
				.pos((this.width - 200) / 2 + 102, socialsY)
				.size(98, 20)
				.tooltip(Tooltip.create(Component.translatable("gui.crackerslib.screen.config.patreon.info")))
				.build();
		
		this.githubButton = Button.builder(Component.translatable("gui.crackerslib.screen.config.github").withStyle(Style.EMPTY.withColor(0xFFababab)), button -> this.openLink("https://github.com/nonamecrackers2/crackers-wither-storm-mod/issues"))
				.pos((this.width - 200) / 2, this.discordButton.getY() + ySpacing)
				.size(98, 20)
				.tooltip(Tooltip.create(Component.translatable("gui.crackerslib.screen.config.github.info")))
				.build();
		
		this.nazaKofiButton = Button.builder(Component.translatable("gui.crackerslib.screen.config.nazaKofi").withStyle(ChatFormatting.GREEN), button -> this.openLink("https://ko-fi.com/nazaru"))
				.pos((this.width - 200) / 2 + 102, this.patreonButton.getY() + ySpacing)
				.size(98, 20)
				.tooltip(Tooltip.create(Component.translatable("gui.crackerslib.screen.config.nazaKofi.info")))
				.build();
		
		this.addRenderableWidget(this.discordButton);
		this.addRenderableWidget(this.patreonButton);
		this.addRenderableWidget(this.githubButton);
		this.addRenderableWidget(this.nazaKofiButton);
		
		this.addRenderableWidget(this.clientButton);
		this.commonButton.active = (this.isWorldLoaded && this.hasSinglePlayerServer) || !this.isWorldLoaded;
		this.addRenderableWidget(this.commonButton);
		this.worldButton.active = (this.isWorldLoaded && this.hasSinglePlayerServer);
		this.addRenderableWidget(this.worldButton);
		this.addRenderableWidget(this.exit);
	}
	
	@Override
	public void render(GuiGraphics stack, int mouseX, int mouseY, float partialTicks)
	{
		MutableComponent worldDesc = Component.translatable("gui.crackerslib.screen.serverOptions.notInWorld.info");
		if (this.isWorldLoaded)
			worldDesc = Component.translatable("gui.crackerslib.screen.serverOptions.inWorld.info");
		this.worldButton.setTooltip(Tooltip.create(worldDesc));
		this.renderBackground(stack);
		stack.blit(this.titleLogo, this.width / 2 - TITLE_WIDTH / 2, 20, 0, 0.0F, 0.0F, TITLE_WIDTH, TITLE_HEIGHT, 256, 256);
		super.render(stack, mouseX, mouseY, partialTicks); 
	}
	
	@Override
	public void onClose()
	{
		if (this.previous == null)
			super.onClose();
		else
			this.minecraft.setScreen(this.previous);
	}
	
	protected void openConfigMenu(ModConfig.Type type)
	{
		ForgeConfigSpec spec = this.specs.get(type);
		if (spec != null)
			this.minecraft.setScreen(ConfigScreen.makeScreen(this.modid, spec, type, this));
	}
	
	protected void openLink(String link)
	{
		this.minecraft.setScreen(new ConfirmLinkScreen(b -> {
			if (b)
				Util.getPlatform().openUri(link);
			this.minecraft.setScreen(this);
		}, link, true));
	}
}
