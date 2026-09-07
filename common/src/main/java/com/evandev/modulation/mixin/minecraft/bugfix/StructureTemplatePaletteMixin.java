package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.google.common.collect.Maps;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@IfModAbsent("neoforge")
@Mixin(StructureTemplate.Palette.class)
public class StructureTemplatePaletteMixin {

    @Shadow
    @Final
    @Mutable
    private Map<Block, List<StructureTemplate.StructureBlockInfo>> cache = VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixStructurePaletteThreadingEnabled)
            ? Maps.newConcurrentMap()
            : Maps.newHashMap();
}
