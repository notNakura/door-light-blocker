package io.github.notnakura.doorlightblocker.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Light blocked by closed doors, as a number in 0..15 (0 keeps the vanilla value).
 *
 * @param defaultClosedLightBlock value used when neither a door nor its group has an override
 * @param groups                  group overrides by group name (see {@link DoorGroups}); absent means inherit
 * @param doors                   door overrides by block id, for example {@code minecraft:oak_door}; absent means inherit
 */
public record DoorLightConfig(int defaultClosedLightBlock, Map<String, Integer> groups, Map<String, Integer> doors) {
	public static final int MIN_LIGHT = 0;
	public static final int MAX_LIGHT = 15;

	public DoorLightConfig {
		groups = Collections.unmodifiableMap(new LinkedHashMap<>(groups));
		doors = Collections.unmodifiableMap(new TreeMap<>(doors));
	}

	public static DoorLightConfig defaults() {
		return new DoorLightConfig(MAX_LIGHT, Map.of(), Map.of());
	}

	/** Ids in {@link #doors()} that are not in {@code knownDoorIds}. */
	public Set<String> unknownDoors(Set<String> knownDoorIds) {
		Set<String> unknown = new TreeSet<>(doors.keySet());
		unknown.removeAll(knownDoorIds);
		return unknown;
	}
}
