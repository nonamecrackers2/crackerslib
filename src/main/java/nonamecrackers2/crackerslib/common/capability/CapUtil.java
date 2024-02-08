package nonamecrackers2.crackerslib.common.capability;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class CapUtil
{
	public static void registerCap(AttachCapabilitiesEvent<?> event, ResourceLocation id, Capability<?> cap, final @Nullable NonNullSupplier<?> object)
	{
		LazyOptional<?> optional = LazyOptional.of(object);
		event.addCapability(id, new ICapabilityProvider()
		{
			@Override
			public <M> @NotNull LazyOptional<M> getCapability(@NotNull Capability<M> in, @Nullable Direction side)
			{
				return in == cap ? optional.cast() : LazyOptional.empty();
			}
		});
		event.addListener(optional::invalidate);
	}
	
	public static <T extends TagSerializable> void registerSerializableCap(AttachCapabilitiesEvent<?> event, ResourceLocation id, Capability<?> cap, final @Nullable NonNullSupplier<T> object)
	{
		LazyOptional<T> optional = LazyOptional.of(object);
		event.addCapability(id, new ICapabilitySerializable<CompoundTag>()
		{

			@Override
			public <M> @NotNull LazyOptional<M> getCapability(@NotNull Capability<M> in, @Nullable Direction side)
			{
				return in == cap ? optional.cast() : LazyOptional.empty();
			}

			@Override
			public CompoundTag serializeNBT()
			{
				return optional.orElse(null).write();
			}

			@Override
			public void deserializeNBT(CompoundTag nbt)
			{
				optional.orElse(null).read(nbt);
			}
		});
	}
}
