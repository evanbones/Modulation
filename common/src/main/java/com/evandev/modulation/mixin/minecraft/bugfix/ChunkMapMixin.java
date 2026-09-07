package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@IfModAbsent("debugify")
@IfModAbsent("chunksavingfix")
@IfModAbsent("moonrise")
@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @Unique
    private static boolean modulation$isUnsavedChunkFixEnabled() {
        return ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixUnsavedChunksEnabled);
    }

    @ModifyArg(
            method = "saveAllChunks",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 0)
    )
    private Predicate<ChunkHolder> modulation$saveInaccessibleChunks(Predicate<ChunkHolder> original) {
        return modulation$isUnsavedChunkFixEnabled() ? holder -> true : original;
    }

    @ModifyArg(
            method = "saveAllChunks",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 1)
    )
    private Predicate<ChunkAccess> modulation$saveProtoChunks(Predicate<ChunkAccess> original) {
        return modulation$isUnsavedChunkFixEnabled() ? chunk -> original.test(chunk) || chunk instanceof ProtoChunk : original;
    }

    @ModifyExpressionValue(
            method = "saveChunkIfNeeded",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkHolder;wasAccessibleSinceLastSave()Z")
    )
    private boolean modulation$saveInaccessibleChunkIfNeeded(boolean original) {
        return original || modulation$isUnsavedChunkFixEnabled();
    }
}
