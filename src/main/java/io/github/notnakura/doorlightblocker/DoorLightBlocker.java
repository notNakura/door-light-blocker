package io.github.notnakura.doorlightblocker;

import java.util.Set;
import java.util.stream.Collectors;

import io.github.notnakura.doorlightblocker.config.DoorLightConfigs;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.DoorBlock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoorLightBlocker implements ModInitializer {
	public static final String MOD_ID = "door_light_blocker";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Set<String> doorIds = BuiltInRegistries.BLOCK.entrySet().stream()
			.filter(entry -> entry.getValue() instanceof DoorBlock)
			.map(entry -> entry.getKey().identifier().toString())
			.collect(Collectors.toSet());
		for (String unknown : DoorLightConfigs.active().unknownDoors(doorIds)) {
			LOGGER.warn("Config entry 'doors.{}' does not match any registered door", unknown);
		}
		LOGGER.info("Door Light Blocker loaded");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
