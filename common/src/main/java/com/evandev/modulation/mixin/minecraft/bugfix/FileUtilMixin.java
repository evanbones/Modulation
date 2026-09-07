package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.FileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@IfModAbsent("neoforge")
@Mixin(FileUtil.class)
public class FileUtilMixin {

    @ModifyArg(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Ljava/util/regex/Pattern;compile(Ljava/lang/String;I)Ljava/util/regex/Pattern;"),
            index = 0
    )
    private static String modulation$widerReservedFilenamePattern(String regex) {
        if (!VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixStructureSavePathEnabled)) {
            return regex;
        }
        return ".*\\.|(?:CON|PRN|AUX|NUL|CLOCK\\$|CONIN\\$|CONOUT\\$|(?:COM|LPT)[\u00b9\u00b2\u00b30-9])(?:\\..*)?";
    }
}
