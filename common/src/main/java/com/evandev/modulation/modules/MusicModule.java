package com.evandev.modulation.modules;

import com.evandev.modulation.Constants;
import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.evandev.modulation.api.tweaks.StringListTweak;
import com.evandev.modulation.mixin.minecraft.accessor.MusicManagerAccessor;
import com.evandev.modulation.mixin.minecraft.accessor.SoundManagerAccessor;
import com.evandev.modulation.modules.music.CombatSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MusicModule implements IModule {

    private final IntTweak minPursuitEntities = new IntTweak("min_pursuit_entities", 3);
    private final IntTweak decayTime = new IntTweak("decay_time", 20);
    private final StringListTweak sounds = new StringListTweak("sounds", List.of("minecraft:music_disc.pigstep", "minecraft:music_disc.mellohi"));
    private final float fadeRate = 1.0f / 40.0f;
    private final BooleanTweak enabled = new BooleanTweak("enabled", false);
    private CombatSoundInstance currentCombatMusic;
    private boolean inCombat = false;
    private int ticksSinceCombatUpdate = 0;
    private float vanillaMusicMultiplier = 1.0f;
    private boolean wasHurtLastTick = false;

    @Override
    public String getId() {
        return "music";
    }

    @Override
    public boolean shouldLoad() {
        return true;
    }

    @Override
    public List<AbstractTweak<?>> getTweaks() {
        return List.of(enabled, minPursuitEntities, decayTime, sounds);
    }

    @Override
    public void initialize() {
    }

    public boolean isCombatSound(SoundInstance sound) {
        return sound == currentCombatMusic;
    }

    public float getVanillaMusicMultiplier() {
        return vanillaMusicMultiplier;
    }

    public void onClientTick(Minecraft mc) {
        if (mc.level == null || !enabled.getValue()) return;

        LocalPlayer player = mc.player;
        if (player == null) return;

        boolean isHurt = player.hurtTime > 0;
        if (isHurt && !wasHurtLastTick) {
            if (mc.level.getDifficulty() != Difficulty.PEACEFUL) {
                ticksSinceCombatUpdate = 0;
                inCombat = true;
            }
        }
        wasHurtLastTick = isHurt;

        if (mc.level.getGameTime() % 20 == 0) {
            int entityCount = getEntities(player);
            if (entityCount > minPursuitEntities.getValue()) {
                ticksSinceCombatUpdate = 0;
                inCombat = true;
            } else {
                ticksSinceCombatUpdate += 20;
            }

            if (inCombat && ticksSinceCombatUpdate > decayTime.getValue() * 20) {
                endCombat();
            }
        }

        boolean volumeChanged = false;
        if (inCombat) {
            if (vanillaMusicMultiplier > 0.001f) {
                SoundInstance currentVanillaMusic = ((MusicManagerAccessor) mc.getMusicManager()).getCurrentMusic();
                if (currentVanillaMusic == null || !mc.getSoundManager().isActive(currentVanillaMusic)) {
                    vanillaMusicMultiplier = 0.001f;
                } else {
                    vanillaMusicMultiplier -= fadeRate;
                }

                if (vanillaMusicMultiplier < 0.001f) vanillaMusicMultiplier = 0.001f;
                volumeChanged = true;
            } else if (currentCombatMusic == null || currentCombatMusic.isStopped()) {
                startCombatMusic(mc);
            }
        } else {
            if (vanillaMusicMultiplier < 1.0f) {
                vanillaMusicMultiplier += fadeRate;
                if (vanillaMusicMultiplier > 1.0f) vanillaMusicMultiplier = 1.0f;
                volumeChanged = true;
            }
        }

        if (volumeChanged) {
            updateVanillaVolumes(mc);
        }
    }

    private void startCombatMusic(Minecraft mc) {
        SoundEvent sound = pickSound(mc.player.getRandom());
        if (sound != null) {
            currentCombatMusic = new CombatSoundInstance(sound);
            mc.getSoundManager().play(currentCombatMusic);
        }
    }

    private void endCombat() {
        inCombat = false;
        if (currentCombatMusic != null) {
            currentCombatMusic.fadeOut();
        }
    }

    private void updateVanillaVolumes(Minecraft mc) {
        SoundManager manager = mc.getSoundManager();
        SoundEngine engine = ((SoundManagerAccessor) manager).getSoundEngine();
        engine.updateCategoryVolume(SoundSource.MUSIC, 1.0f);
        engine.updateCategoryVolume(SoundSource.RECORDS, 1.0f);
    }

    private int getEntities(LocalPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return 0;
        }

        AABB box = new AABB(-12D, -10D, -12D, 12D, 10D, 12D).move(player.blockPosition());
        return player.clientLevel.getEntitiesOfClass(
                Monster.class,
                box,
                LivingEntity::isAlive
        ).size();
    }

    private SoundEvent pickSound(RandomSource rand) {
        List<String> soundList = sounds.getValue();
        if (soundList.isEmpty()) return null;

        int idx = rand.nextInt(soundList.size());
        String soundStr = soundList.get(idx).trim();

        ResourceLocation soundLocation = ResourceLocation.parse(soundStr);
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(soundLocation);

        if (sound == null) {
            Constants.LOG.error("Invalid sound event resource location detected: {}", soundStr);
            return null;
        }
        return sound;
    }
}