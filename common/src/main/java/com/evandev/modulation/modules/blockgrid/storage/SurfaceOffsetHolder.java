package com.evandev.modulation.modules.blockgrid.storage;

import org.joml.Vector3f;

public interface SurfaceOffsetHolder {
    Vector3f modulation$getSurfaceOffset();

    void modulation$setSurfaceOffset(Vector3f offset);
}
