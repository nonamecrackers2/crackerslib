package nonamecrackers2.crackerslib.client.gui;

import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import nonamecrackers2.crackerslib.client.gui.widget.SelectableNamedObjectList;
import nonamecrackers2.crackerslib.client.util.CommonColors;

public class Popup extends Screen
{
	private static final Queue<Popup> POPUP_QUEUE = Queues.newArrayDeque();
	private static final int BUTTON_WIDTH = 80;
	private final @Nullable Screen previous;
	private final Popup.Initializer onInitialized;
	private final MultiLineLabel text;
	private final int boxWidth;
	private final int widgetsHeight;
	private int x;
	private int y;
	private int boxHeight;
	
	public Popup(@Nullable Screen previous, Popup.Initializer onInitialized, int width, int widgetsHeight, Component pMessage)
	{
		super(pMessage);
		this.text = MultiLineLabel.create(Minecraft.getInstance().font, pMessage, width - 20);
		this.previous = previous;
		this.onInitialized = onInitialized;
		this.boxWidth = width;
		this.widgetsHeight = widgetsHeight;
	}
	
	public static Popup createYesNoPopupWithCancel(@Nullable Screen screen, Runnable onAccepted, Runnable onNotAccepted, int width, Component message)
	{
		return new Popup(screen, (p, r) -> {
			GridLayout layout = new GridLayout().rowSpacing(5);
			GridLayout.RowHelper row = layout.createRowHelper(1);
			
			GridLayout yesNoLayout = row.addChild(new GridLayout().columnSpacing(10));
			GridLayout.RowHelper yesNoRow = yesNoLayout.createRowHelper(2);
			Button yes = yesNoRow.addChild(Button.builder(Component.translatable("gui.popup.yes"), b -> {
				p.close();
				onAccepted.run();
			}).width(BUTTON_WIDTH).build());
			Button no = yesNoRow.addChild(Button.builder(Component.translatable("gui.popup.no"), b -> {
				p.close();
				onNotAccepted.run();
			}).width(BUTTON_WIDTH).build());
			
			GridLayout cancelLayout = row.addChild(new GridLayout());
			GridLayout.RowHelper cancelRow = cancelLayout.createRowHelper(1);
			
			Button cancel = cancelRow.addChild(Button.builder(Component.translatable("gui.popup.cancel"), b -> {
				p.close();
			}).width(BUTTON_WIDTH * 2 + 10).build());
			
			layout.arrangeElements();
			FrameLayout.centerInRectangle(layout, r);
			p.addRenderableWidget(yes);
			p.addRenderableWidget(no);
			p.addRenderableWidget(cancel);
		}, width, 45, message).open();
	}
	
	public static Popup createYesNoPopup(@Nullable Screen screen, Runnable onAccepted, Runnable onNotAccepted, int width, Component message)
	{
		return new Popup(screen, (p, r) -> {
			GridLayout layout = new GridLayout().columnSpacing(10);
			GridLayout.RowHelper row = layout.createRowHelper(2);
			Button yes = row.addChild(Button.builder(Component.translatable("gui.popup.yes"), b -> {
				p.close();
				onAccepted.run();
			}).width(BUTTON_WIDTH).build());
			Button no = row.addChild(Button.builder(Component.translatable("gui.popup.no"), b -> {
				p.close();
				onNotAccepted.run();
			}).width(BUTTON_WIDTH).build());
			layout.arrangeElements();
			FrameLayout.centerInRectangle(layout, r);
			p.addRenderableWidget(yes);
			p.addRenderableWidget(no);
		}, width, 20, message).open();
	}
	
	public static Popup createYesNoPopup(@Nullable Screen screen, Runnable onAccepted, int width, Component message)
	{
		return createYesNoPopup(screen, onAccepted, () -> {}, width, message);
	}
	
	public static Popup createTextFieldPopup(@Nullable Screen screen, Consumer<String> onAccepted, int width, Component message, Predicate<String> filter)
	{
		Minecraft mc = Minecraft.getInstance();
		return new Popup(screen, (p, r) -> {
			GridLayout layout = new GridLayout();
			layout.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle().padding(5);
			GridLayout.RowHelper row = layout.createRowHelper(1);
			
			GridLayout textLayout = row.addChild(new GridLayout());
			GridLayout.RowHelper textSubmit = textLayout.createRowHelper(1);
			EditBox box = textSubmit.addChild(new EditBox(mc.font, 0, 0, width / 2, 20, CommonComponents.EMPTY));
			box.setFilter(filter);
			
			GridLayout buttonLayout = row.addChild(new GridLayout());
			buttonLayout.defaultCellSetting().paddingHorizontal(5);
			GridLayout.RowHelper buttonRow = buttonLayout.createRowHelper(2);
			
			Button submit = buttonRow.addChild(Button.builder(Component.translatable("gui.popup.submit"), b -> {
				p.close();
				onAccepted.accept(box.getValue());
			}).width(80).build());
			Button cancel = buttonRow.addChild(Button.builder(Component.translatable("gui.popup.cancel"), b -> {
				p.close();
			}).width(80).build());
			
			layout.arrangeElements();
			FrameLayout.centerInRectangle(layout, r);
			
			p.addRenderableWidget(box);
			p.addRenderableWidget(submit);
			p.addRenderableWidget(cancel);
		}, width, 45, message).open();
	}
	
