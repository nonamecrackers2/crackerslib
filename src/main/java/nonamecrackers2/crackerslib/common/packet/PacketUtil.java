package nonamecrackers2.crackerslib.common.packet;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketUtil
{
	private static @Nullable Throwable lastException;
	private static final Map<SimpleChannel, AtomicInteger> CURRENT_IDS = Maps.newHashMap();
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static <T extends Packet> void registerToClient(SimpleChannel channel, Class<T> clazz)
	{
		channel.registerMessage(
				CURRENT_IDS.computeIfAbsent(channel, c -> new AtomicInteger()).incrementAndGet(), 
				clazz, 
				Packet::encodeCheck, 
				buffer -> Packet.decode(() -> {
					try
					{
						return clazz.getDeclaredConstructor().newInstance();
					}
					catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
					{
						LOGGER.error("Failed to create blank packet from class {}", clazz);
						e.printStackTrace();
						return null;
					}
				}, buffer),
				PacketUtil::receiveClientMessage,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT)
		);
	}
	
	public static <T extends Packet> void registerToServer(SimpleChannel channel, Class<T> clazz)
	{
		channel.registerMessage(
				CURRENT_IDS.computeIfAbsent(channel, c -> new AtomicInteger()).incrementAndGet(), 
				clazz, 
				Packet::encodeCheck, 
				buffer -> Packet.decode(() -> {
					try
					{
						return clazz.getDeclaredConstructor().newInstance();
					}
					catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
					{
						LOGGER.error("Failed to create blank packet from class {}", clazz);
						e.printStackTrace();
						return null;
					}
				}, buffer),
				PacketUtil::receiveServerMessage,
				Optional.of(NetworkDirection.PLAY_TO_SERVER)
		);
	}
	
	private static <T extends Packet> void receiveClientMessage(final T message, Supplier<NetworkEvent.Context> supplier)
	{
		NetworkEvent.Context context = supplier.get();
		LogicalSide sideReceived = context.getDirection().getReceptionSide();
		context.setPacketHandled(true);
		
		if (sideReceived != LogicalSide.CLIENT)
		{
			LOGGER.warn(message.toString() + " was received on the wrong side: " + sideReceived);
			return;
		}
		
		if (!message.isMessageValid())
		{
			LOGGER.warn(message.toString() + " was invalid");
			return;
		}
		
		context.enqueueWork(message.getProcessor(context)).handle((v, e) -> 
		{
			if (e != null)
			{
				if (lastException == null || !lastException.getClass().equals(e.getClass()))
				{
					LOGGER.error("Failed to process packet {}: {}", message, e);
					e.printStackTrace();
				}
				lastException = e;
			}
			return v;
		});
	}
	
	private static <T extends Packet> void receiveServerMessage(final T message, Supplier<NetworkEvent.Context> supplier)
	{
		NetworkEvent.Context context = supplier.get();
		LogicalSide sideReceived = context.getDirection().getReceptionSide();
		context.setPacketHandled(true);
		
		if (sideReceived != LogicalSide.SERVER)
		{
			LOGGER.warn(message.toString() + " was received on the wrong side: " + sideReceived);
			return;
		}
		
		if (!message.isMessageValid())
		{
			LOGGER.warn(message.toString() + " was invalid");
			return;
		}
		
		final ServerPlayer player = context.getSender();
		if (player == null)
		{
			LOGGER.warn("The sending player is not present when " + message.toString() + " was received");
			return;
		}
		
		context.enqueueWork(message.getProcessor(context)).handle((v, e) -> 
		{
			if (e != null)
			{
				if (lastException == null || !lastException.getClass().equals(e.getClass()))
				{
					LOGGER.error("Failed to process packet {}: {}", message, e);
					e.printStackTrace();
				}
				lastException = e;
			}
			return v;
		});
	}
}
