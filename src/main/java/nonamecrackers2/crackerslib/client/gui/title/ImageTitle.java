package nonamecrackers2.crackerslib.client.gui.title;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public record ImageTitle(Identifier location, int imageWidth, int imageHeight, int width, int height) implements TitleLogo
{
	public static ImageTitle ofMod(String modid, int imageWidth, int imageHeight, int width, int height)
	{
		Identifier location = Identifier.fromNamespaceAndPath(modid, "textures/gui/config/title/title.png");
		return new ImageTitle(location, imageWidth, imageHeight, width, height);
	}
	
	public static ImageTitle ofMod(String modid, int imageWidth, int imageHeight, float scale)
	{
		int width = Mth.floor((float)imageWidth * scale);
		int height = Mth.floor((float)imageHeight * scale);
		return ofMod(modid, width, height, width, height);
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor stack, int x, int y, float partialTicks)
	{
		stack.blit(RenderPipelines.GUI_TEXTURED, this.location, x, y, 0.0F, 0.0F, this.width, this.height, this.width, this.height, this.imageWidth, this.imageHeight); //TODO: Test
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
