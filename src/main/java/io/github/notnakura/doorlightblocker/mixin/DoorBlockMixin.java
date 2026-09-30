package io.github.notnakura.doorlightblocker.mixin;

import io.github.notnakura.doorlightblocker.light.DoorLight;
import io.github.notnakura.doorlightblocker.light.DoorLightValues;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Makes closed doors block light like a solid block; open doors keep vanilla behavior.
 * The blocked amount comes from the JSON config (per door, per group, or the default); a
 * configured value of 0 keeps vanilla behavior for closed doors as well.
 *
 * <p>{@code getLightBlock} and {@code propagatesSkylightDown} are evaluated once per
 * {@link BlockState} by {@code BlockStateBase.initCache()}, so this code never runs on a hot path.
 * Toggling {@link DoorBlock#OPEN} changes the cached light values, which makes
 * {@code LevelChunk.setBlockState} schedule a light update via
 * {@code LightEngine.hasDifferentLightProperties}. When the values are equal (configured 0) that
 * check is skipped, so {@link #onPlace} queues the light check itself.
 *
 * <p>{@code DoorBlock} inherits these methods, so they are added as plain overrides instead of
 * injections. {@code onPlace} runs on the server for every state change of a placed block, including
 * both door halves and redstone toggles. Doors use {@code noOcclusion()}, so shape-based light occlusion does not apply.
 */
@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin extends Block {
	private DoorBlockMixin(Properties properties) {
		super(properties);
	}

	@Override
	protected int getLightBlock(BlockState state) {
		if (state.getValue(DoorBlock.OPEN)) {
			return super.getLightBlock(state);
		}
		int configured = closedLightBlock();
		return configured > 0 ? configured : super.getLightBlock(state);
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		if (!state.getValue(DoorBlock.OPEN) && closedLightBlock() > 0) {
			return false;
		}
		return super.propagatesSkylightDown(state);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		DoorLight.onDoorStateChanged(level, pos, oldState, state);
	}

	private int closedLightBlock() {
		return DoorLightValues.closedLightBlock((DoorBlock) (Object) this);
	}
}
