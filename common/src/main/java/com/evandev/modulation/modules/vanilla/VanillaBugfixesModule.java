package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

import java.util.function.Predicate;

@AutoService(IModule.class)
public class VanillaBugfixesModule extends AbstractModule {

    private static final String ENTITIES = "entities";
    private static final String PLAYER = "player";
    private static final String BLOCKS_ITEMS = "blocks_items";
    private static final String WORLD = "world";
    private static final String RENDERING = "rendering";
    private static final String SOUND = "sound";
    private static final String INTERFACE = "interface";
    private static final String COMMANDS = "commands";
    private static final String SERVER = "server";

    private final BooleanTweak attackSleepingVillagers = tweak(new BooleanTweak("attack_sleeping_villagers", true), ENTITIES);
    private final BooleanTweak fixMobsCrossingRails = tweak(new BooleanTweak("fix_mobs_crossing_rails", false), ENTITIES);
    private final BooleanTweak fixDyingPufferfishSting = tweak(new BooleanTweak("fix_dying_pufferfish_sting", true), ENTITIES, "debugify");
    private final BooleanTweak fixLightningItemDrops = tweak(new BooleanTweak("fix_lightning_item_drops", true), ENTITIES, "debugify");
    private final BooleanTweak fixExperienceOrbLava = tweak(new BooleanTweak("fix_experience_orb_lava", true), ENTITIES);
    private final BooleanTweak fixEndermanTeleportSpam = tweak(new BooleanTweak("fix_enderman_teleport_spam", true), ENTITIES);
    private final BooleanTweak fixWildWolfBreeding = tweak(new BooleanTweak("fix_wild_wolf_breeding", true), ENTITIES, "debugify");
    private final BooleanTweak fixStrafingMobAim = tweak(new BooleanTweak("fix_strafing_mob_aim", true), ENTITIES, "debugify");
    private final BooleanTweak fixWitchHutCats = tweak(new BooleanTweak("fix_witch_hut_cats", true), ENTITIES, "neoforge");
    private final BooleanTweak fixFoxMobLootGamerule = tweak(new BooleanTweak("fix_fox_mob_loot_gamerule", true), ENTITIES);
    private final BooleanTweak fixCreeperDefusing = tweak(new BooleanTweak("fix_creeper_defusing", true), ENTITIES, "debugify");
    private final BooleanTweak fixGroupAiTargetDeath = tweak(new BooleanTweak("fix_group_ai_target_death", true), ENTITIES, "debugify");
    private final BooleanTweak fixCrystalsHealingDyingDragon = tweak(new BooleanTweak("fix_crystals_healing_dying_dragon", true), ENTITIES);
    private final BooleanTweak fixCuredVillagerJockey = tweak(new BooleanTweak("fix_cured_villager_jockey", true), ENTITIES, "debugify");
    private final BooleanTweak fixPeacefulStriderSaddles = tweak(new BooleanTweak("fix_peaceful_strider_saddles", true), ENTITIES, "debugify");
    private final BooleanTweak fixDragonLandingInVoid = tweak(new BooleanTweak("fix_dragon_landing_in_void", true), ENTITIES, "debugify");
    private final BooleanTweak fixCaveSpiderSpinning = tweak(new BooleanTweak("fix_cave_spider_spinning", true), ENTITIES, "neoforge");

    private final BooleanTweak fixExperienceLoss = tweak(new BooleanTweak("fix_experience_loss", true), PLAYER);
    private final BooleanTweak fixSpectatorFireworkBoost = tweak(new BooleanTweak("fix_spectator_firework_boost", true), PLAYER, "debugify");
    private final BooleanTweak fixSpectatorItemUse = tweak(new BooleanTweak("fix_spectator_item_use", true), PLAYER, "debugify");
    private final BooleanTweak fixSpectatorFreezing = tweak(new BooleanTweak("fix_spectator_freezing", true), PLAYER, "debugify");
    private final BooleanTweak fixFishingRodKillCredit = tweak(new BooleanTweak("fix_fishing_rod_kill_credit", true), PLAYER, "debugify");
    private final BooleanTweak fixDeathScreenItemUse = tweak(new BooleanTweak("fix_death_screen_item_use", true), PLAYER);
    private final BooleanTweak fixRiptideDepthStrider = tweak(new BooleanTweak("fix_riptide_depth_strider", true), PLAYER);

