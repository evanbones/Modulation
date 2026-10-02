#version 150

uniform sampler2D ModulationCloudsSampler;
uniform sampler2D ModulationCloudsDepthSampler;

out vec4 fragColor;

void main() {
    ivec2 pixelCoords = ivec2(gl_FragCoord.xy);
    vec4 color = texelFetch(ModulationCloudsSampler, pixelCoords, 0);
    if (color.a <= 0.0) {
        discard;
    }

    gl_FragDepth = texelFetch(ModulationCloudsDepthSampler, pixelCoords, 0).r;
    fragColor = color;
}
