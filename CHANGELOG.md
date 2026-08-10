# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.2.3] - 2026-08-09

### Added

- Added an option to fix horizon banding and skies rendering in caves (most noticeable with mods that modify fog, like
  No Man's Land).

## [2.2.2] - 2026-08-04

### Removed

- Removed block grid module.

## [2.2.1] - 2026-08-04

### Added

- Added a config option to toggle the custom Creative delete button.

### Fixed

- Fixed Creative delete button incompatibility with Not Enough Recipe Book (NERB).

## [2.2.0] - 2026-08-04

### Added

- Added a Block Grid & Surface Offsets module.
- Added a Clear Inventory / Destroy Item button to the survival inventory screen when Creative inventory is disabled.

### Changed

- The Passable Foliage module now uses a tag for passable leaves (`modulation:passable_leaves`).

### Fixed

- Fixed third-person camera acting strange when passing through foliage.

## [2.1.0] - 2026-07-29

### Added

- Added a setting (with optional Vanilla Backport integration) for passable foliage.

### Removed

- Removed Inventorio compat (wasn't working anyway).

## [2.0.0] - 2026-07-28

### Added

- Added a tweak to disable the Creative inventory screen, with compatibility
  for [Inventorio](https://modrinth.com/mod/inventorio)'s inventory screen, ported from Raspberry Core 1.20 (MIT).
- Added a Vanilla Walls module (ported from BetterWalls, AGPL-3.0-only) with toggleable connection tweaks: walls connect
  to fences, fences connect to walls/bars, and bars/panes connect to fences.

### Changed

- Split the Vanilla module into five submodules (Bugfixes, Anvil, Visual, Gameplay, GUI) so each config category is
  easier to navigate.
    - Note: this resets any previously customized Vanilla tweaks back to their defaults, since they're now stored under
      new config keys.
- Backend rewrites.

## [1.9.0] - 2026-07-15

### Added

- Added a vanilla tweak to configure chat duration.

## [1.8.0] - 2026-07-13

### Added

- Added a vanilla tweak to control+drag to move items to the inventory crafting grid.

## [1.7.2] - 2026-07-12

### Fixed

- Fixed concurrency issue with Fancy Tab Sections.

## [1.7.1] - 2026-07-08

### Added

- Added blacklist tags for tooltip features.

## [1.7.0] - 2026-07-03

### Added

- Added an option for Campfires to place unlit.
- Added an option for flammable cobwebs.

### Changed

- Switched from Cloth Config to YACL.
- Backend rewrites.

## [1.6.2] - 2026-07-02

### Fixed

- Improved tooltip matching with mods that use dynamic resources.

## [1.6.1] - 2026-07-02

### Added

- Improved mod compatibility for tooltips.
- Added tags for waxed, trapped, and infested items.

## [1.6.0] - 2026-07-02

### Added

- Added config options for waxed, trapped, and infested tooltips.

## [1.5.0] - 2026-05-14

### Added

- Added config option for nicer map colors.

## [1.4.1] - 2026-04-16

### Fixed

- Fixed crash with Inhabitants.

## [1.4.0] - 2026-04-16

### Fixed

- Fixed chain staff recipe on 1.20.
- Added chain and zipline staffs to the creative menu when enabled.

## [1.3.1] - 2026-04-13

### Fixed

- Fixed mixin crash again.

## [1.3.0] - 2026-04-13

### Added

- Added recipes for the Chain Staff.
- Added config option to require Chains in the player's inventory when using the chain staff (default: true).

### Changed

- The Reconnectible Chains module is now disabled by default (does not affect existing configs).

### Fixed

- Fixed Reconnectible Chains post summoning animation not playing properly on 1.21.1.

## [1.2.1] - 2026-04-13

### Fixed

- Fixed mixin crash.

## [1.2.0] - 2026-04-13

### Added

- Added a fix for a vanilla performance issue that should speed up Tectonic world generation by ~30%.

## [1.1.4] - 2026-04-12

### Fixed

- Fixed server kicking players on 1.20.1.

## [1.1.3] - 2026-04-06

### Fixed

- Fixed crash with Zenith.

## [1.1.2] - 2026-04-02

### Fixed

- Fixed crash with Apotheosis.

## [1.1.1] - 2026-03-31

### Added

- Added a tweak to fix a vanilla bug where datapack filters in pack.mcmeta would disable all paths in that namespace.

## [1.1.0] - 2026-03-28

### Added

- Added a tweak to fix a vanilla bug where experience levels disappear when changing dimensions without portals (e.g.
  when teleporting between worlds).
- Added four new anvil-related tweaks:
    - Remove Anvil Limit: Removes the 'Too Expensive!' limit for anvils
    - No Anvil Enchant Cost: Removes the experience cost for combining enchantments.
    - No Anvil Repair Cost: Removes the experience cost for anvil repairs (using materials or combining damaged items).
    - No Anvil Rename Cost: Removes the experience cost for all anvil renames.

### Removed

- Removed the combat music module as it's now used in my other mod Mini Music Tweaks.

## [1.0.2] - 2026-03-26

### Fixed

- Fixed certain tags not working without Reconnectible Chains.

## [1.0.1] - 2026-03-13

### Fixed

- Fixed crash on dedicated servers.

## [1.0.0] - 2026-03-10

- Initial release.