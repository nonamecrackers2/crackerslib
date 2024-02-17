package nonamecrackers2.crackerslib.client.gui;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.config.ConfigHomeScreenFactory;
import nonamecrackers2.crackerslib.client.gui.title.TitleLogo;
import nonamecrackers2.crackerslib.client.util.GUIUtils;

public class ConfigHomeScreen extends Screen
{
	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 20;
	private static final int EXIT_BUTTON_OFFSET = 6;
	private static final int TITLE_PADDING = 20;
	
	private final String modid;
	private final Map<ModConfig.Type, ForgeConfigSpec> specs;
	private final TitleLogo title;
	
	private final boolean isWorldLoaded;
	private final boolean hasSinglePlayerServer;
	private final @Nullable Screen previous;
	
	private @Nullable Button commonButton;
	private @Nullable Button worldButton;
	private Button exit;
	
	public ConfigHomeScreen(String modid, Map<ModConfig.Type, ForgeConfigSpec> specs, TitleLogo title, boolean isWorldLoaded, boolean hasSinglePlayerServer, @Nullable Screen previous)
	{
		super(Component.translatable("gui." + modid + ".screen.config.home.title"));
		this.title = title;
		this.modid = modid;
		this.specs = specs;
		this.isWorldLoaded = isWorldLoaded;
		this.hasSinglePlayerServer = hasSinglePlayerServer;
		this.previous = previous;
	}
	
	@Override
	protected void init()
	{
		GridLayout layout = new GridLayout().rowSpacing(6);
		GridLayout.RowHelper rowHelper = layout.createRowHelper(1);
		
		if (this.specs.containsKey(ModConfig.Type.CLIENT))
		{
			rowHelper.addChild(Button.builder(Component.translatable("gui.crackerslib.screen.clientOptions.title"), button -> this.openConfigMenu(ModConfig.Type.CLIENT))
					.size(BUTTON_WIDTH, BUTTON_HEIGHT)
					.tooltip(Tooltip.create(Component.translatable("gui.crackerslib.screen.clientOptions.info")))
					.build());
		}
		
		if (this.specs.containsKey(ModConfig.Type.COMMON))
		{
			this.commonButton = rowHelper.addChild(Button.builder(Component.translatable("gui.crackerslib.screen.commonOptions.title"), button -> this.openConfigMenu(ModConfig.Type.COMMON))
					.size(BUTTON_WIDTH, BUTTON_HEIGHT)
					.tooltip(Tooltip.create(Component.translatable("gui.crackerslib.screen.commonOptions.info")))
					.build());
		}
		
		if (this.specs.containsKey(ModConfig.Type.SERVER))
		{
			this.worldButton = rowHelper.addChild(Button.builder(Component.translatable("gui.crackerslib.screen.serverOptions.title"), button -> this.openConfigMenu(ModConfig.Type.SERVER))
					.size(BUTTON_WIDTH, BUTTON_HEIGHT)
					.build());
		}
		
		this.initExtraButtons(rowHelper);
		
//		extraButtonsRowHelper.addChild(Button.builder(Component.translatable("gui.crackerslib.screen.config.nazaKofi").withStyle(ChatFormatting.GREEN), button -> this.openLink("https://ko-fi.com/nazaru"))
//				.size(98, 20)
//				.tooltip(Tooltip.create(Component.translatable("gui.crackerslib.screen.config.nazaKofi.info")))
//				.build());
		
		this.exit = Button.builder(Component.translatable("gui.crackerslib.button.exit.title"), button -> this.onClose())
				.pos((this.width - BUTTON_WIDTH) / 2, this.height - EXIT_BUTTON_OFFSET - 20)
				.size(BUTTON_WIDTH, BUTTON_HEIGHT)
				.build();
		
		layout.arrangeElements();
		int top = TITLE_PADDING + this.title.getHeight();
		int padding = EXIT_BUTTON_OFFSET * 2 + this.exit.getHeight();
		FrameLayout.centerInRectangle(layout, 0, Math.max(padding, top), this.width, this.height - top - padding);
		layout.visitWidgets(this::addRenderableWidget);
		
		if (this.commonButton != null)
			this.commonButton.active = (this.isWorldLoaded && this.hasSinglePlayerServer) || !this.isWorldLoaded;
		if (this.worldButton != null)
			this.worldButton.active = (this.isWorldLoaded && this.hasSinglePlayerServer);
		this.addRenderableWidget(this.exit);
	}
	
