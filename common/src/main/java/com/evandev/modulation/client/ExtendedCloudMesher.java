package com.evandev.modulation.client;

import com.evandev.modulation.Constants;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.io.InputStream;

public final class ExtendedCloudMesher {

    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/environment/clouds.png");
    private static final CloudCells NO_CELLS = new CloudCells(new boolean[1], 1, 1);
    private static final int EMPTY_ALPHA = 10;
    private static final float CELL_SIZE = 12.0F;
    private static final float CLOUD_THICKNESS = 4.0F;
    private static final float ALPHA = 0.8F;

    private static volatile CloudCells cells;

    private ExtendedCloudMesher() {
    }

    public static void reload(ResourceManager resourceManager) {
        cells = load(resourceManager);
    }

    public static void ensureLoaded() {
        if (cells == null) {
            cells = load(Minecraft.getInstance().getResourceManager());
        }
    }

    public static int radiusCells(int rangeChunks) {
        return Mth.ceil(rangeChunks * 16 / CELL_SIZE);
    }

    public static MeshData build(Tesselator tesselator, double x, double y, double z, Vec3 color, CloudStatus status, int radiusCells) {
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        CloudCells data = cells;
        Mesh mesh = new Mesh(builder, (float) color.x, (float) color.y, (float) color.z, (float) Math.floor(y / CLOUD_THICKNESS) * CLOUD_THICKNESS);

        if (data != null) {
            int originX = Mth.floor(x);
            int originZ = Mth.floor(z);
            int radiusSqr = radiusCells * radiusCells;
            boolean fancy = status == CloudStatus.FANCY;

            for (int dz = -radiusCells; dz <= radiusCells; dz++) {
                for (int dx = -radiusCells; dx <= radiusCells; dx++) {
                    if (dx * dx + dz * dz > radiusSqr) {
                        continue;
                    }

                    int cellX = originX + dx;
                    int cellZ = originZ + dz;
                    if (!data.filled(cellX, cellZ)) {
                        continue;
                    }

                    mesh.cell(dx, dz, data.u(cellX), data.v(cellZ));
                    if (fancy) {
                        buildExtrudedCell(mesh, data, dx, dz, cellX, cellZ);
                    } else {
                        mesh.face(Direction.DOWN, 1.0F);
                    }
                }
            }
        }

        if (mesh.quads == 0) {
            mesh.degenerate();
        }
        return builder.buildOrThrow();
    }

    private static void buildExtrudedCell(Mesh mesh, CloudCells data, int dx, int dz, int cellX, int cellZ) {
        if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) {
            for (Direction direction : Direction.values()) {
                mesh.face(direction, shade(direction));
            }
            return;
        }

