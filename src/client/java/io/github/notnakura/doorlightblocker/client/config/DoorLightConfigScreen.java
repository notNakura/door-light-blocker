package io.github.notnakura.doorlightblocker.client.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.notnakura.doorlightblocker.DoorLightBlocker;
import io.github.notnakura.doorlightblocker.config.ConfigLoader;
import io.github.notnakura.doorlightblocker.config.DoorGroups;
import io.github.notnakura.doorlightblocker.config.DoorLightConfig;
import io.github.notnakura.doorlightblocker.config.DoorLightConfigs;
import io.github.notnakura.doorlightblocker.light.DoorLightReloader;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;

/**
 * YACL config screen. Must only be loaded when YACL is present.
 *
 * <p>Group and door options use {@link #INHERIT} as "not set": a door falls back to its group override,
 * then to the general value; a group falls back to the general value.
 *
 * <p>Saving applies the values immediately from the title screen and in singleplayer (see
 * {@link DoorLightReloader}). While connected to a remote server the caches are left alone, so the
 * options carry {@link OptionFlag#GAME_RESTART} and the change takes effect after a restart.
 */
public final class DoorLightConfigScreen {
	private static final int INHERIT = -1;

	private DoorLightConfigScreen() {
	}

	public static Screen create(Screen parent) {
		Minecraft minecraft = Minecraft.getInstance();
		boolean remote = minecraft.level != null && minecraft.getSingleplayerServer() == null;
		DoorLightConfig current = ConfigLoader.load(DoorLightConfigs.file());
		int[] defaultValue = {current.defaultClosedLightBlock()};
		Map<String, Integer> groups = new LinkedHashMap<>(current.groups());
		Map<String, Integer> doors = new TreeMap<>(current.doors());

		List<DoorBlock> doorBlocks = BuiltInRegistries.BLOCK.stream()
			.filter(DoorBlock.class::isInstance)
			.map(DoorBlock.class::cast)
			.sorted((a, b) -> BuiltInRegistries.BLOCK.getKey(a).compareTo(BuiltInRegistries.BLOCK.getKey(b)))
			.toList();

		Component notice = text(remote ? "restart" : "live");

		ConfigCategory general = ConfigCategory.createBuilder()
			.name(text("category.general"))
			.option(slider(
				text("default.name"),
				OptionDescription.of(text("default.description"), notice),
				DoorLightConfig.MAX_LIGHT,
				() -> defaultValue[0],
				value -> defaultValue[0] = value,
				DoorLightConfig.MIN_LIGHT,
				remote))
			.build();

		ConfigCategory groupsCategory = ConfigCategory.createBuilder()
			.name(text("category.groups"))
			.options(DoorGroups.ALL.stream().map(group -> slider(
				text("group." + group),
				OptionDescription.of(text("group." + group + ".description"), notice),
				INHERIT,
				() -> groups.getOrDefault(group, INHERIT),
				value -> setOverride(groups, group, value),
				INHERIT,
				remote)).toList())
			.build();

		List<Option<?>> doorOptions = new ArrayList<>();
		for (DoorBlock door : doorBlocks) {
			String id = BuiltInRegistries.BLOCK.getKey(door).toString();
			OptionDescription description = OptionDescription.createBuilder()
				.text(Component.literal(id), text("door.description"), notice)
				.customImage(new DoorItemImage(new ItemStack(door.asItem())))
				.build();
			doorOptions.add(slider(
				door.getName(),
				description,
				INHERIT,
				() -> doors.getOrDefault(id, INHERIT),
				value -> setOverride(doors, id, value),
				INHERIT,
				remote));
		}
		ConfigCategory doorsCategory = ConfigCategory.createBuilder()
			.name(text("category.doors"))
			.group(OptionGroup.createBuilder()
				.name(text("doors.group"))
				.description(OptionDescription.of(text("doors.group.description"), notice))
				.options(doorOptions)
				.build())
			.build();

		Set<String> knownDoorIds = new HashSet<>();
		doorBlocks.forEach(door -> knownDoorIds.add(BuiltInRegistries.BLOCK.getKey(door).toString()));

		return YetAnotherConfigLib.createBuilder()
			.title(text("title"))
			.category(general)
			.category(groupsCategory)
			.category(doorsCategory)
			.save(() -> save(current, defaultValue[0], groups, doors, knownDoorIds, remote))
			.build()
			.generateScreen(parent);
	}

	private static void setOverride(Map<String, Integer> overrides, String key, int value) {
		if (value == INHERIT) {
			overrides.remove(key);
		} else {
			overrides.put(key, value);
		}
	}

	private static Option<Integer> slider(
		Component name,
		OptionDescription description,
		int defaultValue,
		Supplier<Integer> getter,
		Consumer<Integer> setter,
		int min,
		boolean restartRequired
	) {
		Option.Builder<Integer> builder = Option.<Integer>createBuilder()
			.name(name)
			.description(description)
			.binding(defaultValue, getter, setter)
			.controller(option -> IntegerSliderControllerBuilder.create(option)
				.range(min, DoorLightConfig.MAX_LIGHT)
				.step(1)
				.formatValue(value -> value == INHERIT ? text("inherit") : Component.literal(Integer.toString(value))));
		if (restartRequired) {
			builder.flag(OptionFlag.GAME_RESTART);
		}
		return builder.build();
	}

	private static void save(
		DoorLightConfig previous,
		int defaultValue,
		Map<String, Integer> groups,
		Map<String, Integer> doors,
		Set<String> knownDoorIds,
		boolean remote
	) {
		// Keep overrides for doors that are not registered right now (for example a mod that is not loaded).
		Map<String, Integer> merged = new TreeMap<>(doors);
		previous.doors().forEach((id, value) -> {
			if (!knownDoorIds.contains(id)) {
				merged.put(id, value);
			}
		});
		DoorLightConfig saved = new DoorLightConfig(defaultValue, groups, merged);
		try {
			ConfigLoader.save(DoorLightConfigs.file(), saved);
		} catch (IOException e) {
			DoorLightBlocker.LOGGER.error("Could not save config", e);
			return;
		}
		// A remote server owns the light rules; the file is only read again after a restart.
		if (!remote) {
			DoorLightReloader.apply(saved, Minecraft.getInstance().getSingleplayerServer());
		}
	}

	private static Component text(String key) {
		return Component.translatable("config." + DoorLightBlocker.MOD_ID + "." + key);
	}
}
