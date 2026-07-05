package nonamecrackers2.crackerslib.client.gui.widget;

import java.util.Objects;

import javax.annotation.Nullable;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.SubmitNodeCollector;
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

	public void submitAs3D(PoseStack stack, SubmitNodeCollector collector, int mouseX, int mouseY, float partialTick)
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
}
