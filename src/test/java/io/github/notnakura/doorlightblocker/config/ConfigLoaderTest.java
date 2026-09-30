package io.github.notnakura.doorlightblocker.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigLoaderTest {
	@TempDir
	Path dir;

	private DoorLightConfig loadJson(String json) throws IOException {
		Path file = dir.resolve(ConfigLoader.FILE_NAME);
		Files.writeString(file, json);
		return ConfigLoader.load(file);
	}

	@Test
	void missingFileIsCreatedWithDefaults() throws IOException {
		Path file = dir.resolve("config").resolve(ConfigLoader.FILE_NAME);

		DoorLightConfig config = ConfigLoader.load(file);

		assertEquals(DoorLightConfig.defaults(), config);
		assertTrue(Files.exists(file));
		assertEquals(DoorLightConfig.defaults(), ConfigLoader.load(file));
		assertTrue(Files.readString(file).contains("\n"), "file should be pretty-printed");
	}

	@Test
	void malformedFileKeepsDefaultsAndIsNotOverwritten() throws IOException {
		String broken = "{ \"default_closed_light_block\": ";
		Path file = dir.resolve(ConfigLoader.FILE_NAME);
		Files.writeString(file, broken);

		assertEquals(DoorLightConfig.defaults(), ConfigLoader.load(file));
		assertEquals(broken, Files.readString(file));
	}

	@Test
	void nonObjectRootKeepsDefaults() throws IOException {
		assertEquals(DoorLightConfig.defaults(), loadJson("[1, 2]"));
	}

	@Test
	void outOfRangeValuesAreClamped() throws IOException {
		DoorLightConfig config = loadJson("""
			{ "default_closed_light_block": 99,
			  "groups": { "wooden": -4, "iron": 16 },
			  "doors": { "minecraft:oak_door": 200, "minecraft:birch_door": -1 } }
			""");

		assertEquals(15, config.defaultClosedLightBlock());
		assertEquals(0, config.groups().get("wooden"));
		assertEquals(15, config.groups().get("iron"));
		assertEquals(15, config.doors().get("minecraft:oak_door"));
		assertEquals(0, config.doors().get("minecraft:birch_door"));
	}

	@Test
	void invalidValuesAreIgnored() throws IOException {
		DoorLightConfig config = loadJson("""
			{ "default_closed_light_block": "high",
			  "groups": { "wooden": 7.5, "copper": 4, "bogus": 3 },
			  "doors": { "minecraft:oak_door": null, "minecraft:birch_door": 2 } }
			""");

		assertEquals(15, config.defaultClosedLightBlock());
		assertEquals(Map.of("copper", 4), config.groups());
		assertEquals(Map.of("minecraft:birch_door", 2), config.doors());
	}

	@Test
	void missingGroupsStayUnset() throws IOException {
		DoorLightConfig config = loadJson("{ \"default_closed_light_block\": 6, \"groups\": { \"iron\": 2 } }");

		assertEquals(6, config.defaultClosedLightBlock());
		assertEquals(Map.of("iron", 2), config.groups());
		assertTrue(config.doors().isEmpty());
	}

	@Test
	void defaultFileHasEmptyOverrides() throws IOException {
		Path file = dir.resolve(ConfigLoader.FILE_NAME);

		ConfigLoader.load(file);

		assertTrue(DoorLightConfig.defaults().groups().isEmpty());
		assertTrue(DoorLightConfig.defaults().doors().isEmpty());
		JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
		assertEquals(15, root.get("default_closed_light_block").getAsInt());
		assertTrue(root.getAsJsonObject("groups").isEmpty());
		assertTrue(root.getAsJsonObject("doors").isEmpty());
	}

	@Test
	void groupWithoutOverrideIsNotWritten() throws IOException {
		DoorLightConfig config = new DoorLightConfig(4, Map.of("iron", 1), Map.of());
		Path file = dir.resolve(ConfigLoader.FILE_NAME);

		ConfigLoader.save(file, config);

		JsonObject groups = JsonParser.parseString(Files.readString(file)).getAsJsonObject().getAsJsonObject("groups");
		assertEquals(Set.of("iron"), groups.keySet());
	}

	@Test
	void saveAndLoadRoundTrip() throws IOException {
		DoorLightConfig config = new DoorLightConfig(
			4,
			Map.of("wooden", 1, "copper", 2, "iron", 3, "other", 0),
			Map.of("minecraft:oak_door", 9)
		);
		Path file = dir.resolve(ConfigLoader.FILE_NAME);

		ConfigLoader.save(file, config);

		assertEquals(config, ConfigLoader.load(file));
	}

	@Test
	void unknownDoorsAreReported() {
		DoorLightConfig config = new DoorLightConfig(15, Map.of(), Map.of("a:x", 1, "minecraft:oak_door", 2));

		assertEquals(Set.of("a:x"), config.unknownDoors(Set.of("minecraft:oak_door")));
	}
}
