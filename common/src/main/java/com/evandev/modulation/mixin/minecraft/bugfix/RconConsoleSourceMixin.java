package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.rcon.RconConsoleSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("debugify")
@IfModAbsent("neoforge")
@Mixin(RconConsoleSource.class)
public class RconConsoleSourceMixin {

    @Shadow
    @Final
    private StringBuffer buffer;

    @Inject(method = "sendSystemMessage", at = @At("RETURN"))
    private void modulation$keepRconNewlines(Component component, CallbackInfo ci) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixRconNewlinesEnabled)) {
            buffer.append(System.lineSeparator());
        }
    }
}
