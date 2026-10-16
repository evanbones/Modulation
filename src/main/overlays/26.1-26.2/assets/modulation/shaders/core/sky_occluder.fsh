#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

in vec3 viewDirection;

out vec4 fragColor;

void main() {
    vec3 worldDirection = normalize(transpose(mat3(ModelViewMat)) * viewDirection);
    float fragmentAngle = degrees(asin(clamp(worldDirection.y, -1.0, 1.0)));
    float alpha = smoothstep(ModelOffset.x, ModelOffset.y, fragmentAngle);
    fragColor = vec4(FogColor.rgb, alpha);
}
