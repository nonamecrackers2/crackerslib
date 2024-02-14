package nonamecrackers2.crackerslib.client.gui.widget.config.entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraftforge.common.ForgeConfigSpec;

public class StringConfigEntry extends ConfigEntry<String, EditBox>
{
	public StringConfigEntry(Minecraft mc, String modid, String path, ForgeConfigSpec spec, Runnable onValueUpdated)
	{
		super(mc, modid, path, spec, onValueUpdated);
	}
	
	@Override
	protected EditBox buildWidget(int x, int y, int width, int height)
	{
		EditBox box = new EditBox(this.mc.font, x + 6, y + height / 2 - 10, 60, 20, CommonComponents.EMPTY);
		box.setValue(this.value.get());
		box.setResponder(value -> {
			this.getValueUpdatedResponder().run();
		});
		return box;
	}
	
	@Override
	protected String getCurrentValue()
	{
		return this.widget.getValue();
	}
	
	@Override
	protected void setCurrentValue(String value)
	{
		this.widget.setValue(value);
	}
}
