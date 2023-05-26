package nonamecrackers2.crackerslib.packet;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public abstract class Packet
{
	protected static final Logger LOGGER = LogManager.getLogger();
	
	protected boolean isValid;
	
	public Packet(boolean valid)
	{
		this.isValid = valid;
	}
	
	public boolean isMessageValid()
	{
		return this.isValid;
	}
	
	protected abstract void encode(FriendlyByteBuf buffer);
	
	protected abstract void decode(FriendlyByteBuf buffer);
	
	public static <T extends Packet> void encodeCheck(T packet, FriendlyByteBuf buffer)
	{
		if (!packet.isValid) return;
		packet.encode(buffer);
	}
	
	public static <T extends Packet> T decode(Supplier<T> blank, FriendlyByteBuf buffer)
	{
		T message = blank.get();
		try
		{
			message.decode(buffer);
		}
		catch (IllegalArgumentException | IndexOutOfBoundsException | DecoderException e)
		{
			LOGGER.warn("Exception while reading " + message.toString() + "; " + e);
			e.printStackTrace();
			return message;
		}
		message.isValid = true;
		return message;
	}
	
	public abstract Runnable getProcessor(NetworkEvent.Context context);
	
	protected static Runnable client(Runnable processor)
	{
		return () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> processor);
	}
}
