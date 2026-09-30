package io.github.notnakura.doorlightblocker.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Map;

import org.junit.jupiter.api.Test;

class DoorLightConfigsTest {
	@Test
	void reloadReplacesTheActiveSnapshot() {
		var first = new DoorLightConfig(15, Map.of(), Map.of());
		DoorLightConfigs.reload(first);
		assertSame(first, DoorLightConfigs.active());
		assertEquals(15, DoorLightConfigs.resolver().closedLightBlock("minecraft:oak_door", DoorGroups.WOODEN));

		var second = new DoorLightConfig(2, Map.of(), Map.of("minecraft:oak_door", 9));
		DoorLightConfigs.reload(second);
		assertSame(second, DoorLightConfigs.active());
		assertEquals(9, DoorLightConfigs.resolver().closedLightBlock("minecraft:oak_door", DoorGroups.WOODEN));
		assertEquals(2, DoorLightConfigs.resolver().closedLightBlock("minecraft:iron_door", DoorGroups.IRON));
	}
}
