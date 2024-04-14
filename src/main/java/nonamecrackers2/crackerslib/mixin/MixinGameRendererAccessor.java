package nonamecrackers2.crackerslib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public interface MixinGameRendererAccessor
{
	@Invoker("getFov")
	double crackerslib$getFov(Camera camera, float partialTicks, boolean useFOVSetting);

	@Accessor("zoom")
	float crackerslib$getZoom();
	
	@Accessor("zoomX")
	float crackerslib$getZoomX();
	
	@Accessor("zoomY")
	float crackerslib$getZoomY();
}
