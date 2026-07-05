package nonamecrackers2.crackerslib.client.gui.title;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface TitleLogo
{
	void extractRenderState(GuiGraphicsExtractor stack, int x, int y, float partialTicks);
	
	int getWidth();
	
	int getHeight();
}
