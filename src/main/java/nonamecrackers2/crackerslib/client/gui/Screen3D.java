package nonamecrackers2.crackerslib.client.gui;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import nonamecrackers2.crackerslib.client.gui.widget.Widget3D;

public abstract class Screen3D extends Screen
{
	protected final float farPlane;
	protected final float zoomConstant;
	protected float camRotX = 180.0F + 45.0F;
	protected float camRotY = 45.0F;
	protected float zoom = 1.0F;
	protected Vector3f offset = new Vector3f(0.0F, 0.0F, 0.0F);
	protected @Nullable Matrix4f poseMatrix;
	protected @Nullable Vector3f hitPos;
	protected @Nullable Vector3f lastDragPos;
	protected boolean renderOrigin;
	protected int moveForTime;
	protected float moveFor;
	protected @Nullable Vector3f moveFrom;
	protected @Nullable Supplier<Vector3f> moveTo;
	protected float initialZoom;
	protected float finalZoom;
	
	protected Screen3D(Component title, float zoomConstant, float farPlane)
	{
		super(title);
		this.farPlane = farPlane;
		this.zoomConstant = zoomConstant;
	}
	
	protected void renderOrigin(boolean flag)
	{
		this.renderOrigin = flag;
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if (!super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY))
		{
			if (pButton == 1)
			{
				if (this.canRotate())
				{
					this.camRotX = Mth.clamp(this.camRotX + (float)pDragY, 90.0F, 270.0F);
					this.camRotY = (float)Mth.wrapDegrees((double)this.camRotY + pDragX);
					this.onRotate();
				}
			}
			else if (pButton == 0)
			{
				if (this.canMove())
				{
					Vector3f move = new Vector3f((float)-pDragX / (this.zoom * this.zoomConstant), (float)pDragY / (this.zoom * this.zoomConstant), 0.0F);
					move.rotate(Axis.XP.rotationDegrees(this.camRotX));
					move.rotate(Axis.YN.rotationDegrees(this.camRotY));
					this.offset.add(move);
					this.onMove();
				}
			}
			return false;
		}
		else
		{
			return true;
		}
	}
	
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
	{
		if (!super.mouseScrolled(pMouseX, pMouseY, pDelta))
		{
			if (this.canZoom())
			{
				this.zoom = Math.max(this.zoom + (float)pDelta * (this.zoom / 10.0F), 1.0F);
				this.onZoom();
			}
			return false;
		}
		else
		{
			return true;
		}
	}
	
	@Override
	public void render(PoseStack stack, int pMouseX, int pMouseY, float pPartialTick)
	{
		if (this.moveFor > 0.0F)
		{
			this.moveFor -= pPartialTick * Math.max(this.moveFor/(float)this.moveForTime, 0.0001F);
			if (this.moveForTime > 0 && this.moveTo != null && this.moveFrom != null)
			{
				Vector3f finalPos = this.moveTo.get();
				if (finalPos != null)
				{
					float transition = this.moveFor/(float)this.moveForTime;
					float x = Mth.lerp(transition, finalPos.x, this.moveFrom.x);
					float y = Mth.lerp(transition, finalPos.y, this.moveFrom.y);
					float z = Mth.lerp(transition, finalPos.z, this.moveFrom.z);
					this.offset = new Vector3f(x, y, z);
					this.zoom = Mth.lerp(transition, this.finalZoom, this.initialZoom);
				}
			}
			if (this.moveFor <= 0.0F)
			{
				this.moveFrom = null;
				this.moveForTime = 0;
			}
		}
		else
		{
			if (this.moveTo != null)
				this.offset = this.moveTo.get();
		}
		
		Quaternionf rot = new Quaternionf().rotateX(this.camRotX * ((float)Math.PI / 180.0F)).rotateY((float)Math.PI + this.camRotY * ((float)Math.PI / 180.0F));
		
		Matrix4f prevProjMat = RenderSystem.getProjectionMatrix();
		Window window = this.minecraft.getWindow();
		Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, (float)((double)window.getWidth() / window.getGuiScale()), (float)((double)window.getHeight() / window.getGuiScale()), 0.0F, 0.0F, this.farPlane);
		RenderSystem.setProjectionMatrix(matrix4f);
		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();
		modelViewStack.setIdentity();
		RenderSystem.applyModelViewMatrix();
		
		stack.pushPose();
		stack.translate((double)(this.width / 2), (double)(this.height / 2), 0.0F);
		stack.mulPoseMatrix((new Matrix4f()).scaling(this.zoom * this.zoomConstant, this.zoom * this.zoomConstant, -1.0F));
		stack.translate(0.0D, 0.0D, this.farPlane / 2.0F);
		stack.mulPose(rot);
		stack.translate(this.offset.x, this.offset.y, this.offset.z);
		
		Lighting.setupForEntityInInventory();
		
		MultiBufferSource.BufferSource bufferSource = this.minecraft.renderBuffers().bufferSource();
		
		if (this.renderOrigin)
		{
			VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
			Matrix4f pose = stack.last().pose();
			Matrix3f normal = stack.last().normal();
			consumer.vertex(pose, 0.0F, 0.0F, 0.0F).color(0.0F, 1.0F, 0.0F, 1.0F).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
			consumer.vertex(pose, 0.0F, 1.0F, 0.0F).color(0.0F, 1.0F, 0.0F, 1.0F).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
			
			consumer.vertex(pose, 0.0F, 0.0F, 0.0F).color(1.0F, 0.0F, 0.0F, 1.0F).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
			consumer.vertex(pose, 1.0F, 0.0F, 0.0F).color(1.0F, 0.0F, 0.0F, 1.0F).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
			
			consumer.vertex(pose, 0.0F, 0.0F, 0.0F).color(0.0F, 0.0F, 1.0F, 1.0F).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
			consumer.vertex(pose, 0.0F, 0.0F, 1.0F).color(0.0F, 0.0F, 1.0F, 1.0F).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
		}
		
		this.poseMatrix = stack.last().pose();
		this.render3D(stack, bufferSource, pMouseX, pMouseY, this.minecraft.getPartialTick());
		
		bufferSource.endBatch();
		
		for (Renderable renderable : this.renderables)
		{
			if (renderable instanceof Widget3D widget)
				widget.renderAs3D(stack, bufferSource, pMouseX, pMouseY, this.minecraft.getPartialTick());
		}
		
		RenderSystem.clear(256, Minecraft.ON_OSX);
		bufferSource.endBatch();
		
		stack.popPose();
		
		RenderSystem.clear(256, Minecraft.ON_OSX);
		
		RenderSystem.setProjectionMatrix(prevProjMat);
		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();
		
		super.render(stack, pMouseX, pMouseY, pPartialTick);
	}
	
	protected void render3D(PoseStack stack, MultiBufferSource buffers, int mouseX, int mouseY, float partialTick) {}
	
	protected boolean canZoom() 
	{ 
		return this.moveFor <= 0.0F;
	}
	
	protected boolean canMove() 
	{ 
		return this.moveFor <= 0.0F; 
	}
	
	protected boolean canRotate() { return true; }
	
	protected void onZoom() {}
	
	protected void onMove()
	{
		this.moveTo = null;
	}
	
	protected void onRotate() {}
	
	protected void lerpTo(int time, Supplier<Vector3f> pos, float zoom)
	{
		this.moveFor = (float)time;
		this.moveForTime = time;
		this.moveTo = pos;
		this.moveFrom = this.offset;
		this.initialZoom = this.zoom;
		this.finalZoom = zoom;
	}
	
	public static void renderIcon(PoseStack stack, Vector3f pos, MultiBufferSource buffer, ResourceLocation tex, float camRotX, float camRotY, float zoom, float size, float r, float g, float b)
	{
		stack.pushPose();
		stack.translate(pos.x, pos.y, pos.z);
		stack.scale(1.0F/zoom, 1.0F/zoom, 1.0F/zoom);
		stack.mulPose(Axis.YN.rotationDegrees(camRotY));
		stack.mulPose(Axis.XP.rotationDegrees(camRotX));
		stack.scale(1.0F, -1.0F, 1.0F);
		stack.translate(0.0D, 0.0D, 10.0D);
		Matrix4f pose = stack.last().pose();
		Matrix3f normal = stack.last().normal();
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(tex));
		consumer.vertex(pose, size, -size, size).color(r, g, b, 1.0F).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(pose, -size, -size, size).color(r, g, b, 1.0F).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(pose, -size, size, size).color(r, g, b, 1.0F).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(pose, size, size, size).color(r, g, b, 1.0F).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		stack.popPose();
	}
}
