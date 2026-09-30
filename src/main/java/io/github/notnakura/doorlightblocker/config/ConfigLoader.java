package io.github.notnakura.doorlightblocker.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Reads, validates and writes the JSON config file. */
public final class ConfigLoader {
	public static final String FILE_NAME = "door_light_blocker.json";

	private static final Logger LOGGER = LoggerFactory.getLogger("door_light_blocker");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final String KEY_DEFAULT = "default_closed_light_block";
	private static final String KEY_GROUPS = "groups";
	private static final String KEY_DOORS = "doors";

	private ConfigLoader() {
	}

	/**
	 * Loads the config. A missing file is created with defaults; a malformed file is reported and
	 * left untouched while defaults are used in memory.
	 */
	public static DoorLightConfig load(Path file) {
		if (Files.notExists(file)) {
			DoorLightConfig defaults = DoorLightConfig.defaults();
			try {
				save(file, defaults);
			} catch (IOException e) {
				LOGGER.error("Could not write default config {}", file, e);
			}
			return defaults;
		}

		try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			JsonElement root = JsonParser.parseReader(reader);
			if (!root.isJsonObject()) {
				LOGGER.error("Config {} must contain a JSON object; using defaults (file left untouched)", file);
				return DoorLightConfig.defaults();
			}
			return parse(root.getAsJsonObject());
		} catch (IOException | RuntimeException e) {
			LOGGER.error("Could not read config {}; using defaults (file left untouched)", file, e);
			return DoorLightConfig.defaults();
		}
	}

	public static void save(Path file, DoorLightConfig config) throws IOException {
		Path parent = file.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		Files.writeString(file, toJson(config) + System.lineSeparator(), StandardCharsets.UTF_8);
	}

	static String toJson(DoorLightConfig config) {
		JsonObject root = new JsonObject();
		root.addProperty(KEY_DEFAULT, config.defaultClosedLightBlock());
		root.add(KEY_GROUPS, toJsonObject(config.groups()));
		root.add(KEY_DOORS, toJsonObject(config.doors()));
		return GSON.toJson(root);
	}

	static DoorLightConfig parse(JsonObject root) {
		int defaultValue = DoorLightConfig.MAX_LIGHT;
		if (root.has(KEY_DEFAULT)) {
			Integer value = readValue(KEY_DEFAULT, root.get(KEY_DEFAULT));
			if (value != null) {
				defaultValue = value;
			}
		}

		// Groups are overrides: only groups present in the file are set, the rest inherit the default.
		Map<String, Integer> groups = new LinkedHashMap<>();
		readMap(root, KEY_GROUPS).forEach((group, element) -> {
			if (!DoorGroups.ALL.contains(group)) {
				LOGGER.warn("Ignoring unknown group '{}' in config (known groups: {})", group, DoorGroups.ALL);
				return;
			}
			Integer value = readValue("groups." + group, element);
			if (value != null) {
				groups.put(group, value);
			}
		});

		Map<String, Integer> doors = new LinkedHashMap<>();
		readMap(root, KEY_DOORS).forEach((id, element) -> {
			Integer value = readValue("doors." + id, element);
			if (value != null) {
				doors.put(id, value);
			}
		});

		return new DoorLightConfig(defaultValue, groups, doors);
	}

	private static Map<String, JsonElement> readMap(JsonObject root, String key) {
		JsonElement element = root.get(key);
		if (element == null) {
			return Map.of();
		}
		if (!element.isJsonObject()) {
			LOGGER.warn("Ignoring '{}' in config: expected a JSON object", key);
			return Map.of();
		}
		return element.getAsJsonObject().asMap();
	}

	/** Returns the value clamped to 0..15, or null (with a warning) if it is not an integer. */
	private static Integer readValue(String path, JsonElement element) {
		if (!(element instanceof JsonPrimitive primitive) || !primitive.isNumber()) {
			LOGGER.warn("Ignoring '{}' in config: expected an integer from 0 to 15", path);
			return null;
		}
		double raw = primitive.getAsDouble();
		if (raw != Math.rint(raw)) {
			LOGGER.warn("Ignoring '{}' in config: {} is not an integer", path, raw);
			return null;
		}
		int clamped = (int) Math.max(DoorLightConfig.MIN_LIGHT, Math.min(DoorLightConfig.MAX_LIGHT, raw));
		if (clamped != raw) {
			LOGGER.warn("Clamped '{}' in config from {} to {}", path, primitive.getAsString(), clamped);
		}
		return clamped;
	}

	private static JsonObject toJsonObject(Map<String, Integer> values) {
		JsonObject object = new JsonObject();
		values.forEach(object::addProperty);
		return object;
	}
}
