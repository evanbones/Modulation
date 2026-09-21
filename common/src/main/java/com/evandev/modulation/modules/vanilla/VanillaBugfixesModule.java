package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

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

    private static final ModuleDef DEF = ModuleDef.of("vanilla_bugfixes");

    public static final BooleanTweak ATTACK_SLEEPING_VILLAGERS = DEF.bool("attack_sleeping_villagers", true).group(ENTITIES);
    public static final BooleanTweak FIX_MOBS_CROSSING_RAILS = DEF.bool("fix_mobs_crossing_rails", false).group(ENTITIES);
    public static final BooleanTweak FIX_DYING_PUFFERFISH_STING = DEF.bool("fix_dying_pufferfish_sting", true).group(ENTITIES).conflicts("debugify");
    public static final BooleanTweak FIX_LIGHTNING_ITEM_DROPS = DEF.bool("fix_lightning_item_drops", true).group(ENTITIES).conflicts("debugify");
    public static final BooleanTweak FIX_EXPERIENCE_ORB_LAVA = DEF.bool("fix_experience_orb_lava", true).group(ENTITIES);
    public static final BooleanTweak FIX_ENDERMAN_TELEPORT_SPAM = DEF.bool("fix_enderman_teleport_spam", true).group(ENTITIES);
    public static final BooleanTweak FIX_WILD_WOLF_BREEDING = DEF.bool("fix_wild_wolf_breeding", true).group(ENTITIES).conflicts("debugify");
    public static final BooleanTweak FIX_STRAFING_MOB_AIM = DEF.bool("fix_strafing_mob_aim", true).group(ENTITIES).conflicts("debugify");
    public static final BooleanTweak FIX_WITCH_HUT_CATS = DEF.bool("fix_witch_hut_cats", true).group(ENTITIES).conflicts("neoforge");
    public static final BooleanTweak FIX_FOX_MOB_LOOT_GAMERULE = DEF.bool("fix_fox_mob_loot_gamerule", true).group(ENTITIES);
    public static final BooleanTweak FIX_CREEPER_DEFUSING = DEF.bool("fix_creeper_defusing", true).group(ENTITIES).conflicts("debugify");
    public static final BooleanTweak FIX_GROUP_AI_TARGET_DEATH = DEF.bool("fix_group_ai_target_death", true).group(ENTITIES).conflicts("debugify");
    public static final BooleanTweak FIX_CRYSTALS_HEALING_DYING_DRAGON = DEF.bool("fix_crystals_healing_dying_dragon", true).group(ENTITIES);
    public static final BooleanTweak FIX_CURED_VILLAGER_JOCKEY = DEF.bool("fix_cured_villager_jockey", true).group(ENTITIES).conflicts("debugify");
    public static final BooleanTweak FIX_PEACEFUL_STRIDER_SADDLES = DEF.bool("fix_peaceful_strider_saddles", true).group(ENTITIES).conflicts("debugify");
    public static final BooleanTweak FIX_DRAGON_LANDING_IN_VOID = DEF.bool("fix_dragon_landing_in_void", true).group(ENTITIES).conflicts("debugify");
    public static final BooleanTweak FIX_CAVE_SPIDER_SPINNING = DEF.bool("fix_cave_spider_spinning", true).group(ENTITIES).conflicts("neoforge");
    public static final BooleanTweak FIX_GHAST_DIRECTION = DEF.bool("fix_ghast_direction", true).group(ENTITIES).conflicts("ghastdirection");

    public static final BooleanTweak FIX_EXPERIENCE_LOSS = DEF.bool("fix_experience_loss", true).group(PLAYER);
    public static final BooleanTweak FIX_SPECTATOR_FIREWORK_BOOST = DEF.bool("fix_spectator_firework_boost", true).group(PLAYER).conflicts("debugify");
    public static final BooleanTweak FIX_SPECTATOR_ITEM_USE = DEF.bool("fix_spectator_item_use", true).group(PLAYER).conflicts("debugify");
    public static final BooleanTweak FIX_SPECTATOR_FREEZING = DEF.bool("fix_spectator_freezing", true).group(PLAYER).conflicts("debugify");
    public static final BooleanTweak FIX_FISHING_ROD_KILL_CREDIT = DEF.bool("fix_fishing_rod_kill_credit", true).group(PLAYER).conflicts("debugify");
    public static final BooleanTweak FIX_DEATH_SCREEN_ITEM_USE = DEF.bool("fix_death_screen_item_use", true).group(PLAYER);
    public static final BooleanTweak FIX_RIPTIDE_DEPTH_STRIDER = DEF.bool("fix_riptide_depth_strider", true).group(PLAYER);

    public static final BooleanTweak FIX_MIRRORED_DOUBLE_CHESTS = DEF.bool("fix_mirrored_double_chests", true).group(BLOCKS_ITEMS).conflicts("neoforge");
    public static final BooleanTweak FIX_CREEPER_IGNITER_DURABILITY = DEF.bool("fix_creeper_igniter_durability", true).group(BLOCKS_ITEMS);
    public static final BooleanTweak FIX_PISTON_MOVED_CACTUS = DEF.bool("fix_piston_moved_cactus", true).group(BLOCKS_ITEMS).conflicts("debugify");
    public static final BooleanTweak FIX_CAMPFIRE_SMOKE_POSITION = DEF.bool("fix_campfire_smoke_position", true).group(BLOCKS_ITEMS).conflicts("neoforge");
    public static final BooleanTweak FIX_PATH_UNDER_BLOCKS = DEF.bool("fix_path_under_blocks", true).group(BLOCKS_ITEMS).conflicts("pathunderfencegates");

    public static final BooleanTweak FIX_UNSAVED_CHUNKS = DEF.bool("fix_unsaved_chunks", true).group(WORLD).conflicts("chunksavingfix", "debugify", "moonrise");
    public static final BooleanTweak FIX_PISTON_RELOAD_UPDATES = DEF.bool("fix_piston_reload_updates", true).group(WORLD).conflicts("debugify");
    public static final BooleanTweak FIX_COMMAND_MINECART_COOLDOWN = DEF.bool("fix_command_minecart_cooldown", true).group(WORLD).conflicts("debugify");
    public static final BooleanTweak FIX_END_ISLAND_RINGS = DEF.bool("fix_end_island_rings", true).group(WORLD);
    public static final BooleanTweak FIX_STRUCTURE_SAVE_PATH = DEF.bool("fix_structure_save_path", true).group(WORLD).conflicts("neoforge");
    public static final BooleanTweak FIX_STRUCTURE_PALETTE_THREADING = DEF.bool("fix_structure_palette_threading", true).group(WORLD).conflicts("neoforge");
    public static final BooleanTweak FIX_TWO_BY_TWO_SAPLINGS = DEF.bool("fix_two_by_two_saplings", true).group(WORLD).conflicts("debugify", "flwr-8187");

    public static final BooleanTweak FIX_HORIZON_LINE = DEF.bool("fix_horizon_line", true).group(RENDERING);
    public static final BooleanTweak FIX_CAVE_SKY = DEF.bool("fix_cave_sky", true).group(RENDERING);
    public static final BooleanTweak PATCH_SUNBATHING_GODRAYS = DEF.bool("patch_sunbathing_godrays", true).group(RENDERING);
    public static final BooleanTweak BETTER_PAUSE_FREEZING = DEF.bool("better_pause_freezing", true).group(RENDERING);
    public static final BooleanTweak COLORED_CRACK_PARTICLES = DEF.bool("colored_crack_particles", true).group(RENDERING);
    public static final BooleanTweak GHAST_CHARGING = DEF.bool("ghast_charging", true).group(RENDERING);
    public static final BooleanTweak FIX_ZOMBIE_DOOR_PARTICLES = DEF.bool("fix_zombie_door_particles", true).group(RENDERING);
    public static final BooleanTweak FIX_ARMOR_STAND_BREAK_PARTICLES = DEF.bool("fix_armor_stand_break_particles", true).group(RENDERING).conflicts("debugify");
    public static final BooleanTweak FIX_SHULKER_BULLET_BUBBLES = DEF.bool("fix_shulker_bullet_bubbles", true).group(RENDERING);
    public static final BooleanTweak FIX_SHULKER_BULLET_IMPACT = DEF.bool("fix_shulker_bullet_impact", true).group(RENDERING);
    public static final BooleanTweak FIX_SLOW_FALLING_PARTICLES = DEF.bool("fix_slow_falling_particles", true).group(RENDERING).conflicts("debugify");

    public static final BooleanTweak FIX_ITEM_FRAME_LOAD_SOUND = DEF.bool("fix_item_frame_load_sound", true).group(SOUND);
    public static final BooleanTweak FIX_FISHING_BOBBER_SOUND = DEF.bool("fix_fishing_bobber_sound", true).group(SOUND);
    public static final BooleanTweak FIX_RAID_HORN_SOUND = DEF.bool("fix_raid_horn_sound", true).group(SOUND);
    public static final BooleanTweak FIX_RAW_COPPER_SOUNDS = DEF.bool("fix_raw_copper_sounds", true).group(SOUND).conflicts("debugify");
    public static final BooleanTweak FIX_EATING_SOUND = DEF.bool("fix_eating_sound", true).group(SOUND);
    public static final BooleanTweak FIX_SHIELD_SOUNDS = DEF.bool("fix_shield_sounds", true).group(SOUND);

    public static final BooleanTweak FIX_FOCUS_BUG = DEF.bool("fix_focus_bug", true).group(INTERFACE);
    public static final BooleanTweak FIX_BAD_OMEN_TOOLTIP = DEF.bool("fix_bad_omen_tooltip", true).group(INTERFACE);
    public static final BooleanTweak FIX_GLOWING_TOOLTIP = DEF.bool("fix_glowing_tooltip", true).group(INTERFACE);

    public static final BooleanTweak FIX_EMPTY_COMMAND_MINECART_MESSAGE = DEF.bool("fix_empty_command_minecart_message", true).group(COMMANDS);
    public static final BooleanTweak FIX_RESPAWN_ANCHOR_STAT = DEF.bool("fix_respawn_anchor_stat", true).group(COMMANDS);
    public static final BooleanTweak FIX_LOCATE_DISTANCE = DEF.bool("fix_locate_distance", true).group(COMMANDS);
    public static final BooleanTweak FIX_FLOWER_POT_STAT = DEF.bool("fix_flower_pot_stat", true).group(COMMANDS).conflicts("debugify");
    public static final BooleanTweak FIX_RELOAD_COMMAND_SUGGESTIONS = DEF.bool("fix_reload_command_suggestions", true).group(COMMANDS);

    public static final BooleanTweak FIX_RESOURCE_FILTER_LEAK = DEF.bool("fix_resource_filter_leak", true).group(SERVER);
    public static final BooleanTweak FIX_EXPIRED_BAN_LOGIN = DEF.bool("fix_expired_ban_login", true).group(SERVER);
    public static final BooleanTweak FIX_MISSING_SERVER_PROPERTIES = DEF.bool("fix_missing_server_properties", true).group(SERVER);
    public static final BooleanTweak FIX_RCON_NEWLINES = DEF.bool("fix_rcon_newlines", true).group(SERVER).conflicts("debugify", "neoforge");

    public VanillaBugfixesModule() {
        super(DEF);
    }
}
