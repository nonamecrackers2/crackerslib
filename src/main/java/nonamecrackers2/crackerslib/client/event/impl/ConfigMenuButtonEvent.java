package nonamecrackers2.crackerslib.client.event.impl;

import javax.annotation.Nullable;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import nonamecrackers2.crackerslib.client.gui.ConfigMenuButtons;

public class ConfigMenuButtonEvent extends Event implements IModBusEvent
{
	private final String modid;
	private @Nullable ConfigMenuButtons.Factory factory;
	
	public ConfigMenuButtonEvent(String modid)
	{
		this.modid = modid;
	}
	
	public void registerFactory(ConfigMenuButtons.Factory factory)
	{
		this.factory = factory;
	}
	
	public void defaultButtonWithSingleCharacter(char character, int color)
	{
		this.factory = (onPress, tooltip) -> 
		{
			Button button = new Button(0, 0, 20, 20, Component.literal(String.valueOf(character)), onPress, tooltip);
			button.setFGColor(color);
			return button;
		};
	}
	
	public String getModId()
	{
		return this.modid;
	}
	
	public @Nullable ConfigMenuButtons.Factory getFactory()
	{
		return this.factory;
	}
}
