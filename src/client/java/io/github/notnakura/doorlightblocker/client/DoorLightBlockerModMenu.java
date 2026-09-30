package io.github.notnakura.doorlightblocker.client;

import io.github.notnakura.doorlightblocker.client.config.DoorLightConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Mod Menu entrypoint. YACL is optional: {@link DoorLightConfigScreen} is the only class that
 * touches it and is referenced solely behind the {@code isModLoaded} check.
 */
public class DoorLightBlockerModMenu implements ModMenuApi {
	private static final String YACL_MOD_ID = "yet_another_config_lib_v3";

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (!FabricLoader.getInstance().isModLoaded(YACL_MOD_ID)) {
			return ModMenuApi.super.getModConfigScreenFactory();
		}
		return DoorLightConfigScreen::create;
	}
}
