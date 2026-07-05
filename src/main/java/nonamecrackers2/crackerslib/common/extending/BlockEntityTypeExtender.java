package nonamecrackers2.crackerslib.common.extending;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import nonamecrackers2.crackerslib.mixin.MixinBlockEntityType;

public class BlockEntityTypeExtender
{
	public static void addToBlockEntityType(BlockEntityType<?> type, Block... blocks)
	{
		MixinBlockEntityType mixin = (MixinBlockEntityType)type;
		Set<Block> currentBlocks = new HashSet<>(type.getValidBlocks());
		for (Block block : blocks)
			currentBlocks.add(block);
		mixin.crackerslib$setValidBlocks(currentBlocks);
	}
}