	protected void initExtraButtons(GridLayout.RowHelper main) {}
	
	@Override
	public void render(GuiGraphics stack, int mouseX, int mouseY, float partialTicks)
	{
		MutableComponent worldDesc = Component.translatable("gui.crackerslib.screen.serverOptions.notInWorld.info");
		if (this.isWorldLoaded)
			worldDesc = Component.translatable("gui.crackerslib.screen.serverOptions.inWorld.info");
		if (this.worldButton != null)
			this.worldButton.setTooltip(Tooltip.create(worldDesc));
		this.renderBackground(stack);
		int titleX = this.width / 2;
		int titleY = TITLE_PADDING + this.title.getHeight() / 2;
		this.title.blit(stack, titleX, titleY, partialTicks);
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
//	
//	protected void openLink(String link)
//	{
//		this.minecraft.setScreen(new ConfirmLinkScreen(b -> {
//			if (b)
//				Util.getPlatform().openUri(link);
//			this.minecraft.setScreen(this);
//		}, link, true));
//	}
	
	public static ConfigHomeScreen.Builder builder(TitleLogo title)
	{
		return new ConfigHomeScreen.Builder(title);
	}
	
	public static class Builder
	{
		private final List<Supplier<AbstractButton>> extraButtons = Lists.newArrayList();
		private final TitleLogo title;
		
		private Builder(TitleLogo title)
		{
			this.title = title;
		}
		
		public Builder addExtraButton(Supplier<AbstractButton> supplier)
		{
			this.extraButtons.add(supplier);
			return this;
		}
		
		public Builder addLinkButton(Component title, String link, boolean fullSize, @Nullable Tooltip tooltip)
		{
			return this.addExtraButton(() -> 
			{
				return Button.builder(title, button -> GUIUtils.openLink(link))
						.size(fullSize ? 200 : 98, 20)
						.tooltip(tooltip)
						.build();
			});
		}
		
		public Builder addLinkButton(Component title, String link, boolean fullSize)
		{
			return this.addLinkButton(title, link, fullSize, null);
		}
		
		public Builder standardLinks(String discordLink, String patreonLink, String githubLink)
		{
			this.addLinkButton(Component.translatable("gui.crackerslib.screen.config.discord").withStyle(Style.EMPTY.withColor(0xFF5865F2)), discordLink, false, Tooltip.create(Component.translatable("gui.crackerslib.screen.config.discord.info")));
			this.addLinkButton(Component.translatable("gui.crackerslib.screen.config.github").withStyle(Style.EMPTY.withColor(0xFFababab)), githubLink, false, Tooltip.create(Component.translatable("gui.crackerslib.screen.config.github.info")));
			this.addLinkButton(Component.translatable("gui.crackerslib.screen.config.patreon").withStyle(ChatFormatting.RED), patreonLink, true, Tooltip.create(Component.translatable("gui.crackerslib.screen.config.patreon.info")));
			return this;
		}
		
		public Builder crackersDefault(String github)
		{
			return this.standardLinks("https://discord.com/invite/cracker-s-modded-community-987817685293355028", "https://www.patreon.com/nonamecrackers2", github);
		}
		
		public ConfigHomeScreenFactory build()
		{
			return (modid, specs, isWorldLoaded, hasSinglePlayerServer, previous) -> 
			{
				return new ConfigHomeScreen(modid, specs, this.title, isWorldLoaded, hasSinglePlayerServer, previous)
				{
					@Override
					protected void initExtraButtons(GridLayout.RowHelper main)
					{
						if (!Builder.this.extraButtons.isEmpty())
						{
							GridLayout extraButtons = main.addChild(new GridLayout().rowSpacing(6).columnSpacing(4));
							GridLayout.RowHelper extraButtonsRowHelper = extraButtons.createRowHelper(2);
							
							for (var supplier : Builder.this.extraButtons)
							{
								var button = supplier.get();
								int occupiedColumns = 1;
								if (button.getWidth() > 100)
									occupiedColumns++;
								extraButtonsRowHelper.addChild(button, occupiedColumns);
							}
						}
					}
				};
			};
		}
	}
}
