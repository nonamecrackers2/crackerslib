package nonamecrackers2.crackerslib.client.gui.renderer.state;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;

public record GuiCustom3DState(
		Render render,
		Vector3fc translation,
	    Quaternionfc rotation,
	    float partialTick,
	    int mouseX,
	    int mouseY,
	    int x0,
	    int y0,
	    int x1,
	    int y1,
	    float scale,
	    float farPlane,
	    @Nullable ScreenRectangle scissorArea,
	    @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
	
	public GuiCustom3DState(Render render, Vector3fc translation, Quaternionfc rotation, float partialTick, int mouseX, int mouseY, int x0, int y0, int x1, int y1, float scale, float farPlane, @Nullable ScreenRectangle scissorArea)
	{
		this(render, translation, rotation, partialTick, mouseX, mouseY, x0, y0, x1, y1, scale, farPlane, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
	}
	
	@FunctionalInterface
	public static interface Render
	{
		void render(PoseStack stack, SubmitNodeCollector collector, int mouseX, int mouseY, float partialTick);
	}
}
