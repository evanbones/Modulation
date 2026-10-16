package com.evandev.modulation.mixin.minecraft.accessor;

//? if <26.3 {
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@IfMinecraftVersion(maxVersion = "26.3", maxInclusive = false)
@Mixin(AxeItem.class)
public interface AxeItemAccessor {
    @Accessor("STRIPPABLES")
    static Map<Block, Block> modulation$getStrippables() {
        throw new AssertionError();
    }
}
//?} else {
/*import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(maxVersion = "26.3", maxInclusive = false)
@Mixin(targets = "net.minecraft.world.item.AxeItem")
public interface AxeItemAccessor {
}
*///?}
