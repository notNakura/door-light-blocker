package io.github.notnakura.doorlightblocker.config;

import java.util.List;
import java.util.Set;

/** Door groups derived from the vanilla block set type name. */
public final class DoorGroups {
	public static final String WOODEN = "wooden";
	public static final String COPPER = "copper";
	public static final String IRON = "iron";
	public static final String OTHER = "other";

	/** Every known group, in display order. */
	public static final List<String> ALL = List.of(WOODEN, COPPER, IRON, OTHER);

	private static final Set<String> WOODEN_SET_TYPES = Set.of(
		"oak", "spruce", "birch", "acacia", "cherry", "jungle", "dark_oak",
		"pale_oak", "mangrove", "bamboo", "crimson", "warped"
	);

	private DoorGroups() {
	}

	/** Maps a {@code BlockSetType} name (for example {@code oak} or {@code copper}) to a group. */
	public static String forSetType(String setTypeName) {
		if (IRON.equals(setTypeName)) {
			return IRON;
		}
		if (COPPER.equals(setTypeName)) {
			return COPPER;
		}
		return WOODEN_SET_TYPES.contains(setTypeName) ? WOODEN : OTHER;
	}
}
