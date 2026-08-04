package com.evandev.modulation.modules.blockgrid.storage;

import net.minecraft.world.phys.Vec3;

public interface SignOffsetHolder {
    Vec3 modulation$getSignOffset();

    void modulation$setSignOffset(Vec3 offset);
}
