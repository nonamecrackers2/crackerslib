package nonamecrackers2.crackerslib.common.util.nbt;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class NbtHelper
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
}
