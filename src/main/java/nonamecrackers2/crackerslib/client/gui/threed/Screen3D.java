package nonamecrackers2.crackerslib.client.gui.threed;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import nonamecrackers2.crackerslib.client.gui.renderer.state.GuiCustom3DState;
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
	public boolean mouseDragged(MouseButtonEvent event, double pDragX, double pDragY)
	{
		if (!super.mouseDragged(event, pDragX, pDragY))
		{
			if (event.button() == 1)
			{
				if (this.canRotate())
				{
					this.camRotX = Mth.clamp(this.camRotX + (float)pDragY, 90.0F, 270.0F);
					this.camRotY = (float)Mth.wrapDegrees((double)this.camRotY + pDragX);
					this.onRotate();
				}
			}
			else if (event.button() == 0)
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
	public boolean mouseScrolled(double pMouseX, double pMouseY, double d, double pDelta)
	{
		if (!super.mouseScrolled(pMouseX, pMouseY, d, pDelta))
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
	public void extractRenderState(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick)
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
		
		graphics.submitPictureInPictureRenderState(new GuiCustom3DState((stack, collector, mouseX, mouseY, partialTick) -> {

			if (this.renderOrigin)
			{
				collector.submitCustomGeometry(stack, RenderTypes.LINES, (pose, consumer) -> 
				{
					consumer.addVertex(pose, 0.0F, 0.0F, 0.0F).setColor(0.0F, 1.0F, 0.0F, 1.0F).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(1.0F);
					consumer.addVertex(pose, 0.0F, 1.0F, 0.0F).setColor(0.0F, 1.0F, 0.0F, 1.0F).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(1.0F);
					
					consumer.addVertex(pose, 0.0F, 0.0F, 0.0F).setColor(1.0F, 0.0F, 0.0F, 1.0F).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(1.0F);
					consumer.addVertex(pose, 1.0F, 0.0F, 0.0F).setColor(1.0F, 0.0F, 0.0F, 1.0F).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(1.0F);
					
					consumer.addVertex(pose, 0.0F, 0.0F, 0.0F).setColor(0.0F, 0.0F, 1.0F, 1.0F).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(1.0F);
					consumer.addVertex(pose, 0.0F, 0.0F, 1.0F).setColor(0.0F, 0.0F, 1.0F, 1.0F).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(1.0F);
				});
			}
			
			this.poseMatrix = stack.last().pose();
			
			this.render3D(stack, collector, mouseX, mouseY, partialTick);
			
			for (Renderable renderable : this.renderables)
			{
				if (renderable instanceof Widget3D widget)
					widget.submitAs3D(stack, collector, pMouseX, pMouseY, pPartialTick);
			}
			
		}, this.offset, rot, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false), pMouseX, pMouseY, 0, 0, this.width, this.height, this.zoom * this.zoomConstant, this.farPlane, graphics.peekScissorStack()));
		
		for (Renderable renderable : this.renderables) 
            renderable.extractRenderState(graphics, pMouseX, pMouseY, pPartialTick);
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	protected void render3D(PoseStack stack, SubmitNodeCollector collector, int mouseX, int mouseY, float partialTick) {}
	
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
}