    private final BooleanTweak fixMirroredDoubleChests = tweak(new BooleanTweak("fix_mirrored_double_chests", true), BLOCKS_ITEMS, "neoforge");
    private final BooleanTweak fixCreeperIgniterDurability = tweak(new BooleanTweak("fix_creeper_igniter_durability", true), BLOCKS_ITEMS);
    private final BooleanTweak fixPistonMovedCactus = tweak(new BooleanTweak("fix_piston_moved_cactus", true), BLOCKS_ITEMS, "debugify");
    private final BooleanTweak fixCampfireSmokePosition = tweak(new BooleanTweak("fix_campfire_smoke_position", true), BLOCKS_ITEMS, "neoforge");
    private final BooleanTweak fixPathUnderBlocks = tweak(new BooleanTweak("fix_path_under_blocks", true), BLOCKS_ITEMS, "pathunderfencegates");

    private final BooleanTweak fixUnsavedChunks = tweak(new BooleanTweak("fix_unsaved_chunks", true), WORLD, "chunksavingfix", "debugify", "moonrise");
    private final BooleanTweak fixPistonReloadUpdates = tweak(new BooleanTweak("fix_piston_reload_updates", true), WORLD, "debugify");
    private final BooleanTweak fixCommandMinecartCooldown = tweak(new BooleanTweak("fix_command_minecart_cooldown", true), WORLD, "debugify");
    private final BooleanTweak fixEndIslandRings = tweak(new BooleanTweak("fix_end_island_rings", true), WORLD);
    private final BooleanTweak fixStructureSavePath = tweak(new BooleanTweak("fix_structure_save_path", true), WORLD, "neoforge");
    private final BooleanTweak fixStructurePaletteThreading = tweak(new BooleanTweak("fix_structure_palette_threading", true), WORLD, "neoforge");
    private final BooleanTweak fixTwoByTwoSaplings = tweak(new BooleanTweak("fix_two_by_two_saplings", true), WORLD, "debugify", "flwr-8187");

    private final BooleanTweak fixHorizonLine = tweak(new BooleanTweak("fix_horizon_line", true), RENDERING);
    private final BooleanTweak fixCaveSky = tweak(new BooleanTweak("fix_cave_sky", true), RENDERING);
    private final BooleanTweak betterPauseFreezing = tweak(new BooleanTweak("better_pause_freezing", true), RENDERING);
    private final BooleanTweak coloredCrackParticles = tweak(new BooleanTweak("colored_crack_particles", true), RENDERING);
    private final BooleanTweak ghastCharging = tweak(new BooleanTweak("ghast_charging", true), RENDERING);
    private final BooleanTweak fixZombieDoorParticles = tweak(new BooleanTweak("fix_zombie_door_particles", true), RENDERING);
    private final BooleanTweak fixArmorStandBreakParticles = tweak(new BooleanTweak("fix_armor_stand_break_particles", true), RENDERING, "debugify");
    private final BooleanTweak fixShulkerBulletBubbles = tweak(new BooleanTweak("fix_shulker_bullet_bubbles", true), RENDERING);
    private final BooleanTweak fixShulkerBulletImpact = tweak(new BooleanTweak("fix_shulker_bullet_impact", true), RENDERING);
    private final BooleanTweak fixSlowFallingParticles = tweak(new BooleanTweak("fix_slow_falling_particles", true), RENDERING, "debugify");

    private final BooleanTweak fixItemFrameLoadSound = tweak(new BooleanTweak("fix_item_frame_load_sound", true), SOUND);
    private final BooleanTweak fixFishingBobberSound = tweak(new BooleanTweak("fix_fishing_bobber_sound", true), SOUND);
    private final BooleanTweak fixRaidHornSound = tweak(new BooleanTweak("fix_raid_horn_sound", true), SOUND);
    private final BooleanTweak fixRawCopperSounds = tweak(new BooleanTweak("fix_raw_copper_sounds", true), SOUND, "debugify");
    private final BooleanTweak fixEatingSound = tweak(new BooleanTweak("fix_eating_sound", true), SOUND);
    private final BooleanTweak fixShieldSounds = tweak(new BooleanTweak("fix_shield_sounds", true), SOUND);

    private final BooleanTweak fixFocusBug = tweak(new BooleanTweak("fix_focus_bug", true), INTERFACE);
    private final BooleanTweak fixBadOmenTooltip = tweak(new BooleanTweak("fix_bad_omen_tooltip", true), INTERFACE);
    private final BooleanTweak fixGlowingTooltip = tweak(new BooleanTweak("fix_glowing_tooltip", true), INTERFACE);