        if (mesh.baseY > -5.0F) {
            mesh.face(Direction.DOWN, shade(Direction.DOWN));
        }
        if (mesh.baseY <= 5.0F) {
            mesh.face(Direction.UP, shade(Direction.UP));
        }
        if (dx > 0 && !data.filled(cellX - 1, cellZ)) {
            mesh.face(Direction.WEST, shade(Direction.WEST));
        }
        if (dx < 0 && !data.filled(cellX + 1, cellZ)) {
            mesh.face(Direction.EAST, shade(Direction.EAST));
        }
        if (dz > 0 && !data.filled(cellX, cellZ - 1)) {
            mesh.face(Direction.NORTH, shade(Direction.NORTH));
        }
        if (dz < 0 && !data.filled(cellX, cellZ + 1)) {
            mesh.face(Direction.SOUTH, shade(Direction.SOUTH));
        }
    }

    private static float shade(Direction direction) {
        return switch (direction) {
            case DOWN -> 0.7F;
            case UP -> 1.0F;
            case NORTH, SOUTH -> 0.8F;
            case WEST, EAST -> 0.9F;
        };
    }

    private static CloudCells load(ResourceManager resourceManager) {
        try (InputStream stream = resourceManager.open(TEXTURE); NativeImage image = NativeImage.read(stream)) {
            int width = image.getWidth();
            int height = image.getHeight();
            boolean[] filled = new boolean[width * height];
            for (int z = 0; z < height; z++) {
                for (int x = 0; x < width; x++) {
                    filled[x + z * width] = (image.getPixelRGBA(x, z) >>> 24) >= EMPTY_ALPHA;
                }
            }
            return new CloudCells(filled, width, height);
        } catch (IOException | RuntimeException failure) {
            Constants.LOG.error("Failed to load cloud texture for extended clouds", failure);
            return NO_CELLS;
        }
    }

    private record CloudCells(boolean[] cells, int width, int height) {

        boolean filled(int x, int z) {
            return this.cells[Math.floorMod(x, this.width) + Math.floorMod(z, this.height) * this.width];
        }

        float u(int x) {
            return (Math.floorMod(x, this.width) + 0.5F) / this.width;
        }

        float v(int z) {
            return (Math.floorMod(z, this.height) + 0.5F) / this.height;
        }
    }

    private static final class Mesh {

        private final BufferBuilder builder;
        private final float red;
        private final float green;
        private final float blue;
        private final float baseY;
        private int quads;
        private float x0;
        private float z0;
        private float u;
        private float v;

        private Mesh(BufferBuilder builder, float red, float green, float blue, float baseY) {
            this.builder = builder;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.baseY = baseY;
        }

        private void cell(int x, int z, float u, float v) {
            this.x0 = x;
            this.z0 = z;
            this.u = u;
            this.v = v;
        }

        private void face(Direction direction, float shade) {
            float x1 = this.x0 + 1.0F;
            float z1 = this.z0 + 1.0F;
            float y0 = this.baseY;
            float y1 = this.baseY + CLOUD_THICKNESS;
            switch (direction) {
                case DOWN ->
                        this.quad(direction, shade, this.x0, y0, z1, x1, y0, z1, x1, y0, this.z0, this.x0, y0, this.z0);
                case UP ->
                        this.quad(direction, shade, this.x0, y1, z1, x1, y1, z1, x1, y1, this.z0, this.x0, y1, this.z0);
                case WEST ->
                        this.quad(direction, shade, this.x0, y0, z1, this.x0, y1, z1, this.x0, y1, this.z0, this.x0, y0, this.z0);
                case EAST -> this.quad(direction, shade, x1, y0, z1, x1, y1, z1, x1, y1, this.z0, x1, y0, this.z0);
                case NORTH ->
                        this.quad(direction, shade, this.x0, y1, this.z0, x1, y1, this.z0, x1, y0, this.z0, this.x0, y0, this.z0);
                case SOUTH -> this.quad(direction, shade, this.x0, y1, z1, x1, y1, z1, x1, y0, z1, this.x0, y0, z1);
            }
        }

        private void quad(Direction direction, float shade,
                          float ax, float ay, float az, float bx, float by, float bz,
                          float cx, float cy, float cz, float dx, float dy, float dz) {
            float r = this.red * shade;
            float g = this.green * shade;
            float b = this.blue * shade;
            float nx = direction.getStepX();
            float ny = direction.getStepY();
            float nz = direction.getStepZ();
            this.builder.addVertex(ax, ay, az).setUv(this.u, this.v).setColor(r, g, b, ALPHA).setNormal(nx, ny, nz);
            this.builder.addVertex(bx, by, bz).setUv(this.u, this.v).setColor(r, g, b, ALPHA).setNormal(nx, ny, nz);
            this.builder.addVertex(cx, cy, cz).setUv(this.u, this.v).setColor(r, g, b, ALPHA).setNormal(nx, ny, nz);
            this.builder.addVertex(dx, dy, dz).setUv(this.u, this.v).setColor(r, g, b, ALPHA).setNormal(nx, ny, nz);
            this.quads++;
        }

        private void degenerate() {
            for (int i = 0; i < 4; i++) {
                this.builder.addVertex(0.0F, 0.0F, 0.0F).setUv(0.0F, 0.0F).setColor(0.0F, 0.0F, 0.0F, 0.0F).setNormal(0.0F, 1.0F, 0.0F);
            }
        }
    }
}
