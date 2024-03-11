package nonamecrackers2.crackerslib.client.gui.title;

import com.mojang.blaze3d.vertex.PoseStack;

public interface TitleLogo
{
	void blit(PoseStack stack, int x, int y, float partialTicks);
	
	int getWidth();
	
	int getHeight();
}
