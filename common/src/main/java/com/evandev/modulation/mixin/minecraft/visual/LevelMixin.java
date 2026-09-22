package com.evandev.modulation.mixin.minecraft.visual;

import com.evandev.modulation.modules.vanilla.smoothlight.LightTransition;
import com.evandev.modulation.modules.vanilla.smoothlight.LightTransitionHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.ConcurrentHashMap;

@Mixin(Level.class)
public abstract class LevelMixin implements LightTransitionHolder {

    @Unique
    private volatile ConcurrentHashMap<Long, LightTransition> modulation$lightTransitions = null;

    @Unique
    @Override
    public ConcurrentHashMap<Long, LightTransition> modulation$lightTransitions() {
        ConcurrentHashMap<Long, LightTransition> map = this.modulation$lightTransitions;
        if (map != null) return map;
        synchronized (this) {
            if (this.modulation$lightTransitions == null) {
                this.modulation$lightTransitions = new ConcurrentHashMap<>();
            }
            return this.modulation$lightTransitions;
        }
    }

    @Unique
    @Nullable
    @Override
    public ConcurrentHashMap<Long, LightTransition> modulation$lightTransitionsOrNull() {
        return this.modulation$lightTransitions;
    }
}
