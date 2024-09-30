package nonamecrackers2.crackerslib.client.gui.widget;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import nonamecrackers2.crackerslib.client.util.CommonColors;

public class SelectableNamedObjectList<T> extends ObjectSelectionList<SelectableNamedObjectList.Entry<T>>
{
	private @Nullable Consumer<T> onObjectSelected;
	
	public SelectableNamedObjectList(Minecraft pMinecraft, int width, int height, int headerHeight)
	{
		super(pMinecraft, width, height, headerHeight, pMinecraft.font.lineHeight + 5);
	}
	
	public void setOnObjectSelectedCallback(Consumer<T> callback)
	{
		this.onObjectSelected = callback;
	}
	
	public void addObject(Component name, T object)
	{
		this.addEntry(new SelectableNamedObjectList.Entry<>(this, name, object));
	}
	
	@Override
	public int getRowWidth()
	{
		return this.getWidth();
	}
	
	public @Nullable T getSelectedObject()
	{
		if (this.getSelected() != null)
			return this.getSelected().object;
		else
			return null;
	}
	
	@Override
	protected int getScrollbarPosition()
	{
		return this.getX() + this.getWidth() - 5;
	}
	
	@Override
	protected void renderListBackground(GuiGraphics stack)
	{
		stack.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), CommonColors.BACKGROUND);
	}
	
	@Override
	public void setSelected(Entry<T> pSelected)
	{
		if (this.onObjectSelected != null && pSelected != null)
			this.onObjectSelected.accept(pSelected.object);
		super.setSelected(pSelected);
	}

	public static class Entry<T> extends ObjectSelectionList.Entry<SelectableNamedObjectList.Entry<T>>
	{
		private final SelectableNamedObjectList<T> list;
		private final Component text;
		private final T object;
		
		public Entry(SelectableNamedObjectList<T> list, Component text, T object)
		{
			this.list = list;
			this.text = text;
			this.object = object;
		}
		
		@Override
		public Component getNarration()
		{
			return this.text;
		}

		@Override
		public void render(GuiGraphics stack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick)
		{
			Font font = this.list.minecraft.font;
			stack.drawString(font, this.text, pLeft + 2, pTop + pHeight / 2 - font.lineHeight / 2, CommonColors.WHITE);
		}
		
		@Override
		public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
		{
			if (pButton == 0)
			{
				this.list.setSelected(this);
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
