vec2 modulation_depthCoord = vec2(0.5);

vec4 modulation_sample_depth(vec2 uv) {
    modulation_depthCoord = uv;
    return texture(InDepth, uv);
}

float modulation_fog_distance(float d) {
    vec4 view = inverse(PolyProjMat) * vec4(modulation_depthCoord * 2.0 - 1.0, d * 2.0 - 1.0, 1.0);
    vec3 pos = transpose(mat3(PolyModelViewMat)) * (view.xyz / view.w);
    if (ModulationFogShape == 0) {
        return length(pos);
    }
    return max(length(pos.xz), abs(pos.y));
}

float modulation_fog_value(float dist, vec2 range) {
    if (dist <= range.x) {
        return 0.0;
    } else if (dist >= range.y) {
        return 1.0;
    }
    return smoothstep(range.x, range.y, dist);
}

float modulation_fogged_sky(float d) {
    vec2 fogRange = ModulationFogRange;
    if (fogRange.y <= fogRange.x || fogRange.y <= 0.0) {
        return 0.0;
    }

    float dist = modulation_fog_distance(d);
    float environmental = modulation_fog_value(dist, fogRange);
    if (environmental <= 0.0) {
        return 0.0;
    }

    float background = smoothstep(0.9, 1.0, environmental);
    if (ModulationRenderFogRange.y > ModulationRenderFogRange.x) {
        background = max(background, modulation_fog_value(dist, ModulationRenderFogRange));
    }
    return environmental * background;
}

float modulation_skyness(float d) {
    float visible = 1.0 - clamp(ModulationSkyHidden, 0.0, 1.0);
    if (visible <= 0.0) {
        return 0.0;
    }

    float raw = step(0.999999, d);
    if (raw >= 1.0 || ModulationSkyVisibility <= 0.0) {
        return visible * raw;
    }

    return visible * ModulationSkyVisibility * modulation_fogged_sky(d);
}

float modulation_sky_coverage(float d) {
    float raw = step(0.999999, d);
    if (raw >= 1.0) {
        return 1.0;
    }
    return modulation_fogged_sky(d);
}
