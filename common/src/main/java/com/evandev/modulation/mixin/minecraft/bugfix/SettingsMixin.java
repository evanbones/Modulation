package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.dedicated.Settings;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Mixin(Settings.class)
public class SettingsMixin {

    @WrapMethod(method = "loadFromFile")
    private static Properties modulation$skipMissingSettingsFile(Path path, Operation<Properties> original) {
        if (Files.notExists(path) && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixMissingServerPropertiesEnabled)) {
            return new Properties();
        }
        return original.call(path);
    }
}
