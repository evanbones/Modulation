package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@IfModAbsent("debugify")
@Mixin(Blocks.class)
public class BlocksMixin {

    @ModifyArg(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=raw_copper_block")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            index = 1
    )
    private static Block modulation$rawCopperBlockSounds(Block block) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixRawCopperSoundsEnabled)) {
            return new Block(BlockBehaviour.Properties.ofFullCopy(block).sound(SoundType.COPPER));
        }
        return block;
    }
}
