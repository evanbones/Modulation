uniform float ModulationImprovedFog;
uniform vec2 ModulationRenderFog;
uniform sampler2D ModulationSkySampler;
uniform sampler2D ModulationCloudsSampler;
uniform sampler2D ModulationCloudsDepthSampler;
vec4 modulation_improved_fog(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor);
