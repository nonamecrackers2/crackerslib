package nonamecrackers2.crackerslib.common.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public class PacketHelper
{
	public static final StreamCodec<ByteBuf, BlockPos> BLOCK_POS = new StreamCodec<ByteBuf, BlockPos>()
	{
		@Override
		public void encode(ByteBuf buffer, BlockPos pos)
		{
			FriendlyByteBuf.writeBlockPos(buffer, pos);
		}
		
		@Override
		public BlockPos decode(ByteBuf buffer)
		{
			return FriendlyByteBuf.readBlockPos(buffer);
		}
	};
	
	public static <T extends Enum<T> & StringRepresentable> StreamCodec<ByteBuf, T> enumStreamCodec(Class<T> enumClass)
	{
		return ByteBufCodecs.STRING_UTF8.map(str -> enumFromSerializableName(enumClass, str), StringRepresentable::getSerializedName);
	}
	
	public static <T extends Enum<T> & StringRepresentable> T enumFromSerializableName(Class<T> enumClass, String id)
	{
		for (T value : enumClass.getEnumConstants())
		{
			if (value.getSerializedName().equals(id))
				return value;
		}
		throw new NullPointerException();
	}
}
