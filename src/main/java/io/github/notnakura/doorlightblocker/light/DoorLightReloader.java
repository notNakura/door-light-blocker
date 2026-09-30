package io.github.notnakura.doorlightblocker.light;

import io.github.notnakura.doorlightblocker.config.DoorLightConfig;
import io.github.notnakura.doorlightblocker.config.DoorLightConfigs;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;

/**
 * Applies a config change without restarting. Only valid when this process owns the light rules: the
 * title screen (no world) or a singleplayer / integrated server. Never call it while connected to a
 * remote server, which keeps its own values until it restarts. Nothing is relit here: light around a
 * door refreshes the next time that door is opened or closed.
 */
public final class DoorLightReloader {
	private DoorLightReloader() {
	}

	/**
	 * Swaps in {@code config} and rebuilds the cached block state values of every door. With an
	 * integrated server the work is queued to its thread, because block state caches are read there.
	 */
	public static void apply(DoorLightConfig config, @Nullable MinecraftServer integratedServer) {
		if (integratedServer == null) {
			applyNow(config);
		} else {
			integratedServer.execute(() -> applyNow(config));
		}
	}

	private static void applyNow(DoorLightConfig config) {
		DoorLightConfigs.reload(config);
		// Vanilla fills the per-state light cache once from the Blocks static init.
		for (Block block : BuiltInRegistries.BLOCK) {
			if (block instanceof DoorBlock) {
				block.getStateDefinition().getPossibleStates().forEach(state -> state.initCache());
			}
		}
	}
}