	public static Popup createTextFieldPopup(@Nullable Screen screen, Consumer<String> onAccepted, int width, Component message)
	{
		return createTextFieldPopup(screen, onAccepted, width, message, str -> true);
	}
	
	public static <T> Popup createOptionListPopup(@Nullable Screen screen, Consumer<SelectableNamedObjectList<T>> valueApplier, Consumer<T> onAccepted, int width, int listHeight, Component message)
	{
		Minecraft mc = Minecraft.getInstance();
		return new Popup(screen, (p, r) -> {
			GridLayout layout = new GridLayout();
			layout.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle().padding(5);
			GridLayout.RowHelper row = layout.createRowHelper(2);
			
			int listWidth = (int)((float)width / 1.2F);
			int listY = r.top();
			SelectableNamedObjectList<T> list = new SelectableNamedObjectList<>(mc, listWidth, listHeight, listY, listY + listHeight);
			list.setLeftPos(p.boxX() + p.boxWidth() / 2 - list.getWidth() / 2);
			valueApplier.accept(list);
			
			Button select = row.addChild(Button.builder(Component.translatable("gui.popup.select"), b -> {
				p.close();
				onAccepted.accept(list.getSelectedObject());
			}).width(80).build());
			select.active = false;
			list.setOnObjectSelectedCallback(t -> select.active = true);
			
			Button cancel = row.addChild(Button.builder(Component.translatable("gui.popup.cancel"), b -> {
				p.close();
			}).width(80).build());
			
			layout.arrangeElements();
			FrameLayout.centerInRectangle(layout, r.left(), list.getBottom() + 5, r.width(), 30);
			
			p.addRenderableWidget(select);
			p.addRenderableWidget(cancel);
			p.addRenderableWidget(list);
		}, width, listHeight + 30, message).open();
	}
	
	public static Popup createInfoPopup(@Nullable Screen screen, int width, Component message)
	{
		return new Popup(screen, (p, r) -> {
			int buttonWidth = 100;
			Button close = Button.builder(Component.translatable("gui.popup.close"), b -> {
				p.close();
			}).pos(p.boxX() + width / 2 - buttonWidth / 2, p.boxY() + p.boxHeight() - 30).width(buttonWidth).build();
			p.addRenderableWidget(close);
		}, width, 20, message).open();
	}
	
	public int boxX()
	{
		return this.x;
	}
	
	public int boxY()
	{
		return this.y;
	}
	
	public int boxWidth()
	{
		return this.boxWidth;
	}
	
	public int boxHeight()
	{
		return this.boxHeight;
	}
	
	@Override
	protected void init()
	{
		this.boxHeight = 40 + this.messageHeight() + this.widgetsHeight;
		this.x = this.width / 2 - this.boxWidth / 2;
		this.y = this.height / 2 - this.boxHeight / 2;
		ScreenRectangle widgetsRectangle = new ScreenRectangle(this.x, this.messageTop() + this.messageHeight() + 10, this.boxWidth, this.widgetsHeight);
		this.onInitialized.init(this, widgetsRectangle);
		if (this.previous != null)
			this.previous.init(this.minecraft, this.width, this.height);
	}
	
	private int messageTop()
	{
		return this.y + 20;
	}
	
	private int messageHeight()
	{
		return this.text.getLineCount() * this.font.lineHeight;
	}
	
	@Override
	public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T pWidget)
	{
		return super.addRenderableWidget(pWidget);
	}
	
	private void close()
	{
		if (!POPUP_QUEUE.isEmpty())
			this.minecraft.setScreen(POPUP_QUEUE.poll());
		else if (this.previous != null)
			this.minecraft.setScreen(this.previous);
		else
			this.minecraft.popGuiLayer();
	}
	
	private Popup open()
	{
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen instanceof Popup)
			POPUP_QUEUE.add(this);
		else
			mc.setScreen(this);
		return this;
	}
	
	@Override
	public void tick()
	{
		if (this.previous != null)
			this.previous.tick();
	}
	
	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
	{
		if (this.previous != null)
			this.previous.render(stack, mouseX, mouseY, partialTicks);
		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
		fillGradient(stack, 0, 0, this.width, this.height, -1072689136, -804253680);
		fill(stack, this.x, this.y, this.x + this.boxWidth, this.y + this.boxHeight, CommonColors.BACKGROUND);
		this.text.renderCentered(stack, this.x + this.boxWidth / 2, this.messageTop(), this.font.lineHeight, CommonColors.WHITE);
		super.render(stack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void onClose()
	{
		this.close();
	}
	
	@FunctionalInterface
	public static interface Initializer
	{
		public void init(Popup popup, ScreenRectangle buttons);	
	}
}
