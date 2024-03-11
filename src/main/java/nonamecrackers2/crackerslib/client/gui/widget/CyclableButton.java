package nonamecrackers2.crackerslib.client.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class CyclableButton<T> extends Button
{
	private final List<T> values;
	private final Function<T, Component> messageGetter;
	private @Nullable Consumer<T> responder;
	private int index;
	
	public CyclableButton(int x, int y, int width, List<T> values, T current, Function<T, Component> messageGetter)
	{
		super(x, y, width, 20, messageGetter.apply(current), b -> {});
		this.messageGetter = messageGetter;
		this.values = values;
		this.setValue(current);
	}
	
	public CyclableButton(int x, int y, int width, List<T> values, T current)
	{
		this(x, y, width, values, current, val -> Component.literal(val.toString()));
	}
	
	public void setResponder(Consumer<T> responder)
	{
		this.responder = responder;
	}
	
	@Override
	public void onPress()
	{
		this.index++;
		if (this.index >= this.values.size())
			this.index = 0;
		this.setMessage(this.messageGetter.apply(this.getValue()));
		if (this.responder != null)
			this.responder.accept(this.getValue());
	}
	
	public T getValue()
	{
		return this.values.get(this.index);
	}
	
	public void setValue(T value)
	{
		this.index = this.values.indexOf(value);
		if (this.index == -1)
			throw new IllegalArgumentException("'" + value + "' is not a valid value!");
		this.setMessage(this.messageGetter.apply(value));
	}
}
