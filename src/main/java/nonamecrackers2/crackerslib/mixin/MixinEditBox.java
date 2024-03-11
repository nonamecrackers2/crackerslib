package nonamecrackers2.crackerslib.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import nonamecrackers2.crackerslib.client.util.EditBoxAccessor;

@Mixin(EditBox.class)
public abstract class MixinEditBox extends AbstractWidget implements EditBoxAccessor
{
	private @Nullable Component hint;
	
	private MixinEditBox(int x, int y, int width, int height, Component text)
	{
		super(x, y, width, height, text);
	}

	@Override
	public void setHint(Component text)
	{
		this.hint = text;
	}
	
	@Inject(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;getMaxLength()I"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void crackerslib$renderHint_renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick, CallbackInfo ci, int i2, int j, int k, String s, boolean flag, boolean flag1, int l, int i1, int j1)
	{
		if (this.hint != null && s.isEmpty() && !this.isFocused())
			Minecraft.getInstance().font.drawShadow(stack, this.hint, (float)j1, (float)i1, i2);
			
	}
}
