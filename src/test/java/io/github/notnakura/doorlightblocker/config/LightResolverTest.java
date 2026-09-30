package io.github.notnakura.doorlightblocker.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

class LightResolverTest {
	private static final Map<String, Integer> GROUPS = Map.of("wooden", 10, "iron", 5);

	@Test
	void perDoorBeatsGroupAndDefault() {
		var resolver = new LightResolver(new DoorLightConfig(15, GROUPS, Map.of("minecraft:oak_door", 3)));
		assertEquals(3, resolver.closedLightBlock("minecraft:oak_door", "wooden"));
	}

	@Test
	void groupBeatsDefault() {
		var resolver = new LightResolver(new DoorLightConfig(15, GROUPS, Map.of()));
		assertEquals(10, resolver.closedLightBlock("minecraft:oak_door", "wooden"));
		assertEquals(5, resolver.closedLightBlock("minecraft:iron_door", "iron"));
	}

	@Test
	void unconfiguredGroupFallsBackToDefault() {
		var resolver = new LightResolver(new DoorLightConfig(7, GROUPS, Map.of()));
		assertEquals(7, resolver.closedLightBlock("other:door", "other"));
	}

	@Test
	void zeroOverrideIsHonored() {
		var resolver = new LightResolver(new DoorLightConfig(15, GROUPS, Map.of("minecraft:oak_door", 0)));
		assertEquals(0, resolver.closedLightBlock("minecraft:oak_door", "wooden"));
	}

	@Test
	void changingDefaultAffectsAllDoorsWithoutOverrides() {
		var resolver = new LightResolver(new DoorLightConfig(0, Map.of(), Map.of()));
		for (String group : DoorGroups.ALL) {
			assertEquals(0, resolver.closedLightBlock("any:door", group));
		}
	}

	@Test
	void defaultsBlockAllLight() {
		var resolver = new LightResolver(DoorLightConfig.defaults());
		for (String group : DoorGroups.ALL) {
			assertEquals(15, resolver.closedLightBlock("any:door", group));
		}
	}
}
