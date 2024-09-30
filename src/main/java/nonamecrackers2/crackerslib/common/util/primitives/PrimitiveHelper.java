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
		if (tag.contains("x", 5))
			quaternion.x = tag.getFloat("x");
		if (tag.contains("y", 5))
			quaternion.y = tag.getFloat("y");
		if (tag.contains("z", 5))
			quaternion.z = tag.getFloat("z");
		if (tag.contains("w", 5))
			quaternion.w = tag.getFloat("w");
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
		return new Vector3f(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"));
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
		return new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
	}
	
	public static CompoundTag chunkPosToTag(ChunkPos pos)
	{
		CompoundTag tag = new CompoundTag();
		tag.putInt("chunkX", pos.x);
		tag.putInt("chunkZ", pos.z);
		return tag;
	}
	
	public static ChunkPos chunkPosFromTag(CompoundTag tag)
	{
		int x = tag.getInt("chunkX");
		int z = tag.getInt("chunkZ");
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
		return new Vec2(tag.getFloat("x"), tag.getFloat("y"));
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
		int ordinal = tag.getInt(id);
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
