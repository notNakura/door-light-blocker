package io.github.notnakura.doorlightblocker.config;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Active config snapshot. Light values are cached while {@code Blocks} is initialized, which can
 * happen before {@code onInitialize}, so the file is read on first access instead. {@link #reload}
 * swaps in a new snapshot; the snapshot is immutable and published through a volatile field.
 */
public final class DoorLightConfigs {
	private static volatile Snapshot snapshot;

	private DoorLightConfigs() {
	}

	public static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve(ConfigLoader.FILE_NAME);
	}

	public static DoorLightConfig active() {
		return current().config();
	}

	public static LightResolver resolver() {
		return current().resolver();
	}

	/** Replaces the active config. Cached block state values are not touched; see DoorLightReloader. */
	public static void reload(DoorLightConfig config) {
		snapshot = new Snapshot(config, new LightResolver(config));
	}

	private static Snapshot current() {
		Snapshot current = snapshot;
		if (current == null) {
			synchronized (DoorLightConfigs.class) {
				current = snapshot;
				if (current == null) {
					DoorLightConfig loaded = ConfigLoader.load(file());
					current = new Snapshot(loaded, new LightResolver(loaded));
					snapshot = current;
				}
			}
		}
		return current;
	}

	private record Snapshot(DoorLightConfig config, LightResolver resolver) {
	}
}
