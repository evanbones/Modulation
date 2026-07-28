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