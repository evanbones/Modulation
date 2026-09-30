float modulation_fog_value(float vertexDistance, float fogStart, float fogEnd) {
    if (vertexDistance <= fogStart) {
        return 0.0;
    } else if (vertexDistance >= fogEnd) {
        return 1.0;
    }

    return smoothstep(fogStart, fogEnd, vertexDistance);
}

vec3 modulation_background_color(ivec2 pixelCoords) {
    vec3 skyColor = texelFetch(ModulationSkySampler, pixelCoords, 0).rgb;
    vec4 cloudColor = texelFetch(ModulationCloudsSampler, pixelCoords, 0);
    float cloudDepth = texelFetch(ModulationCloudsDepthSampler, pixelCoords, 0).r;
    float cloudsBehind = step(gl_FragCoord.z, cloudDepth);
    return mix(skyColor, cloudColor.rgb / (cloudColor.a > 0.0 ? cloudColor.a : 1.0), cloudColor.a * cloudsBehind);
}

vec4 modulation_improved_fog(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
    if (ModulationImprovedFog <= 0.0) {
        return linear_fog(inColor, vertexDistance, fogStart, fogEnd, fogColor);
    }

    float environmentalValue = modulation_fog_value(vertexDistance, fogStart, fogEnd);
    if (environmentalValue <= 0.0) {
        return inColor;
    }

    float renderDistanceValue = modulation_fog_value(vertexDistance, ModulationRenderFog.x, ModulationRenderFog.y);
    float backgroundValue = max(smoothstep(0.9, 1.0, environmentalValue), renderDistanceValue);
    vec3 targetColor = fogColor.rgb;
    if (backgroundValue > 0.0) {
        targetColor = mix(fogColor.rgb, modulation_background_color(ivec2(gl_FragCoord.xy)), backgroundValue);
    }

    return vec4(mix(inColor.rgb, targetColor, environmentalValue * fogColor.a), inColor.a);
}