    private final BooleanTweak fixEmptyCommandMinecartMessage = tweak(new BooleanTweak("fix_empty_command_minecart_message", true), COMMANDS);
    private final BooleanTweak fixRespawnAnchorStat = tweak(new BooleanTweak("fix_respawn_anchor_stat", true), COMMANDS);
    private final BooleanTweak fixLocateDistance = tweak(new BooleanTweak("fix_locate_distance", true), COMMANDS);
    private final BooleanTweak fixFlowerPotStat = tweak(new BooleanTweak("fix_flower_pot_stat", true), COMMANDS, "debugify");
    private final BooleanTweak fixReloadCommandSuggestions = tweak(new BooleanTweak("fix_reload_command_suggestions", true), COMMANDS);

    private final BooleanTweak fixResourceFilterLeak = tweak(new BooleanTweak("fix_resource_filter_leak", true), SERVER);
    private final BooleanTweak fixExpiredBanLogin = tweak(new BooleanTweak("fix_expired_ban_login", true), SERVER);
    private final BooleanTweak fixMissingServerProperties = tweak(new BooleanTweak("fix_missing_server_properties", true), SERVER);
    private final BooleanTweak fixRconNewlines = tweak(new BooleanTweak("fix_rcon_newlines", true), SERVER, "debugify", "neoforge");

    public VanillaBugfixesModule() {
        super("vanilla_bugfixes");
    }

