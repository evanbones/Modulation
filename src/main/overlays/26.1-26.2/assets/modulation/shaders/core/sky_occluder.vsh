#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

out vec3 viewDirection;

#ifdef MODULATION_REVERSED_Z
const float NEAR_PLANE_DEPTH = 1.0;
const float FAR_PLANE_DEPTH = 0.0;
#else
const float NEAR_PLANE_DEPTH = -1.0;
const float FAR_PLANE_DEPTH = 1.0;
#endif

vec3 unproject(mat4 inverseProjection, vec4 position) {
    vec4 unprojected = inverseProjection * position;
    return unprojected.xyz / unprojected.w;
}

void main() {
    vec2 position = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2) * 2.0 - 1.0;

    gl_Position = vec4(position, 0.0, 1.0);

    mat4 inverseProjection = inverse(ProjMat);
    viewDirection = unproject(inverseProjection, vec4(position, FAR_PLANE_DEPTH, 1.0)) - unproject(inverseProjection, vec4(position, NEAR_PLANE_DEPTH, 1.0));
}
