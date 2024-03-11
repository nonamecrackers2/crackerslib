package nonamecrackers2.crackerslib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.AbstractWidget;

@Mixin(AbstractWidget.class)
public interface MixinAbstractWidget
{
	@Accessor("isHovered")
	boolean crackerslib$getIsHovered();
}
