#version 460 core

#include <sodium:globals.glsl>
#include <sodium:fog.glsl>
#include <sodium:chunk_material.glsl>
#include <minecraft:oit.glsl>

layout(location = 0) in vec4 v_Color;
layout(location = 1) in vec2 v_TexCoord;
layout(location = 2) in vec2 v_FragDistance;
layout(location = 3) in float fadeFactor;

uniform sampler2D u_BlockTex;
uniform sampler2D ModulationSkySampler;
uniform sampler2D ModulationCloudsSampler;
uniform sampler2D ModulationCloudsDepthSampler;

#ifndef OIT_ALPHA_ONLY
layout(location = 0) out vec4 fragColor;
#endif

const float MODULATION_BACKGROUND_BLEND_START = 0.9;

vec3 modulation_backgroundColor(ivec2 pixelCoords) {
    vec3 skyColor = texelFetch(ModulationSkySampler, pixelCoords, 0).rgb;
    vec4 cloudColor = texelFetch(ModulationCloudsSampler, pixelCoords, 0);
    float cloudDepth = texelFetch(ModulationCloudsDepthSampler, pixelCoords, 0).r;
    float cloudsBehind = step(cloudDepth, gl_FragCoord.z);
    return mix(skyColor, cloudColor.rgb / (cloudColor.a > 0.0 ? cloudColor.a : 1.0), cloudColor.a * cloudsBehind);
}

vec4 modulation_distanceFog(vec4 color, vec2 fragDistance, vec4 fogColor, vec2 environmentFog, vec2 renderFog, float fade) {
    float environmentalValue = linear_fog_value(fragDistance.y, environmentFog.x, environmentFog.y);
    float renderDistanceValue = linear_fog_value(fragDistance.x, renderFog.x, renderFog.y);

    float environmentalBackgroundValue = smoothstep(MODULATION_BACKGROUND_BLEND_START, 1.0, environmentalValue);

    float fogValue = max(environmentalValue, renderDistanceValue);

    float backgroundFogValue = max(environmentalBackgroundValue, renderDistanceValue);

    if (backgroundFogValue > 0.0 || fade < 1.0) {
        vec3 backgroundColor = modulation_backgroundColor(ivec2(gl_FragCoord.xy));
        vec3 targetColor = mix(fogColor.rgb, backgroundColor, backgroundFogValue);
        vec3 foggedColor = mix(color.rgb, targetColor, fogValue);
        return vec4(mix(backgroundColor, foggedColor, fade), color.a);
    }

    return vec4(mix(color.rgb, fogColor.rgb, fogValue), color.a);
}

#ifndef OIT_ALPHA_ONLY
vec4 calculateFinalColor(vec4 color) {
    color = modulation_distanceFog(color, v_FragDistance, u_FogColor, u_EnvironmentFog, u_RenderFog, fadeFactor);
    #ifdef OIT_ACCUMULATE
    color = sampleColorForAccumulation(color);
    #endif
    return color;
}
#endif

vec4 sampleNearest(sampler2D source, vec2 uv, vec2 pixelSize, vec2 du, vec2 dv, vec2 texelScreenSize) {
    vec2 uvTexelCoords = uv / pixelSize;
    vec2 texelCenter = round(uvTexelCoords) - 0.5f;
    vec2 texelOffset = uvTexelCoords - texelCenter;

    texelOffset = (texelOffset - 0.5f) * pixelSize / texelScreenSize + 0.5f;
    texelOffset = clamp(texelOffset, 0.0f, 1.0f);

    uv = (texelCenter + texelOffset) * pixelSize;
    return textureGrad(source, uv, du, dv);
}

vec4 sampleNearest(sampler2D source, vec2 uv, vec2 pixelSize) {
    vec2 du = dFdx(uv);
    vec2 dv = dFdy(uv);
    vec2 texelScreenSize = sqrt(du * du + dv * dv);
    return sampleNearest(source, uv, pixelSize, du, dv, texelScreenSize);
}

vec4 sampleRGSS(sampler2D source, vec2 uv, vec2 pixelSize) {
    vec2 du = dFdx(uv);
    vec2 dv = dFdy(uv);

    vec2 texelScreenSize = sqrt(du * du + dv * dv);
    float maxTexelSize = max(texelScreenSize.x, texelScreenSize.y);

    float minPixelSize = min(pixelSize.x, pixelSize.y);

    float transitionStart = minPixelSize * 1.0;
    float transitionEnd = minPixelSize * 2.0;
    float blendFactor = smoothstep(transitionStart, transitionEnd, maxTexelSize);

    float duLength = length(du);
    float dvLength = length(dv);
    float minDerivative = min(duLength, dvLength);
    float maxDerivative = max(duLength, dvLength);

    float effectiveDerivative = sqrt(minDerivative * maxDerivative);

    float mipLevelExact = max(0.0, log2(effectiveDerivative / minPixelSize));

    const vec2 offsets[4] = vec2[](
    vec2(0.125, 0.375),
    vec2(-0.125, -0.375),
    vec2(0.375, -0.125),
    vec2(-0.375, 0.125)
    );

    vec4 rgssColor = vec4(0.0);
    for (int i = 0; i < 4; ++i) {
        vec2 sampleUV = uv + offsets[i] * pixelSize;
        rgssColor += textureLod(source, sampleUV, mipLevelExact);
    }
    rgssColor *= 0.25;

    vec4 nearestColor = sampleNearest(source, uv, pixelSize, du, dv, texelScreenSize);

    return mix(nearestColor, rgssColor, blendFactor);
}

void main() {
    vec4 color = u_UseRGSS ? sampleRGSS(u_BlockTex, v_TexCoord, u_TexelSize) : sampleNearest(u_BlockTex, v_TexCoord, u_TexelSize);
    color *= v_Color;

#ifdef ALPHA_CUTOUT
    if (color.a < ALPHA_CUTOUT) {
        discard;
    }
#endif

    #ifdef OIT_ALPHA_ONLY
    executeAlphaOnlyPhase(gl_FragCoord.z, color.a);
    #else
    fragColor = calculateFinalColor(color);
    #endif
}
