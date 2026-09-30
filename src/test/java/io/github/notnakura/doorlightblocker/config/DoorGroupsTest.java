package io.github.notnakura.doorlightblocker.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DoorGroupsTest {
	@ParameterizedTest
	@ValueSource(strings = {
		"oak", "spruce", "birch", "acacia", "cherry", "jungle", "dark_oak",
		"pale_oak", "mangrove", "bamboo", "crimson", "warped"
	})
	void vanillaWoodSetTypesAreWooden(String setType) {
		assertEquals(DoorGroups.WOODEN, DoorGroups.forSetType(setType));
	}

	@Test
	void ironAndCopperHaveOwnGroups() {
		assertEquals(DoorGroups.IRON, DoorGroups.forSetType("iron"));
		assertEquals(DoorGroups.COPPER, DoorGroups.forSetType("copper"));
	}

	@Test
	void unknownSetTypesAreOther() {
		assertEquals(DoorGroups.OTHER, DoorGroups.forSetType("mythril"));
		assertEquals(DoorGroups.OTHER, DoorGroups.forSetType("stone"));
	}
}