    public static boolean enabled(Predicate<VanillaBugfixesModule> check) {
        return ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, check);
    }

    public boolean isFixFocusBugEnabled() {
        return fixFocusBug.getValue();
    }

    public boolean isAttackSleepingVillagersEnabled() {
        return attackSleepingVillagers.getValue();
    }

    public boolean isFixExperienceLossEnabled() {
        return fixExperienceLoss.getValue();
    }

    public boolean isFixResourceFilterLeakEnabled() {
        return fixResourceFilterLeak.getValue();
    }

    public boolean isFixHorizonLineEnabled() {
        return fixHorizonLine.getValue();
    }

    public boolean isFixCaveSkyEnabled() {
        return fixCaveSky.getValue();
    }

    public boolean isBetterPauseFreezingEnabled() {
        return betterPauseFreezing.getValue();
    }

    public boolean isColoredCrackParticlesEnabled() {
        return coloredCrackParticles.getValue();
    }

    public boolean isGhastChargingEnabled() {
        return ghastCharging.getValue();
    }

    public boolean isFixMobsCrossingRailsEnabled() {
        return fixMobsCrossingRails.getValue();
    }

    public boolean isFixUnsavedChunksEnabled() {
        return fixUnsavedChunks.getValue();
    }

    public boolean isFixSpectatorFireworkBoostEnabled() {
        return fixSpectatorFireworkBoost.getValue();
    }

    public boolean isFixItemFrameLoadSoundEnabled() {
        return fixItemFrameLoadSound.getValue();
    }

    public boolean isFixSpectatorItemUseEnabled() {
        return fixSpectatorItemUse.getValue();
    }

    public boolean isFixSpectatorFreezingEnabled() {
        return fixSpectatorFreezing.getValue();
    }

    public boolean isFixMirroredDoubleChestsEnabled() {
        return fixMirroredDoubleChests.getValue();
    }

    public boolean isFixDyingPufferfishStingEnabled() {
        return fixDyingPufferfishSting.getValue();
    }

    public boolean isFixLightningItemDropsEnabled() {
        return fixLightningItemDrops.getValue();
    }

    public boolean isFixExperienceOrbLavaEnabled() {
        return fixExperienceOrbLava.getValue();
    }

    public boolean isFixEndermanTeleportSpamEnabled() {
        return fixEndermanTeleportSpam.getValue();
    }

    public boolean isFixZombieDoorParticlesEnabled() {
        return fixZombieDoorParticles.getValue();
    }

    public boolean isFixCreeperIgniterDurabilityEnabled() {
        return fixCreeperIgniterDurability.getValue();
    }

    public boolean isFixWildWolfBreedingEnabled() {
        return fixWildWolfBreeding.getValue();
    }

    public boolean isFixPistonReloadUpdatesEnabled() {
        return fixPistonReloadUpdates.getValue();
    }

    public boolean isFixFishingRodKillCreditEnabled() {
        return fixFishingRodKillCredit.getValue();
    }

    public boolean isFixStrafingMobAimEnabled() {
        return fixStrafingMobAim.getValue();
    }

    public boolean isFixCommandMinecartCooldownEnabled() {
        return fixCommandMinecartCooldown.getValue();
    }

    public boolean isFixEmptyCommandMinecartMessageEnabled() {
        return fixEmptyCommandMinecartMessage.getValue();
    }

    public boolean isFixArmorStandBreakParticlesEnabled() {
        return fixArmorStandBreakParticles.getValue();
    }

    public boolean isFixDeathScreenItemUseEnabled() {
        return fixDeathScreenItemUse.getValue();
    }

    public boolean isFixRiptideDepthStriderEnabled() {
        return fixRiptideDepthStrider.getValue();
    }

    public boolean isFixFishingBobberSoundEnabled() {
        return fixFishingBobberSound.getValue();
    }

    public boolean isFixWitchHutCatsEnabled() {
        return fixWitchHutCats.getValue();
    }

    public boolean isFixFoxMobLootGameruleEnabled() {
        return fixFoxMobLootGamerule.getValue();
    }

    public boolean isFixExpiredBanLoginEnabled() {
        return fixExpiredBanLogin.getValue();
    }

    public boolean isFixEndIslandRingsEnabled() {
        return fixEndIslandRings.getValue();
    }

    public boolean isFixPistonMovedCactusEnabled() {
        return fixPistonMovedCactus.getValue();
    }

    public boolean isFixBadOmenTooltipEnabled() {
        return fixBadOmenTooltip.getValue();
    }

    public boolean isFixGlowingTooltipEnabled() {
        return fixGlowingTooltip.getValue();
    }

    public boolean isFixRespawnAnchorStatEnabled() {
        return fixRespawnAnchorStat.getValue();
    }

    public boolean isFixLocateDistanceEnabled() {
        return fixLocateDistance.getValue();
    }

    public boolean isFixCreeperDefusingEnabled() {
        return fixCreeperDefusing.getValue();
    }

    public boolean isFixGroupAiTargetDeathEnabled() {
        return fixGroupAiTargetDeath.getValue();
    }

    public boolean isFixCrystalsHealingDyingDragonEnabled() {
        return fixCrystalsHealingDyingDragon.getValue();
    }

    public boolean isFixCuredVillagerJockeyEnabled() {
        return fixCuredVillagerJockey.getValue();
    }

    public boolean isFixCampfireSmokePositionEnabled() {
        return fixCampfireSmokePosition.getValue();
    }

    public boolean isFixShulkerBulletBubblesEnabled() {
        return fixShulkerBulletBubbles.getValue();
    }

    public boolean isFixShulkerBulletImpactEnabled() {
        return fixShulkerBulletImpact.getValue();
    }

    public boolean isFixFlowerPotStatEnabled() {
        return fixFlowerPotStat.getValue();
    }

    public boolean isFixPeacefulStriderSaddlesEnabled() {
        return fixPeacefulStriderSaddles.getValue();
    }

    public boolean isFixRaidHornSoundEnabled() {
        return fixRaidHornSound.getValue();
    }

    public boolean isFixMissingServerPropertiesEnabled() {
        return fixMissingServerProperties.getValue();
    }

    public boolean isFixReloadCommandSuggestionsEnabled() {
        return fixReloadCommandSuggestions.getValue();
    }

    public boolean isFixStructureSavePathEnabled() {
        return fixStructureSavePath.getValue();
    }

    public boolean isFixStructurePaletteThreadingEnabled() {
        return fixStructurePaletteThreading.getValue();
    }

    public boolean isFixSlowFallingParticlesEnabled() {
        return fixSlowFallingParticles.getValue();
    }

    public boolean isFixRconNewlinesEnabled() {
        return fixRconNewlines.getValue();
    }

    public boolean isFixDragonLandingInVoidEnabled() {
        return fixDragonLandingInVoid.getValue();
    }

    public boolean isFixCaveSpiderSpinningEnabled() {
        return fixCaveSpiderSpinning.getValue();
    }

    public boolean isFixTwoByTwoSaplingsEnabled() {
        return fixTwoByTwoSaplings.getValue();
    }

    public boolean isFixRawCopperSoundsEnabled() {
        return fixRawCopperSounds.getValue();
    }

    public boolean isFixEatingSoundEnabled() {
        return fixEatingSound.getValue();
    }

    public boolean isFixShieldSoundsEnabled() {
        return fixShieldSounds.getValue();
    }

    public boolean isFixPathUnderBlocksEnabled() {
        return fixPathUnderBlocks.getValue();
    }
}
