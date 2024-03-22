package nonamecrackers2.crackerslib.client.gui.title;

import net.minecraft.client.gui.GuiGraphics;

public interface TitleLogo
{
	void blit(GuiGraphics stack, int x, int y, float partialTicks);
	
	int getWidth();
	
	int getHeight();
}
