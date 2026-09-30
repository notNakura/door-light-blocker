# Store description (Modrinth / CurseForge)

Copy the sections below into the project pages.

## Summary (max 100 characters)

```
Closed doors block light like solid blocks. Install on client and server.
```

(73 characters.)

## Long description (Markdown)

```markdown
# Door Light Blocker

**Closed doors block light. Open doors let it through.**

![Opening and closing a door](https://raw.githubusercontent.com/notNakura/door-light-blocker/main/docs/images/door-toggle.gif)

In vanilla Minecraft, doors are transparent to the lighting engine: sunlight and torch light leak straight through a closed door. Door Light Blocker fixes that, so closing a door really darkens the room behind it.

## Features

- Closed doors block light like a solid block.
- Open doors let light through as usual.
- Works with every vanilla door, including copper doors.
- Works out of the box; optionally configurable (see below).

| Door open | Door closed |
|---|---|
| ![Door open](https://raw.githubusercontent.com/notNakura/door-light-blocker/main/docs/images/door-open.png) | ![Door closed](https://raw.githubusercontent.com/notNakura/door-light-blocker/main/docs/images/door-closed.png) |

![Light level shown in the F3 debug screen](https://raw.githubusercontent.com/notNakura/door-light-blocker/main/docs/images/debug-light-level.png)

## Configuration

Edit `config/door_light_blocker.json` to choose how much light a closed door blocks (0 to 15): a default value that applies to every door, plus optional overrides per group (wooden, copper, iron, other) or per door. Per-door overrides beat group overrides, which beat the default; anything you leave unset inherits. With Mod Menu and YACL installed you can do the same from an in-game screen. Open it from the title screen: **Mods** → **Door Light Blocker** → config button.

![Config screen](https://raw.githubusercontent.com/notNakura/door-light-blocker/main/docs/images/config-screen.png)

Saving from the config screen applies without a restart in singleplayer; on a server, restart it. Light around a door updates when it is opened or closed, so toggle doors after changing the config to refresh them. The client and server must use the same config.

## Important: install on client AND server

The mod changes how light is calculated, so it must be installed on both the server and every player's client. Without it on both sides you will see lighting glitches.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.5 or newer
- Fabric API
- Optional: Mod Menu and YACL (YetAnotherConfigLib) for the config screen

## Compatibility and performance

- Trapdoors and fence gates are not affected.
- Mods that replace the lighting engine or add light overhauls have not been tested.
- Light values are computed once at startup. There is no per-tick cost; opening or closing a door triggers a single local light update.

## Source and issues

Source code (MIT license) and issue tracker: https://github.com/notNakura/door-light-blocker
```

## Images

The long description loads images from `docs/images/` on the `main` branch, so they must be committed and pushed before pasting the description. Expected files:

| File | Content |
|---|---|
| `door-toggle.gif` | Short loop opening and closing the door (keep it under ~5 MB) |
| `door-open.png` | Room with the door open, light coming in |
| `door-closed.png` | Same framing, door closed, room dark |
| `debug-light-level.png` | F3 screen showing the light level next to the closed door |
| `config-screen.png` | Config screen (Mods → Door Light Blocker), ideally the Doors category with a door selected so its icon shows |

Also upload the same images to each platform's gallery.

## Environment

- Client: **required**
- Server: **required**
- Loader: Fabric
- Game version: 1.21.11
- Dependency: Fabric API (required)
- Optional dependencies: Mod Menu, YetAnotherConfigLib (YACL)
- License: MIT

## Suggested categories and tags

Modrinth categories (verified against `https://api.modrinth.com/v2/tag/category`, project type `mod`, on 2026-09-30):
`decoration`, `game-mechanics` (alternative: `utility`). Pick at most three; two fit best.

CurseForge categories (unverified; names may differ in the console): "Miscellaneous" or "Redstone"/"Cosmetic" are common choices. Check the options offered when creating the project.
