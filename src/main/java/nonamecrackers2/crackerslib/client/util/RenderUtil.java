package nonamecrackers2.crackerslib.client.util;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class RenderUtil
{
	private RenderUtil() {}
	
	public static void extractCenteredWordWrap(GuiGraphicsExtractor graphics, Font font, FormattedText text, int x, int y, int width, int color)
	{
		List<FormattedCharSequence> texts = font.split(text, width);
		int totalHeight = texts.size() * (font.lineHeight + 2);
		for (int i = 0; i < texts.size(); i++)
			graphics.centeredText(font, texts.get(i), x, y + i * font.lineHeight + 2 - totalHeight / 2, color);
	}
	
	public static void extractHorizontallyCenteredWordWrap(GuiGraphicsExtractor graphics, Font font, FormattedText text, int x, int y, int width, int color)
	{
		List<FormattedCharSequence> texts = font.split(text, width);
		for (int i = 0; i < texts.size(); i++)
			graphics.centeredText(font, texts.get(i), x, y + i * font.lineHeight + 2, color);
	}
	
	public static Vector3f getWorldPosFromScreenPos(Matrix4f mat, int screenX, int screenY, float z)
	{
		Matrix4f inverse = new Matrix4f(mat).invert();
		Vector4f vec4 = new Vector4f(screenX, screenY, z, 1.0F).mul(inverse);
		float w = 1.0F / vec4.w;
		return new Vector3f(vec4.x * w, vec4.y * w, vec4.z * w);
	}
	
	public static Vector2f getScreenCoordinatesFromWorldPos(Matrix4f mat, Vector3f pos)
	{
		Vector4f vector4f = mat.transform(new Vector4f(pos.x, pos.y, pos.z, 1.0F));
		return new Vector2f(vector4f.x, vector4f.y);
	}
	
	public static Vector3f getScreenCoordinatesFromWorldPosWithZDist(Matrix4f mat, Vector3f pos)
	{
		Vector4f vector4f = mat.transform(new Vector4f(pos.x, pos.y, pos.z, 1.0F));
		return new Vector3f(vector4f.x, vector4f.y, vector4f.z);
	}
	
	public static float getScreenZCoord(Matrix4f mat, Vector3f pos)
	{
		return mat.transform(new Vector4f(pos.x, pos.y, pos.z, 1.0F)).z();
	}
	
	/**
	 * Renders a cube sphere
	 * <p>
	 * Refactored from the sources below to work in Java
	 * 
	 * @see <a href="https://www.songho.ca/glsl/files/js/Cubesphere.js">Cubesphere.js</a>
	 * @see <a href="https://www.songho.ca/opengl/gl_sphere.html">OpenGL Sphere</a>
	 * 
	 * @param subdivision The amount to subdivide the cube
	 * @param radius The radius of the cube
	 * @param stack
	 * @param consumer
	 * @param packedLight
	 * @param overlayTexture
	 */
	public static void renderCubeSphere(int subdivision, float radius, PoseStack stack, VertexConsumer consumer, int packedLight, int overlayTexture, boolean useNormals)
	{
		int pointsPerRow = (int)(Math.pow(2, subdivision) + 1);
		List<Vector3f> unitVertices = generateCubeSpherePosXVertices(pointsPerRow);
		
		List<Vector3f> vertices = Lists.newArrayList();
		List<Vector3f> normals = Lists.newArrayList();
		List<Vector2f> texCoords = Lists.newArrayList();
		List<Integer> indices = Lists.newArrayList();
		
        //+X face
        int k = 0;
        for(int i = 0; i < pointsPerRow; i++)
        {
            int k1 = i * pointsPerRow;
            int k2 = k1 + pointsPerRow;
            float t = (float)i / ((float)pointsPerRow - 1.0F);
            for(int j = 0; j < pointsPerRow; j++, k1++, k2++)
            {
            	Vector3f vertex = unitVertices.get(k);
                float x = vertex.x;
                float y = vertex.y;
                float z = vertex.z;
                float s = (float)j / ((float)pointsPerRow - 1.0F);
                vertices.add(new Vector3f(x*radius, y*radius, z*radius));
                normals.add(new Vector3f(x, y, z));
                texCoords.add(new Vector2f(1.0F - s / 2.0F, t / 3.0F + 1.0F/3.0F));
                if(i < (pointsPerRow-1) && j < (pointsPerRow-1))
                {
                	indices.add(k1); 
                	indices.add(k2); 
                	indices.add(k1+1);
                    indices.add(k1+1); 
                    indices.add(k2); 
                    indices.add(k2+1);
                }
                k++;
            }
        }
        
        int vertexSize = pointsPerRow * pointsPerRow;
        int indexSize = 6 * (int)Math.pow(4, subdivision);
        int startIndex;

        //-X face
        for(int i = 0; i < vertexSize; i++)
        {
        	Vector3f vertex = vertices.get(i);
        	Vector3f normal = normals.get(i);
        	Vector2f texCoord = texCoords.get(i);
        	vertices.add(new Vector3f(-vertex.x, vertex.y, -vertex.z));
        	normals.add(new Vector3f(-normal.x, normal.y, -normal.z));
        	texCoords.add(new Vector2f(texCoord.x + 1.0F/2.0F, texCoord.y));
        }
        startIndex = vertexSize;
        for(int i = 0; i < indexSize; ++i)
        	indices.add(startIndex + indices.get(i));
        
        //+Y face
        for(int i = 0; i < vertexSize; i++)
        {
        	Vector3f vertex = vertices.get(i);
        	Vector3f normal = normals.get(i);
        	Vector2f texCoord = texCoords.get(i);
        	vertices.add(new Vector3f(-vertex.z, vertex.x, -vertex.y));
        	normals.add(new Vector3f(-normal.z, normal.x, -normal.y));
        	texCoords.add(new Vector2f(texCoord.x - 1.0F/2.0F, texCoord.y - 1.0F/3.0F));
        }
        startIndex = vertexSize * 2;
        for(int i = 0; i < indexSize; ++i)
        	indices.add(startIndex + indices.get(i));

        //-Y face
        for(int i = 0; i < vertexSize; i++)
        {
            Vector3f vertex = vertices.get(i);
        	Vector3f normal = normals.get(i);
        	Vector2f texCoord = texCoords.get(i);
        	vertices.add(new Vector3f(-vertex.z, -vertex.x, vertex.y));
        	normals.add(new Vector3f(-normal.z, -normal.x, normal.y));
        	texCoords.add(new Vector2f(texCoord.x, texCoord.y - 1.0F/3.0F));
        }
        startIndex = vertexSize * 3;
        for(int i = 0; i < indexSize; ++i)
        	indices.add(startIndex + indices.get(i));

        //+Z face
        for(int i = 0; i < vertexSize; i++)
        {
        	Vector3f vertex = vertices.get(i);
        	Vector3f normal = normals.get(i);
        	Vector2f texCoord = texCoords.get(i);
        	vertices.add(new Vector3f(-vertex.z, vertex.y, vertex.x));
        	normals.add(new Vector3f(-normal.z, normal.y, normal.x));
        	texCoords.add(new Vector2f(texCoord.x, texCoord.y + 1.0F/3.0F));
        }
        startIndex = vertexSize * 4;
        for(int i = 0; i < indexSize; ++i)
        	indices.add(startIndex + indices.get(i));
        
        //-Z face
        for(int i = 0; i < vertexSize; i++)
        {
        	Vector3f vertex = vertices.get(i);
        	Vector3f normal = normals.get(i);
        	Vector2f texCoord = texCoords.get(i);
        	vertices.add(new Vector3f(vertex.z, vertex.y, -vertex.x));
        	normals.add(new Vector3f(normal.z, normal.y, -normal.x));
        	texCoords.add(new Vector2f(texCoord.x + 1.0F/2.0F, texCoord.y + 1.0F/3.0F));
        }
        startIndex = vertexSize * 5;
        for(int i = 0; i < indexSize; ++i)
        	indices.add(startIndex + indices.get(i));
        
        Matrix4f matrix4f = stack.last().pose();
		for (int i = 0; i < indices.size(); i++)
		{
			int index = indices.get(i);
			Vector3f vertex = vertices.get(index);
			Vector3f normal = normals.get(index);
			Vector2f uv = texCoords.get(index);
			consumer.addVertex(matrix4f, vertex.x, vertex.y, vertex.z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(uv.x, uv.y).setOverlay(overlayTexture).setLight(packedLight);
			if (useNormals)
				consumer.setNormal(stack.last(), normal.x, normal.y, normal.z);
			else
				consumer.setNormal(0.0F, -1.0F, 0.0F);
		}
	}
	
	private static List<Vector3f> generateCubeSpherePosXVertices(int pointsPerRow)
	{
		float D2R = (float)Math.acos(-1.0F) / 180.0F;
		List<Vector3f> vertices = Lists.newArrayList();
		
	    for(int i = 0; i < pointsPerRow; ++i)
	    {
	        float a2 = D2R * (45.0F - 90.0F * (float)i / ((float)pointsPerRow - 1.0F));
	        Vector3f n2 = new Vector3f((float)-Math.sin(a2), (float)Math.cos(a2), 0.0F);
	        for(int j = 0; j < pointsPerRow; ++j)
	        {
	        	float a1 = D2R * (-45.0F + 90.0F * (float)j / ((float)pointsPerRow - 1.0F));
	            Vector3f n1 = new Vector3f((float)-Math.sin(a1), 0.0F, (float)-Math.cos(a1));
	            Vector3f v = new Vector3f(
	            		n1.y * n2.z - n1.z * n2.y,
	            		n1.z * n2.x - n1.x * n2.z,
	            		n1.x * n2.y - n1.y * n2.x
	            ).normalize();
	            vertices.add(v);
	        }
	    }
	    
	    return vertices;
	}
	
	/**
	 * Renders a sphere made out of sectors and stacks
	 * <p>
	 * Refactored from the source below to work in Java
	 * 
	 * @see <a href="https://www.songho.ca/opengl/gl_sphere.html">OpenGL Sphere</a>
	 * 
	 * @param radius
	 * @param sectorCount Typically 72
	 * @param stackCount Typically 24
	 * @param stack
	 * @param consumer
	 * @param packedLight
	 * @param overlayTexture
	 */
	public static void renderSectorStackSphere(float radius, int sectorCount, int stackCount, PoseStack stack, VertexConsumer consumer, int packedLight, int overlayTexture)
	{
		List<Vector3f> vertices = Lists.newArrayList();
		List<Vector3f> normals = Lists.newArrayList();
		List<Vector2f> texCoords = Lists.newArrayList();
		
		List<Integer> indices = Lists.newArrayList();
		
		float lengthInv = 1.0F / radius;
		
		float sectorStep = (float)Math.PI*2.0F / (float)sectorCount;
		float stackStep = (float)Math.PI / (float)stackCount;

		for(int i = 0; i <= stackCount; ++i)
		{
			float stackAngle = (float)Math.PI / 2.0F - (float)i * stackStep;
		    float xy = radius * Mth.cos(stackAngle);
		    float z = radius * Mth.sin(stackAngle);

		    for(int j = 0; j <= sectorCount; ++j)
		    {
		        float sectorAngle = j * sectorStep;

		        float x = xy * Mth.cos(sectorAngle);
		        float y = xy * Mth.sin(sectorAngle);
		        vertices.add(new Vector3f(x, y, z));

		        float nx = x * lengthInv;
		        float ny = y * lengthInv;
		        float nz = z * lengthInv;
		        normals.add(new Vector3f(nx, ny, nz));
		        
		        float u = (float)j / sectorCount;
		        float v = (float)i / stackCount;
		        texCoords.add(new Vector2f(u, v));
		    }
		}
		
		for(int i = 0; i < stackCount; ++i)
		{
		    int k1 = i * (sectorCount + 1); 
		    int k2 = k1 + sectorCount + 1;

		    for(int j = 0; j < sectorCount; ++j, ++k1, ++k2)
		    {
		        if(i != 0)
		        {
		            indices.add(k1);
		            indices.add(k2);
		            indices.add(k1 + 1);
		        }

		        if(i != (stackCount-1))
		        {
		            indices.add(k1 + 1);
		            indices.add(k2);
		            indices.add(k2 + 1);
		        }
		    }
		}
		
		Matrix4f matrix4f = stack.last().pose();
		for (int i = 0; i < indices.size(); i++)
		{
			int index = indices.get(i);
			Vector3f vertex = vertices.get(index);
			Vector3f normal = normals.get(index);
			Vector2f uv = texCoords.get(index);
			consumer.addVertex(matrix4f, vertex.x, vertex.y, vertex.z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(uv.x, uv.y).setOverlay(overlayTexture).setLight(packedLight).setNormal(stack.last(), normal.x, normal.y, normal.z);
		}
	}
	
	public static boolean isMouseInBounds(int mouseX, int mouseY, int x, int y, int width, int height)
	{
		return mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height;
	}
	
	public static boolean isMouseInBounds(int mouseX, int mouseY, ScreenRectangle rectangle)
	{
		return isMouseInBounds(mouseX, mouseY, rectangle.position().x(), rectangle.position().y(), rectangle.width(), rectangle.height());
	}
}
