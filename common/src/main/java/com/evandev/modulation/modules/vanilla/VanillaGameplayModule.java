package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class VanillaGameplayModule extends AbstractModule {

    private final BooleanTweak flammableCobwebs = tweak(new BooleanTweak("flammable_cobwebs", false));
    private final BooleanTweak campfiresPlaceUnlit = tweak(new BooleanTweak("campfires_place_unlit", false));
    private final BooleanTweak campfiresIgniteEntities = tweak(new BooleanTweak("campfires_ignite_entities", false));
    private final BooleanTweak disableAxeStripping = tweak(new BooleanTweak("disable_axe_stripping", false));
    private final BooleanTweak disableCopperScraping = tweak(new BooleanTweak("disable_copper_scraping", false));
    private final BooleanTweak noDinnerlava = tweak(new BooleanTweak("no_dinnerlava", false));
    private final BooleanTweak enderPearlSound = tweak(new BooleanTweak("ender_pearl_sound", false));
    private final BooleanTweak crackingSpawnEggs = tweak(new BooleanTweak("cracking_spawn_eggs", false));
    private final BooleanTweak disableAnvilDamage = tweak(new BooleanTweak("disable_anvil_damage", false));
    private final BooleanTweak tridentsInVoidReturn = tweak(new BooleanTweak("tridents_in_void_return", false));
    private final BooleanTweak cactusPunchingHurts = tweak(new BooleanTweak("cactus_punching_hurts", false));
    private final BooleanTweak chainingCreepers = tweak(new BooleanTweak("chaining_creepers", false));
    private final BooleanTweak furnaceMinecartAnyFuel = tweak(new BooleanTweak("furnace_minecart_any_fuel", false));
    private final BooleanTweak infibows = tweak(new BooleanTweak("infibows", false));
    private final BooleanTweak tridentsAcceptSharpness = tweak(new BooleanTweak("tridents_accept_sharpness", false));
    private final BooleanTweak bedrockImpaling = tweak(new BooleanTweak("bedrock_impaling", false));
    private final BooleanTweak netherCauldron = tweak(new BooleanTweak("nether_cauldron", false));
    private final BooleanTweak fireAspectIsFlintAndSteel = tweak(new BooleanTweak("fire_aspect_is_flint_and_steel", false));
    private final BooleanTweak noChestWhenTargeted = tweak(new BooleanTweak("no_chest_when_targeted", false));

    public VanillaGameplayModule() {
        super("vanilla_gameplay");
    }

    public boolean isFlammableCobwebsEnabled() {
        return flammableCobwebs.getValue();
    }

    public boolean isCampfiresPlaceUnlitEnabled() {
        return campfiresPlaceUnlit.getValue();
    }

    public boolean isCampfiresIgniteEntitiesEnabled() {
        return campfiresIgniteEntities.getValue();
    }

    public boolean isDisableAxeStrippingEnabled() {
        return disableAxeStripping.getValue();
    }

    public boolean isDisableCopperScrapingEnabled() {
        return disableCopperScraping.getValue();
    }

    public boolean isNoDinnerlavaEnabled() {
        return noDinnerlava.getValue();
    }

    public boolean isEnderPearlSoundEnabled() {
        return enderPearlSound.getValue();
    }

    public boolean isCrackingSpawnEggsEnabled() {
        return crackingSpawnEggs.getValue();
    }

    public boolean isDisableAnvilDamageEnabled() {
        return disableAnvilDamage.getValue();
    }

    public boolean isTridentsInVoidReturnEnabled() {
        return tridentsInVoidReturn.getValue();
    }

    public boolean isCactusPunchingHurtsEnabled() {
        return cactusPunchingHurts.getValue();
    }

    public boolean isChainingCreepersEnabled() {
        return chainingCreepers.getValue();
    }

    public boolean isFurnaceMinecartAnyFuelEnabled() {
        return furnaceMinecartAnyFuel.getValue();
    }

    public boolean isInfibowsEnabled() {
        return infibows.getValue();
    }

    public boolean isTridentsAcceptSharpnessEnabled() {
        return tridentsAcceptSharpness.getValue();
    }

    public boolean isBedrockImpalingEnabled() {
        return bedrockImpaling.getValue();
    }

    public boolean isNetherCauldronEnabled() {
        return netherCauldron.getValue();
    }

    public boolean isFireAspectIsFlintAndSteelEnabled() {
        return fireAspectIsFlintAndSteel.getValue();
    }

    public boolean isNoChestWhenTargetedEnabled() {
        return noChestWhenTargeted.getValue();
    }
}
