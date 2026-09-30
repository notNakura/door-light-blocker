package io.github.notnakura.doorlightblocker.client.config;

import dev.isxander.yacl3.gui.image.ImageRenderer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/** Draws a door item, scaled up, as the image of a YACL option description. */
final class DoorItemImage implements ImageRenderer {
	private static final int MAX_SIZE = 64;

	private final ItemStack stack;

	DoorItemImage(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta) {
		int size = Math.min(renderWidth, MAX_SIZE);
		graphics.pose().pushMatrix();
		graphics.pose().translate(x + (renderWidth - size) / 2f, y);
		graphics.pose().scale(size / 16f);
		graphics.renderItem(stack, 0, 0);
		graphics.pose().popMatrix();
		return size;
	}

	@Override
	public void close() {
	}
}
