float modulation_skyness(float d) {
    float visible = 1.0 - clamp(ModulationSkyHidden, 0.0, 1.0);
    if (visible <= 0.0) {
        return 0.0;
    }

    float raw = step(0.999999, d);
    float fogStart = ModulationFogRange.x;
    float fogEnd = ModulationFogRange.y;

    if (ModulationSkyVisibility <= 0.0 || fogEnd <= fogStart || fogEnd <= 0.0) {
        return visible * raw;
    }

    float a = PolyProjMat[2][2];
    float b = PolyProjMat[3][2];
    float dist = b / ((d * 2.0 - 1.0) + a);
    float fogged = smoothstep(fogStart, fogEnd, dist);

    return visible * max(raw, ModulationSkyVisibility * fogged);
}
