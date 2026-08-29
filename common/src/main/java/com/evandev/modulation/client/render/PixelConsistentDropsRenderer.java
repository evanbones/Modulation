package com.evandev.modulation.client.render;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.vanilla.accessor.MinecraftAccessor;
import com.evandev.modulation.modules.vanilla.VanillaVisualModule;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.lwjgl.opengl.ARBCopyImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.WeakHashMap;

public class PixelConsistentDropsRenderer {

    private static final Map<Block, Boolean> SAFE_BLOCK_CACHE = new WeakHashMap<>();
    private static final ResourceLocation MIPPED_BLOCKS_LOCATION = ResourceLocation.fromNamespaceAndPath("modulation", "textures/atlas/blocks-mip.png");
    public static boolean mippedBlocksInvalid = true;
    private static AbstractTexture mippedBlocks;

    public static boolean isSafe(Block block) {
        return SAFE_BLOCK_CACHE.computeIfAbsent(block, b -> {
            String path = BuiltInRegistries.BLOCK.getKey(b).getPath();
            if (path.endsWith("_glazed_terracotta")) return false;
            return path.endsWith("_cobblestone") || path.startsWith("cobblestone")
                    || path.endsWith("_planks") || path.endsWith("_nylium")
                    || path.endsWith("_log") || path.endsWith("_wood")
                    || path.endsWith("_stem") || path.endsWith("_hyphae")
                    || path.endsWith("_ore") || path.endsWith("_sand")
                    || path.endsWith("_gravel") || path.endsWith("_wool")
                    || path.endsWith("_terracotta") || path.endsWith("_sandstone")
                    || path.endsWith("_concrete") || path.endsWith("_concrete_powder")
                    || path.endsWith("_leaves") || path.endsWith("_carpet")
                    || path.endsWith("_slab") || path.endsWith("_stairs")
                    || path.endsWith("_block") || path.endsWith("_bricks")
                    || path.endsWith("_tiles") || path.endsWith("_planks_slab")
                    || path.equals("stone") || path.equals("granite") || path.equals("diorite")
                    || path.equals("andesite") || path.equals("grass_block") || path.equals("dirt")
                    || path.equals("coarse_dirt") || path.equals("podzol") || path.equals("cobblestone")
                    || path.equals("bedrock") || path.equals("sand") || path.equals("gravel")
                    || path.equals("obsidian") || path.equals("ice") || path.equals("snow_block")
                    || path.equals("snow") || path.equals("clay") || path.equals("netherrack")
                    || path.equals("soul_sand") || path.equals("soul_soil") || path.equals("basalt")
                    || path.equals("glowstone") || path.equals("mycelium") || path.equals("end_stone")
                    || path.equals("magma_block") || path.equals("packed_ice") || path.equals("blue_ice")
                    || path.equals("deepslate") || path.equals("tuff") || path.equals("calcite")
                    || path.equals("dripstone_block") || path.equals("mud") || path.equals("packed_mud")
                    || path.equals("crying_obsidian") || path.equals("ancient_debris");
        });
    }

    public static boolean renderPixelConsistentBlockDrop(
            ItemStack stack,
            BakedModel model,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        if (!ModuleManager.isEnabled("vanilla_visual", VanillaVisualModule.class, VanillaVisualModule::isPixelConsistentBlockDropsEnabled)) {
            return false;
        }

        if (!(stack.getItem() instanceof BlockItem blockItem) || !model.isGui3d() || model.isCustomRenderer()) {
            return false;
        }

        poseStack.pushPose();
        model.getTransforms().getTransform(ItemDisplayContext.GROUND).apply(false, poseStack);
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        Block block = blockItem.getBlock();
        if (isSafe(block)) {
            RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, true);
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, stack.hasFoil());

