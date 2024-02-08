package nonamecrackers2.crackerslib.common.util;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeConfigSpec;

public class AttributeModifierSnapshot 
{
	private final Supplier<Attribute> attribute;
	private final UUID id;
	private final String description;
	private final ForgeConfigSpec.ConfigValue<Double> value;
	private final AttributeModifier.Operation operation;
	
	public AttributeModifierSnapshot(Supplier<Attribute> attribute, UUID id, String description, ForgeConfigSpec.ConfigValue<Double> value, AttributeModifier.Operation operation)
	{
		this.attribute = attribute;
		this.id = id;
		this.description = description;
		this.value = value;
		this.operation = operation;
	}
	
	public Attribute getAttribute()
	{
		return this.attribute.get();
	}
	
	public UUID getId()
	{
		return this.id;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public double getValue()
	{
		return this.value.get();
	}
	
	public AttributeModifier.Operation getOperation()
	{
		return this.operation;
	}
	
	public AttributeModifier applyModifier(AttributeMap manager)
	{
		AttributeInstance instance = manager.getInstance(this.getAttribute());
		double value = this.getValue() - instance.getBaseValue();
		AttributeModifier modifier = new AttributeModifier(this.getId(), this.getDescription(), value, this.getOperation());
		instance.removeModifier(modifier);
		instance.addPermanentModifier(modifier);
		return modifier;
	}
}
