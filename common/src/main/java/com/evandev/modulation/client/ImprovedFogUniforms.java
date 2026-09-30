package com.evandev.modulation.client;

import com.mojang.blaze3d.shaders.Uniform;
import org.lwjgl.opengl.GL20;

public final class ImprovedFogUniforms {

    private boolean resolved;
    private int enabledLocation = -1;
    private int rangeLocation = -1;
    private int skyLocation = -1;
    private int cloudsLocation = -1;
    private int cloudsDepthLocation = -1;

    public void upload(int program) {
        if (!this.resolved) {
            this.enabledLocation = Uniform.glGetUniformLocation(program, "ModulationImprovedFog");
            this.rangeLocation = Uniform.glGetUniformLocation(program, "ModulationRenderFog");
            this.skyLocation = Uniform.glGetUniformLocation(program, "ModulationSkySampler");
            this.cloudsLocation = Uniform.glGetUniformLocation(program, "ModulationCloudsSampler");
            this.cloudsDepthLocation = Uniform.glGetUniformLocation(program, "ModulationCloudsDepthSampler");
            this.resolved = true;
        }

        if (this.enabledLocation < 0) {
            return;
        }

        boolean active = ImprovedFog.isActive();
        GL20.glUniform1f(this.enabledLocation, active ? 1.0F : 0.0F);
        if (!active) {
            return;
        }

        if (this.rangeLocation >= 0) {
            GL20.glUniform2f(this.rangeLocation, ImprovedFog.renderFogStart(), ImprovedFog.renderFogEnd());
        }
        if (this.skyLocation >= 0) {
            Uniform.uploadInteger(this.skyLocation, ImprovedFog.SKY_UNIT);
        }
        if (this.cloudsLocation >= 0) {
            Uniform.uploadInteger(this.cloudsLocation, ImprovedFog.CLOUDS_UNIT);
        }
        if (this.cloudsDepthLocation >= 0) {
            Uniform.uploadInteger(this.cloudsDepthLocation, ImprovedFog.CLOUDS_DEPTH_UNIT);
        }
        ImprovedFog.bindBackgroundTextures();
    }
}
