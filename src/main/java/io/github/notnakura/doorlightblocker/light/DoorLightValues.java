package io.github.notnakura.doorlightblocker.light;

import io.github.notnakura.doorlightblocker.config.DoorGroups;
import io.github.notnakura.doorlightblocker.config.DoorLightConfigs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.DoorBlock;

/**
 * Resolves configured door values against the block registry. Kept free of event registration so it is safe to use while {@code Blocks} is still being initialized.
 */
public final class DoorLightValues {
	private DoorLightValues() {
	}

	/** Light blocked by {@code door} when closed, from the active config (0 means vanilla). */
	public static int closedLightBlock(DoorBlock door) {
		return DoorLightConfigs.resolver().closedLightBlock(idOf(door), groupOf(door));
	}

	private static String idOf(DoorBlock door) {
		return BuiltInRegistries.BLOCK.getKey(door).toString();
	}

	private static String groupOf(DoorBlock door) {
		return DoorGroups.forSetType(door.type().name());
	}
}
