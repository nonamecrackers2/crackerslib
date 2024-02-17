package nonamecrackers2.crackerslib.client.gui.title;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.fml.ModList;

public record TextTitle(Component title) implements TitleLogo
{
	public static TextTitle ofModDisplayName(String modid, Style style)
	{
		return ModList.get().getModContainerById(modid).map(container -> {
			return new TextTitle(Component.literal(container.getModInfo().getDisplayName()).withStyle(style));
		}).orElseThrow(() -> new NullPointerException("Could not find mod with id '" + modid + "'"));
	}
	
	public static TextTitle ofModDisplayName(String modid)
	{
		return ofModDisplayName(modid, Style.EMPTY.withBold(true).withUnderlined(true));
	}
	
	@Override
	public void blit(GuiGraphics stack, int x, int y, float partialTicks)
	{
		Minecraft mc = Minecraft.getInstance();
		stack.drawCenteredString(mc.font, this.title, x, y, 0xFFFFFFFF);
	}

	@Override
	public int getWidth()
	{
		return 0;
	}

	@Override
	public int getHeight()
	{
		return 0;
	}
}
