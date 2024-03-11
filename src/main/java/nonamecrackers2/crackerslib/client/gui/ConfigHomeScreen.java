package nonamecrackers2.crackerslib.client.gui;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
	
	private static final Component CLIENT_OPTIONS_TOOLTIP = Component.translatable("gui.crackerslib.screen.clientOptions.info");
	private static final Component COMMON_OPTIONS_TOOLTIP = Component.translatable("gui.crackerslib.screen.commonOptions.info");
	private static final Component WORLD_OPTIONS_NOT_IN_WORLD_TOOLTIP = Component.translatable("gui.crackerslib.screen.serverOptions.notInWorld.info");
	private static final Component WORLD_OPTIONS_IN_WORLD_TOOLTIP = Component.translatable("gui.crackerslib.screen.serverOptions.inWorld.info");
	
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
		this.exit = new Button((this.width - BUTTON_WIDTH) / 2, this.height - EXIT_BUTTON_OFFSET - 20, BUTTON_WIDTH, BUTTON_HEIGHT, Component.translatable("gui.crackerslib.button.exit.title"), button -> this.onClose());
		
		int top = TITLE_PADDING + this.title.getHeight();
		int padding = EXIT_BUTTON_OFFSET * 2 + this.exit.getHeight();
		int startY = Math.max(padding, top);
		int maxHeight = this.height - top - padding;
		int y = startY;
		int x = this.width / 2 - BUTTON_WIDTH / 2;
		
		int totalHeight = 0;
		
		if (this.specs.containsKey(ModConfig.Type.CLIENT))
		{
			this.addRenderableWidget(new Button(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, Component.translatable("gui.crackerslib.screen.clientOptions.title"), button -> this.openConfigMenu(ModConfig.Type.CLIENT), (b, p, tx, ty) -> {
				this.renderComponentTooltip(p, GUIUtils.wrapTooltip(CLIENT_OPTIONS_TOOLTIP, this.font, this.width / 2), tx, ty, this.font);
			}));
			y += 26;
			totalHeight += 26;
		}
		
		if (this.specs.containsKey(ModConfig.Type.COMMON))
		{
			this.commonButton = this.addRenderableWidget(new Button(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, Component.translatable("gui.crackerslib.screen.commonOptions.title"), button -> this.openConfigMenu(ModConfig.Type.COMMON), (b, p, tx, ty) -> {
				this.renderComponentTooltip(p, GUIUtils.wrapTooltip(COMMON_OPTIONS_TOOLTIP, this.font, this.width / 2), tx, ty, this.font);
			}));
			y += 26;
			totalHeight += 26;
		}
		
		if (this.specs.containsKey(ModConfig.Type.SERVER))
		{
			this.worldButton = this.addRenderableWidget(new Button(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, Component.translatable("gui.crackerslib.screen.serverOptions.title"), button -> this.openConfigMenu(ModConfig.Type.SERVER), (b, p, tx, ty) -> {
				this.renderComponentTooltip(p, GUIUtils.wrapTooltip(this.isWorldLoaded ? WORLD_OPTIONS_IN_WORLD_TOOLTIP : WORLD_OPTIONS_NOT_IN_WORLD_TOOLTIP, this.font, this.width / 2), tx, ty, this.font);
			}));
			y += 26;
			totalHeight += 26;
		}
		
		totalHeight += this.initExtraButtons(x, y, maxHeight, this.width);
		
		int offset = totalHeight / 2 - maxHeight / 2;
		for (Widget widget : this.renderables)
		{
			if (widget instanceof AbstractWidget w)
				w.y -= offset;
		}
		
		if (this.commonButton != null)
			this.commonButton.active = (this.isWorldLoaded && this.hasSinglePlayerServer) || !this.isWorldLoaded;
		if (this.worldButton != null)
			this.worldButton.active = (this.isWorldLoaded && this.hasSinglePlayerServer);
		this.addRenderableWidget(this.exit);
	}
	
	protected int initExtraButtons(int x, int y, int width, int height)
	{
		return 0;
	}
	
	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
	{
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
	
	public static ConfigHomeScreen.Builder builder(TitleLogo title)
	{
		return new ConfigHomeScreen.Builder(title);
	}
	
	public static class Builder
	{
		private static final int MAX_WIDTH = 200;
		private static final int COLUMN_SPACING = 4;
		private final List<Supplier<AbstractButton>> extraButtons = Lists.newArrayList();
		private final TitleLogo title;
		private int totalColumns = 2;
		
		private Builder(TitleLogo title)
		{
			this.title = title;
		}
		
		public Builder totalColumns(int columns)
		{
			this.totalColumns = columns;
			return this;
		}
		
		public Builder addExtraButton(Supplier<AbstractButton> supplier)
		{
			this.extraButtons.add(supplier);
			return this;
		}
		
		public Builder addLinkButton(Component title, String link, @Nullable Component tooltip)
		{
			return this.addExtraButton(() -> 
			{
				return new Button(0, 0, 200, 20, title, button -> GUIUtils.openLink(link), (b, p, x, y) -> {
					if (tooltip != null)
						Minecraft.getInstance().screen.renderTooltip(p, tooltip, x, y);
				});
			});
		}
		
		public Builder addLinkButton(Component title, String link)
		{
			return this.addLinkButton(title, link, null);
		}
		
		public Builder standardLinks(@Nullable String discordLink, @Nullable String patreonLink, @Nullable String githubLink)
		{
			if (discordLink != null)
				this.addLinkButton(Component.translatable("gui.crackerslib.screen.config.discord").withStyle(Style.EMPTY.withColor(0xFF5865F2)), discordLink, Component.translatable("gui.crackerslib.screen.config.discord.info"));
			if (githubLink != null)
				this.addLinkButton(Component.translatable("gui.crackerslib.screen.config.github").withStyle(Style.EMPTY.withColor(0xFFababab)), githubLink, Component.translatable("gui.crackerslib.screen.config.github.info"));
			if (patreonLink != null)
				this.addLinkButton(Component.translatable("gui.crackerslib.screen.config.patreon").withStyle(ChatFormatting.RED), patreonLink, Component.translatable("gui.crackerslib.screen.config.patreon.info"));
			return this;
		}
		
		public Builder crackersDefault(@Nullable String github)
		{
			return this.standardLinks("https://discord.com/invite/cracker-s-modded-community-987817685293355028", "https://www.patreon.com/nonamecrackers2", github);
		}
		
		public Builder crackersDefault()
		{
			return this.crackersDefault(null);
		}
		
		public ConfigHomeScreenFactory build()
		{
			return (modid, specs, isWorldLoaded, hasSinglePlayerServer, previous) -> 
			{
				return new ConfigHomeScreen(modid, specs, this.title, isWorldLoaded, hasSinglePlayerServer, previous)
				{
					@Override
					protected int initExtraButtons(int x, int y, int width, int height)
					{
						if (!Builder.this.extraButtons.isEmpty())
						{
							int totalButtons = Builder.this.extraButtons.size();
							int totalColumns = Math.min(totalButtons, Builder.this.totalColumns);
							
							//GridLayout extraButtons = main.addChild(new GridLayout().rowSpacing(6).columnSpacing(COLUMN_SPACING));
							//GridLayout.RowHelper extraButtonsRowHelper = extraButtons.createRowHelper(totalColumns);
							
							int currentY = y;
							int currentRow = 0;
							for (int i = 0; i < totalButtons; i += totalColumns)
							{
								currentRow++;
								int totalButtonsInRow = totalColumns;
								if (currentRow * totalColumns >= totalButtons)
									totalButtonsInRow -= currentRow * totalColumns - totalButtons;
								//int occupiedColumns = totalColumns / totalButtonsInRow;
								int widthPerButton = (MAX_WIDTH - COLUMN_SPACING * (totalButtonsInRow - 1)) / totalButtonsInRow; 
								for (int j = 0; j < totalButtonsInRow; j++)
								{
									int index = i + j;
									var button = Builder.this.extraButtons.get(index).get();
									button.y = currentY;
									button.x = x + j * (widthPerButton + COLUMN_SPACING);
									button.setWidth(widthPerButton);
									this.addRenderableWidget(button);
									//extraButtonsRowHelper.addChild(button, occupiedColumns);
								}
								currentY += 26;
							}
							return currentRow * 26;
						}
						else
						{
							return 0;
						}
					}
				};
			};
		}
	}
}
