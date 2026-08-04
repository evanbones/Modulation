### Added

- Added a Block Grid & Surface Offsets module:
    - Sub-block grid snapping (1/16th grid) and surface offset alignment for decorations, redstone components, and
      hanging entities.
    - Support for placing blocks (lanterns, torches, flower pots, redstone wire, repeaters, comparators, etc.) on top of
      slabs and stairs.
    - Item tags (`modulation:mounts_on_facing`, `modulation:sits_on_slabs`) for controlling surface offset behaviors.

### Changed

- The Passable Foliage module now uses a tag for passable leaves (`modulation:passable_leaves`).

### Fixed

- Fixed third-person camera acting strange when passing through foliage.