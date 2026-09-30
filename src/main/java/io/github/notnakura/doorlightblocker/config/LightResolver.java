package io.github.notnakura.doorlightblocker.config;

/** Resolves the light blocked by a closed door: per-door value, then group value, then the default. */
public final class LightResolver {
	private final DoorLightConfig config;

	public LightResolver(DoorLightConfig config) {
		this.config = config;
	}

	public int closedLightBlock(String doorId, String group) {
		Integer perDoor = config.doors().get(doorId);
		if (perDoor != null) {
			return perDoor;
		}
		Integer perGroup = config.groups().get(group);
		return perGroup != null ? perGroup : config.defaultClosedLightBlock();
	}
}
