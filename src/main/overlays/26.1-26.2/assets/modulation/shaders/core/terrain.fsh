#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:chunksection.glsl>

uniform sampler2D Sampler0;
uniform sampler2D ModulationSkySampler;
uniform sampler2D ModulationCloudsSampler;
uniform sampler2D ModulationCloudsDepthSampler;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

const float ENVIRONMENTAL_BACKGROUND_BLEND_START = 0.9;

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

    float mipLevelLow = floor(mipLevelExact);
    float mipLevelHigh = mipLevelLow + 1.0;
    float mipBlend = fract(mipLevelExact);

    const vec2 offsets[4] = vec2[](
    vec2(0.125, 0.375),
    vec2(-0.125, -0.375),
    vec2(0.375, -0.125),
    vec2(-0.375, 0.125)
    );

    vec4 rgssColorLow = vec4(0.0);
    vec4 rgssColorHigh = vec4(0.0);
    for (int i = 0; i < 4; ++i) {
        vec2 sampleUV = uv + offsets[i] * pixelSize;
        rgssColorLow += textureLod(source, sampleUV, mipLevelLow);
        rgssColorHigh += textureLod(source, sampleUV, mipLevelHigh);
    }
    rgssColorLow *= 0.25;
    rgssColorHigh *= 0.25;

    vec4 rgssColor = mix(rgssColorLow, rgssColorHigh, mipBlend);

    vec4 nearestColor = sampleNearest(source, uv, pixelSize, du, dv, texelScreenSize);

    return mix(nearestColor, rgssColor, blendFactor);
}

vec3 sampleBackgroundColor(ivec2 pixelCoords) {
    vec3 skyColor = texelFetch(ModulationSkySampler, pixelCoords, 0).rgb;
    vec4 cloudColor = texelFetch(ModulationCloudsSampler, pixelCoords, 0);
    float cloudDepth = texelFetch(ModulationCloudsDepthSampler, pixelCoords, 0).r;
#ifdef MODULATION_REVERSED_Z
    float cloudsBehind = step(cloudDepth, gl_FragCoord.z);
#else
    float cloudsBehind = step(gl_FragCoord.z, cloudDepth);
#endif
    return mix(skyColor, cloudColor.rgb / (cloudColor.a > 0.0 ? cloudColor.a : 1.0), cloudColor.a * cloudsBehind);
}

vec4 calculateFinalColor(vec4 color) {
    float environmentalValue = linear_fog_value(sphericalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd);
    float renderDistanceValue = linear_fog_value(cylindricalVertexDistance, FogRenderDistanceStart, FogRenderDistanceEnd);

    float environmentalBackgroundValue = smoothstep(ENVIRONMENTAL_BACKGROUND_BLEND_START, 1.0, environmentalValue);

    float fogValue = max(environmentalValue, renderDistanceValue);

    float backgroundFogValue = max(environmentalBackgroundValue, renderDistanceValue);

    if (backgroundFogValue > 0.0 || ChunkVisibility < 1.0) {
        vec3 backgroundColor = sampleBackgroundColor(ivec2(gl_FragCoord.xy));
        vec3 targetColor = mix(FogColor.rgb, backgroundColor, backgroundFogValue);
        vec3 foggedColor = mix(color.rgb, targetColor, fogValue);
        vec3 foggedFadedColor = mix(backgroundColor, foggedColor, ChunkVisibility);
        return vec4(foggedFadedColor, color.a);
    }

    return vec4(mix(color.rgb, FogColor.rgb, fogValue), color.a);
}

void main() {
    vec4 color = (UseRgss == 1 ? sampleRGSS(Sampler0, texCoord0, 1.0f / TextureSize) : sampleNearest(Sampler0, texCoord0, 1.0f / TextureSize)) * vertexColor;
#ifdef ALPHA_CUTOUT
    if (color.a < ALPHA_CUTOUT) {
        discard;
    }
#endif
    fragColor = calculateFinalColor(color);
}
