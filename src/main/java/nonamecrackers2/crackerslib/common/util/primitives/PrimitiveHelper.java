package nonamecrackers2.crackerslib.common.util.primitives;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class PrimitiveHelper
{
	public static CompoundTag quaternionToTag(Quaternionf quaternion)
	{
		CompoundTag tag = new CompoundTag();
		tag.putFloat("x", quaternion.x());
		tag.putFloat("y", quaternion.y());
		tag.putFloat("z", quaternion.z());
		tag.putFloat("w", quaternion.w());
		return tag;
	}

	public static Quaternionf quaternionFromTag(CompoundTag tag)
	{
		Quaternionf quaternion = new Quaternionf().identity();
		quaternion.x = tag.getFloatOr("x", 0.0F);
		quaternion.y = tag.getFloatOr("y", 0.0F);
		quaternion.z = tag.getFloatOr("z", 0.0F);
		quaternion.w = tag.getFloatOr("w", 0.0F);
		return quaternion;
	}
	
	public static CompoundTag vector3fToTag(Vector3f vec)
	{
		CompoundTag tag = new CompoundTag();
		tag.putFloat("x", vec.x);
		tag.putFloat("y", vec.y);
		tag.putFloat("z", vec.z);
		return tag;
	}
	
	public static Vector3f vector3fFromTag(CompoundTag tag)
	{
		return new Vector3f(tag.getFloatOr("x", 0.0F), tag.getFloatOr("y", 0.0F), tag.getFloatOr("z", 0.0F));
	}
	
	public static CompoundTag vec3ToTag(Vec3 vec)
	{
		CompoundTag tag = new CompoundTag();
		tag.putDouble("x", vec.x);
		tag.putDouble("y", vec.y);
		tag.putDouble("z", vec.z);
		return tag;
	}
	
	public static Vec3 vec3FromTag(CompoundTag tag)
	{
		return new Vec3(tag.getDoubleOr("x", 0.0D), tag.getDoubleOr("y", 0.0D), tag.getDoubleOr("z", 0.0D));
	}
	
	public static CompoundTag chunkPosToTag(ChunkPos pos)
	{
		CompoundTag tag = new CompoundTag();
		tag.putInt("chunkX", pos.x());
		tag.putInt("chunkZ", pos.z());
		return tag;
	}
	
	public static ChunkPos chunkPosFromTag(CompoundTag tag)
	{
		int x = tag.getIntOr("chunkX", 0);
		int z = tag.getIntOr("chunkZ", 0);
		return new ChunkPos(x, z);
	}
	
	public static CompoundTag vec2ToTag(Vec2 vec)
	{
		CompoundTag tag = new CompoundTag();
		tag.putDouble("x", vec.x);
		tag.putDouble("y", vec.y);
		return tag;
	}
	
	public static Vec2 vec2FromTag(CompoundTag tag)
	{
		return new Vec2(tag.getFloatOr("x", 0.0F), tag.getFloatOr("y", 0.0F));
	}
	
	public static void encodeVec3(FriendlyByteBuf buffer, Vec3 vec)
	{
		buffer.writeDouble(vec.x);
		buffer.writeDouble(vec.y);
		buffer.writeDouble(vec.z);
	}
	
	public static Vec3 decodeVec3(FriendlyByteBuf buffer)
	{
		return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
	}
	
	public static void saveEnum(Enum<?> enub, CompoundTag tag, String id)
	{
		tag.putInt(id, enub.ordinal());
	}
	
	public static <T extends Enum<T>> T readEnum(Class<T> clazz, CompoundTag tag, String id)
	{
		T[] values = clazz.getEnumConstants();
		int ordinal = tag.getIntOr(id, -1);
		if (ordinal < values.length && ordinal >= 0)
			return values[ordinal];
		else
			return values[0];
	}
	
	public static Vector3f vec3ToVector3f(Vec3 vec)
	{
		return new Vector3f((float)vec.x, (float)vec.y, (float)vec.z);
	}
	
	public static Vec3 vector3fToVec3(Vector3f vec)
	{
		return new Vec3(vec);
	}
	
	public static Vec3 rotate(Vec3 vec, Quaternionf rotation)
	{
		Vector3f vecF = vec3ToVector3f(vec);
		vecF.rotate(rotation);
		return vector3fToVec3(vecF);
	}
}
