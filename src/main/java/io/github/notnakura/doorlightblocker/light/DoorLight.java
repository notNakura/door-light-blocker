package io.github.notnakura.doorlightblocker.light;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LightEngine;

/** Light updates for doors. Runs on the server thread and only queues a light check. */
public final class DoorLight {
	private DoorLight() {
	}

	/**
	 * Called after a door block state changed on the server. When the open and closed states have equal
	 * light properties (value 0), vanilla skips the light check, so queue it here; this also repairs
	 * light left stale by a config change. When the properties differ, vanilla already queued it.
	 */
	public static void onDoorStateChanged(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
		if (level instanceof ServerLevel serverLevel
			&& oldState.is(newState.getBlock())
			&& oldState.getValue(DoorBlock.OPEN) != newState.getValue(DoorBlock.OPEN)
			&& !LightEngine.hasDifferentLightProperties(oldState, newState)) {
			// Mirrors LevelChunk.setBlockState: refresh the sky light column, then queue the light check.
			LevelChunk chunk = serverLevel.getChunkAt(pos);
			chunk.getSkyLightSources().update(chunk, pos.getX() & 15, pos.getY(), pos.getZ() & 15);
			serverLevel.getChunkSource().getLightEngine().checkBlock(pos);
		}
	}
}
