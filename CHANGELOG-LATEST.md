### Added

- Added 52 vanilla bugfix tweaks ported from newer versions of Debugify.
    - They cover various entity, sound, statistic, worldgen, and server bugs, and each one has its own toggle.
    - All automatically disable themselves if Debugify is present.
    - Bugs that NeoForge already patches itself are automatically disabled on Neo.

### Changed

- Split the Bugfixes config section into subsections.
- Switched to MixinConstraints for conditional mixins.
- Slot highlight rendering behind items now also applies to EMI's recipe and sidebar slots.
- Improved the MC-259387 fix.

### Fixed

- Fixed Chat Markdown formatting with Chat Patches installed.