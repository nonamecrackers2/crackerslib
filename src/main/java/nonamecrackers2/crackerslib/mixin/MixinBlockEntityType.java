package nonamecrackers2.crackerslib.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Mixin(BlockEntityType.class)
public interface MixinBlockEntityType
{
	@Accessor("validBlocks")
	public Set<Block> crackerslib$getValidBlocks();
	
	@Mutable
	@Accessor("validBlocks")
	public void crackerslib$setValidBlocks(Set<Block> block);
}
