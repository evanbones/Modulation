package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class VanillaGameplayModule extends AbstractModule {

    private static final ModuleDef DEF = ModuleDef.of("vanilla_gameplay");

    public static final BooleanTweak FLAMMABLE_COBWEBS = DEF.bool("flammable_cobwebs", false);
    public static final BooleanTweak CAMPFIRES_PLACE_UNLIT = DEF.bool("campfires_place_unlit", false);
    public static final BooleanTweak CAMPFIRES_IGNITE_ENTITIES = DEF.bool("campfires_ignite_entities", false);
    public static final BooleanTweak DISABLE_AXE_STRIPPING = DEF.bool("disable_axe_stripping", false);
    public static final BooleanTweak DISABLE_COPPER_SCRAPING = DEF.bool("disable_copper_scraping", false);
    public static final BooleanTweak NO_DINNERLAVA = DEF.bool("no_dinnerlava", false);
    public static final BooleanTweak ENDER_PEARL_SOUND = DEF.bool("ender_pearl_sound", false);
    public static final BooleanTweak CRACKING_SPAWN_EGGS = DEF.bool("cracking_spawn_eggs", false);
    public static final BooleanTweak DISABLE_ANVIL_DAMAGE = DEF.bool("disable_anvil_damage", false);
    public static final BooleanTweak TRIDENTS_IN_VOID_RETURN = DEF.bool("tridents_in_void_return", false);
    public static final BooleanTweak CACTUS_PUNCHING_HURTS = DEF.bool("cactus_punching_hurts", false);
    public static final BooleanTweak CHAINING_CREEPERS = DEF.bool("chaining_creepers", false);
    public static final BooleanTweak FURNACE_MINECART_ANY_FUEL = DEF.bool("furnace_minecart_any_fuel", false);
    public static final BooleanTweak INFIBOWS = DEF.bool("infibows", false);
    public static final BooleanTweak TRIDENTS_ACCEPT_SHARPNESS = DEF.bool("tridents_accept_sharpness", false);
    public static final BooleanTweak BEDROCK_IMPALING = DEF.bool("bedrock_impaling", false);
    public static final BooleanTweak NETHER_CAULDRON = DEF.bool("nether_cauldron", false);
    public static final BooleanTweak FIRE_ASPECT_IS_FLINT_AND_STEEL = DEF.bool("fire_aspect_is_flint_and_steel", false);
    public static final BooleanTweak NO_CHEST_WHEN_TARGETED = DEF.bool("no_chest_when_targeted", false);
    public static final BooleanTweak DISABLE_PATH_CREATION = DEF.bool("disable_path_creation", false);
    public static final BooleanTweak DISABLE_FARMLAND_CREATION = DEF.bool("disable_farmland_creation", false);
    public static final BooleanTweak DISABLE_ENDER_PEARL_DAMAGE = DEF.bool("disable_ender_pearl_damage", false);
    public static final BooleanTweak DISPENSER_SHEARS_PUMPKINS = DEF.bool("dispenser_shears_pumpkins", false);
    public static final BooleanTweak WATER_BOTTLES_ON_CONCRETE = DEF.bool("water_bottles_on_concrete", false);
    public static final BooleanTweak LEAVES_SUPPORT_BLOCKS = DEF.bool("leaves_support_blocks", false);
    public static final BooleanTweak MONSTERS_LEAVE_BOATS = DEF.bool("monsters_leave_boats", false);

    public VanillaGameplayModule() {
        super(DEF);
    }
}