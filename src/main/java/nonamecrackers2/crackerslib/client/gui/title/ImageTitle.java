package nonamecrackers2.crackerslib.client.gui.title;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public record ImageTitle(ResourceLocation location, int imageWidth, int imageHeight, int width, int height) implements TitleLogo
{
	public static ImageTitle ofMod(String modid, int imageWidth, int imageHeight, int width, int height)
	{
		ResourceLocation location = new ResourceLocation(modid, "textures/gui/config/title/title.png");
		return new ImageTitle(location, imageWidth, imageHeight, width, height);
	}
	
	public static ImageTitle ofMod(String modid, int imageWidth, int imageHeight, float scale)
	{
		int width = Mth.floor((float)imageWidth * scale);
		int height = Mth.floor((float)imageHeight * scale);
		return ofMod(modid, width, height, width, height);
	}
	
	@Override
	public void blit(GuiGraphics stack, int x, int y, float partialTicks)
	{
		stack.blit(this.location, x, y, this.width, this.height, 0.0F, 0.0F, this.width, this.height, this.imageWidth, this.imageHeight);
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
