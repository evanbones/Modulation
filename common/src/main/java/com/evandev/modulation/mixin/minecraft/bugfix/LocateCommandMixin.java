package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.commands.LocateCommand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocateCommand.class)
public class LocateCommandMixin {

    @WrapMethod(method = "dist")
    private static float modulation$locateDistanceWithoutOverflow(int x1, int z1, int x2, int z2, Operation<Float> original) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixLocateDistanceEnabled)) {
            return (float) Math.hypot((double) x2 - x1, (double) z2 - z1);
        }
        return original.call(x1, z1, x2, z2);
    }
}