            RandomSource random = RandomSource.create();
            long seed = 42L;
            for (Direction direction : Direction.values()) {
                random.setSeed(seed);
                for (BakedQuad quad : model.getQuads(null, direction, random)) {
                    drawExaggeratedQuad(stack, poseStack, vertexConsumer, quad, packedLight, packedOverlay);
                }
            }
            random.setSeed(seed);
            for (BakedQuad quad : model.getQuads(null, null, random)) {
                drawExaggeratedQuad(stack, poseStack, vertexConsumer, quad, packedLight, packedOverlay);
            }
        } else {
            ensureMippedBlocksTexture();
            RenderType defLayer = ItemBlockRenderTypes.getRenderType(stack, true);
            RenderType layer = defLayer == Sheets.cutoutBlockSheet()
                    ? RenderType.entityCutout(MIPPED_BLOCKS_LOCATION)
                    : RenderType.entityTranslucent(MIPPED_BLOCKS_LOCATION);
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(bufferSource, layer, true, stack.hasFoil());
            renderBakedModelQuads(model, stack, packedLight, packedOverlay, poseStack, vertexConsumer);
        }

        poseStack.popPose();
        return true;
    }

    private static void ensureMippedBlocksTexture() {
        if (mippedBlocks == null || mippedBlocksInvalid) {
            mippedBlocksInvalid = false;
            mippedBlocks = new AbstractTexture() {
                @Override
                public void load(ResourceManager manager) {
                    releaseId();
                    TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
                    RenderSystem.bindTexture(atlas.getId());
                    int maxLevel = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL);
                    if (maxLevel == 0 || !GL.getCapabilities().GL_ARB_copy_image) {
                        int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                        int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
                        try (NativeImage img = new NativeImage(NativeImage.Format.RGBA, w, h, false)) {
                            img.downloadTexture(0, false);
                            try (NativeImage mipped = MipmapGenerator.generateMipLevels(new NativeImage[]{img}, 1)[1]) {
                                TextureUtil.prepareImage(getId(), mipped.getWidth(), mipped.getHeight());
                                RenderSystem.bindTexture(getId());
                                mipped.upload(0, 0, 0, true);
                            }
                        }
                    } else {
                        int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 1, GL11.GL_TEXTURE_WIDTH);
                        int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 1, GL11.GL_TEXTURE_HEIGHT);
                        TextureUtil.prepareImage(getId(), w, h);
                        ARBCopyImage.glCopyImageSubData(
                                atlas.getId(), GL11.GL_TEXTURE_2D, 1, 0, 0, 0,
                                getId(), GL11.GL_TEXTURE_2D, 0, 0, 0, 0,
                                w, h, 1
                        );
                    }
                }
            };
            Minecraft.getInstance().getTextureManager().register(MIPPED_BLOCKS_LOCATION, mippedBlocks);
        }
    }

    private static void renderBakedModelQuads(
            BakedModel model,
            ItemStack stack,
            int packedLight,
            int packedOverlay,
            PoseStack poseStack,
            VertexConsumer vertexConsumer
    ) {
        RandomSource random = RandomSource.create();
        long seed = 42L;
        PoseStack.Pose pose = poseStack.last();
        for (Direction direction : Direction.values()) {
            random.setSeed(seed);
            for (BakedQuad quad : model.getQuads(null, direction, random)) {
                int packedColor = -1;
                if (quad.isTinted()) {
                    packedColor = ((MinecraftAccessor) Minecraft.getInstance()).modulation$getItemColors().getColor(stack, quad.getTintIndex());
                }
                float tintR = (float) FastColor.ARGB32.red(packedColor) / 255.0F;
                float tintG = (float) FastColor.ARGB32.green(packedColor) / 255.0F;
                float tintB = (float) FastColor.ARGB32.blue(packedColor) / 255.0F;
                float tintA = (float) FastColor.ARGB32.alpha(packedColor) / 255.0F;
                vertexConsumer.putBulkData(pose, quad, tintR, tintG, tintB, tintA, packedLight, packedOverlay);
            }
        }
        random.setSeed(seed);
        for (BakedQuad quad : model.getQuads(null, null, random)) {
            int packedColor = -1;
            if (quad.isTinted()) {
                packedColor = ((MinecraftAccessor) Minecraft.getInstance()).modulation$getItemColors().getColor(stack, quad.getTintIndex());
            }
            float tintR = (float) FastColor.ARGB32.red(packedColor) / 255.0F;
            float tintG = (float) FastColor.ARGB32.green(packedColor) / 255.0F;
            float tintB = (float) FastColor.ARGB32.blue(packedColor) / 255.0F;
            float tintA = (float) FastColor.ARGB32.alpha(packedColor) / 255.0F;
            vertexConsumer.putBulkData(pose, quad, tintR, tintG, tintB, tintA, packedLight, packedOverlay);
        }
    }

    private static void drawExaggeratedQuad(
            ItemStack stack,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            BakedQuad quad,
            int packedLight,
            int packedOverlay
    ) {
        int packedColor = -1;
        if (quad.isTinted()) {
            packedColor = ((MinecraftAccessor) Minecraft.getInstance()).modulation$getItemColors().getColor(stack, quad.getTintIndex());
        }

        float tintR = (float) FastColor.ARGB32.red(packedColor) / 255.0F;
        float tintG = (float) FastColor.ARGB32.green(packedColor) / 255.0F;
        float tintB = (float) FastColor.ARGB32.blue(packedColor) / 255.0F;
        float tintA = (float) FastColor.ARGB32.alpha(packedColor) / 255.0F;

        boolean isProbablyGrass = false;
        if (stack.getItem() instanceof BlockItem bi) {
            Block b = bi.getBlock();
            isProbablyGrass = b.defaultBlockState().getSoundType() == SoundType.GRASS || b.defaultBlockState().is(BlockTags.DIRT);
        }

        int[] aint = quad.getVertices();
        Vec3i vec3i = quad.getDirection().getNormal();
        PoseStack.Pose pose = poseStack.last();

        int vertexCount = aint.length / 8;
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            ByteBuffer byteBuffer = memoryStack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();

            float minU = Float.POSITIVE_INFINITY;
            float maxU = Float.NEGATIVE_INFINITY;
            float minV = Float.POSITIVE_INFINITY;
            float maxV = Float.NEGATIVE_INFINITY;

            for (int pass = 0; pass < 2; pass++) {
                for (int l = 0; l < vertexCount; l++) {
                    intBuffer.clear();
                    intBuffer.put(aint, l * 8, 8);
                    float u = byteBuffer.getFloat(16);
                    float v = byteBuffer.getFloat(20);

                    if (pass == 0) {
                        minU = Math.min(minU, u);
                        maxU = Math.max(maxU, u);
                        minV = Math.min(minV, v);
                        maxV = Math.max(maxV, v);
                    } else {
                        float x = byteBuffer.getFloat(0);
                        float y = byteBuffer.getFloat(4);
                        float z = byteBuffer.getFloat(8);

                        float r = ((float) (byteBuffer.get(12) & 255) / 255.0F) * tintR;
                        float g = ((float) (byteBuffer.get(13) & 255) / 255.0F) * tintG;
                        float b = ((float) (byteBuffer.get(14) & 255) / 255.0F) * tintB;
                        float a = ((float) (byteBuffer.get(15) & 255) / 255.0F) * tintA;

                        float uSize = maxU - minU;
                        float vSize = maxV - minV;

                        if (Math.abs(u - minU) < 0.0001F) {
                            u = minU + (uSize * 0.25F);
                        } else if (Math.abs(u - maxU) < 0.0001F) {
                            u = minU + (uSize * 0.75F);
                        }

                        if (Math.abs(v - minV) < 0.0001F) {
                            v = minV + (vSize * (isProbablyGrass ? (1.0F / 16.0F) : 0.25F));
                        } else if (Math.abs(v - maxV) < 0.0001F) {
                            v = minV + (vSize * (isProbablyGrass ? (9.0F / 16.0F) : 0.75F));
                        }

                        vertexConsumer.addVertex(pose, x, y, z)
                                .setColor((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F), (int) (a * 255.0F))
                                .setUv(u, v)
                                .setOverlay(packedOverlay)
                                .setLight(packedLight)
                                .setNormal(pose, (float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
                    }
                }
            }
        }
    }
}
