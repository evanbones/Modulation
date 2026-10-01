uniform vec2 ModulationFogRange;
uniform vec2 ModulationRenderFogRange;
uniform int ModulationFogShape;
uniform float ModulationSkyVisibility;
uniform float ModulationSkyHidden;
vec4 modulation_sample_depth(vec2 uv);
float modulation_skyness(float d);
float modulation_sky_coverage(float d);
