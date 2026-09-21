### Added

- Added a Brainier Bees module, ported from Brainier Bees by dopadream.
    - Replaces the vanilla goal-based bee AI with a brain-based one, with dedicated tasks for wandering, finding
      flowers, pollinating, growing crops, locating a hive, and flying home.
    - Bees no longer pile up against ceilings, and they pathfind around ladders instead of hovering into them.
    - Hives that are gone, full, unreachable or have a campfire under them are temporarily blacklisted, so a bee looks
      elsewhere instead of retrying the same one forever.
    - Flowers under water are skipped, and a flower the bee can't path to is abandoned instead of blocking
      pollination.
    - Configurable wander radius, flower search range, and ladder avoidance.
    - The module is disabled automatically if Brainier Bees is installed.
- Added a Panorama Screenshot tweak, ported from Panorama Screenshot by Fridtjof-DE.
    - Adds a keybind (F4 by default) that captures the six `panorama_0.png` - `panorama_5.png` images used for title
      screen panoramas, saved into the screenshots folder at a configurable resolution.
    - The tweak and its keybind are disabled automatically if Panorama Screenshot is installed.
- Added a Fix Ghast Direction bugfix, ported from Ghast Direction by Roundaround.
    - Idle ghasts keep whichever way they drifted in instead of snapping back to face due south whenever they stop
      moving.
    - Disabled automatically if Ghast Direction is installed.
- Added a config option to disable Sunbathing Godrays patches.

### Changed

- Sunbathing Godrays patches are now resource-pack driven.