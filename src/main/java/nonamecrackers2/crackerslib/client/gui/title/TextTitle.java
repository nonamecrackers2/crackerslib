package nonamecrackers2.crackerslib.client.gui.title;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.fml.ModList;

public record TextTitle(Component title, int width, int height) implements TitleLogo
{
	public static TextTitle ofModDisplayName(String modid, Style style)
	{
		Minecraft mc = Minecraft.getInstance();
		return ModList.get().getModContainerById(modid).map(container -> {
			Component text = Component.literal(container.getModInfo().getDisplayName()).withStyle(style);
			return new TextTitle(text, mc.font.width(text), mc.font.lineHeight);
		}).orElseThrow(() -> new NullPointerException("Could not find mod with id '" + modid + "'"));
	}
	
	public static TextTitle ofModDisplayName(String modid)
	{
		return ofModDisplayName(modid, Style.EMPTY.withBold(true).withUnderlined(true));
	}
	
	@Override
	public void blit(PoseStack stack, int x, int y, float partialTicks)
	{
		Minecraft mc = Minecraft.getInstance();
		GuiComponent.drawString(stack, mc.font, this.title, x, y, 0xFFFFFFFF);
	}

	@Override
	public int getWidth()
	{
		return this.width;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}
}
