package nonamecrackers2.crackerslib.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.data.ModelData;

public class TestScreen3D extends Screen3D
{
	public TestScreen3D()
	{
		super(CommonComponents.EMPTY, 5.0F, 100.0F);
		this.renderOrigin(true);
	}
	
	@Override
	protected void render3D(PoseStack stack, MultiBufferSource buffers, int mouseX, int mouseY, float partialTick)
	{
		this.minecraft.getBlockRenderer().renderSingleBlock(Blocks.GRASS_BLOCK.defaultBlockState(), stack, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, (RenderType)null);
	}
}
