package com.evandev.modulation.mixin.minecraft.bugfix;

//? if <26.1 {
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {

    @IfModAbsent("neoforge")
    @WrapMethod(method = "mirror")
    private BlockState modulation$fixMirroredDoubleChests(BlockState state, Mirror mirror, Operation<BlockState> original) {
        BlockState result = original.call(state, mirror);
        if (mirror != Mirror.NONE && VanillaBugfixesModule.FIX_MIRRORED_DOUBLE_CHESTS.on()) {
            return result.setValue(ChestBlock.TYPE, result.getValue(ChestBlock.TYPE).getOpposite());
        }
        return result;
    }
}
//?}
