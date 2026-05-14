# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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