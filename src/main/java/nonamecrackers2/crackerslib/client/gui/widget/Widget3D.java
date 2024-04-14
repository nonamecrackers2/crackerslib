package nonamecrackers2.crackerslib.client.gui.widget;

import java.util.Objects;

import javax.annotation.Nullable;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import nonamecrackers2.crackerslib.client.util.RenderUtil;

public abstract class Widget3D extends AbstractWidget
{
	protected @Nullable Matrix4f poseMatrix;
	protected Vector2f screenPos;
	protected Vector3f pos;
	
	public Widget3D(Vector3f pos, int screenX, int screenY, int width, int height, Component name)
	{
		super(screenX, screenY, width, height, name);
		this.pos = pos;
	}
	
	public void setPos(Vector3f pos)
	{
		this.pos = pos;
	}
	
	protected void updatePoseMatrix(Matrix4f poseMatrix)
	{
		this.poseMatrix = poseMatrix;
	}
	
	protected final Vector2f convertPosToScreenCoord(Vector3f pos)
	{
		Objects.requireNonNull(this.poseMatrix, "Previous 3D pose matrix is null!");
		return RenderUtil.getScreenCoordinatesFromWorldPos(this.poseMatrix, pos);
	}
	
	protected final Vector3f convertPosToScreenCordWithZDist(Vector3f pos)
	{
		Objects.requireNonNull(this.poseMatrix, "Previous 3D pose matrix is null!");
		return RenderUtil.getScreenCoordinatesFromWorldPosWithZDist(this.poseMatrix, pos);
	}

	public void renderAs3D(PoseStack stack, MultiBufferSource buffers, int mouseX, int mouseY, float partialTick)
	{
		this.updatePoseMatrix(stack.last().pose());
		this.screenPos = this.convertPosToScreenCoord(this.pos);
		this.updatePos();
	}

	protected void updatePos()
	{
		this.setX((int)this.screenPos.x);
		this.setY((int)this.screenPos.y);
	}
	
	protected static void blit(PoseStack stack, float x, float y, int blitOffset, float width, float height, float u1, float v1, float u2, float v2)
	{
		Matrix4f matrix4f = stack.last().pose();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(matrix4f, x, y, (float)blitOffset).uv(u1, v1).endVertex();
		bufferbuilder.vertex(matrix4f, x, y + height, (float)blitOffset).uv(u1, v2).endVertex();
		bufferbuilder.vertex(matrix4f, x + width, y + height, (float)blitOffset).uv(u2, v2).endVertex();
		bufferbuilder.vertex(matrix4f, x + width, y, (float)blitOffset).uv(u2, v1).endVertex();
		BufferUploader.drawWithShader(bufferbuilder.end());
	}
}
