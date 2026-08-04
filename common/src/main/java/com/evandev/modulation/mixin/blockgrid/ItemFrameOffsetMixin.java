package com.evandev.modulation.mixin.blockgrid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.blockgrid.BlockGridModule;
import com.evandev.modulation.modules.blockgrid.PackedSurfaceOffset;
import com.evandev.modulation.modules.blockgrid.storage.SurfaceOffsetHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrame.class)
public abstract class ItemFrameOffsetMixin extends HangingEntity implements SurfaceOffsetHolder {
    @Unique
    private static final EntityDataAccessor<Vector3f> MODULATION$SURFACE_OFFSET = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.VECTOR3);

    protected ItemFrameOffsetMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Vector3f modulation$getSurfaceOffset() {
        return getEntityData().get(MODULATION$SURFACE_OFFSET);
    }

    @Override
    public void modulation$setSurfaceOffset(Vector3f offset) {
        getEntityData().set(MODULATION$SURFACE_OFFSET, offset);
        setDirection(getDirection());
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void modulation$registerSurfaceOffsetData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(MODULATION$SURFACE_OFFSET, new Vector3f());
    }

    @ModifyReturnValue(method = "calculateBoundingBox", at = @At("RETURN"))
    private AABB modulation$offsetBoundingBox(AABB original, BlockPos pos, Direction direction) {
        BlockGridModule module = ModuleManager.getModule("block_grid", BlockGridModule.class);
        if (module == null || !module.isEnableBlockOffsets() || !module.isEnableHangingEntityOffsets()) {
            return original;
        }
        Vector3f offset = getEntityData().get(MODULATION$SURFACE_OFFSET);
        return (offset.x == 0.0F && offset.y == 0.0F && offset.z == 0.0F) ? original : original.move(offset.x, offset.y, offset.z);
    }

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    private void modulation$reapplyDirectionOnSync(EntityDataAccessor<?> key, CallbackInfo ci) {
        if (MODULATION$SURFACE_OFFSET.equals(key) && getDirection() != null) {
            setDirection(getDirection());
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void modulation$writeSurfaceOffset(CompoundTag tag, CallbackInfo ci) {
        Vector3f offset = getEntityData().get(MODULATION$SURFACE_OFFSET);
        if (offset.x != 0.0F || offset.y != 0.0F || offset.z != 0.0F) {
            tag.putFloat("ModulationSurfaceOffsetX", offset.x);
            tag.putFloat("ModulationSurfaceOffsetY", offset.y);
            tag.putFloat("ModulationSurfaceOffsetZ", offset.z);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void modulation$readSurfaceOffset(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ModulationSurfaceOffsetX")) {
            getEntityData().set(MODULATION$SURFACE_OFFSET, new Vector3f(
                    tag.getFloat("ModulationSurfaceOffsetX"),
                    tag.getFloat("ModulationSurfaceOffsetY"),
                    tag.getFloat("ModulationSurfaceOffsetZ")
            ));
            setDirection(getDirection());
        }
    }

    @ModifyExpressionValue(method = "getAddEntityPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Direction;get3DDataValue()I"))
    private int modulation$encodeOffsetInSpawnData(int direction) {
        return PackedSurfaceOffset.pack(direction, getEntityData().get(MODULATION$SURFACE_OFFSET));
    }

    @ModifyExpressionValue(method = "recreateFromPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;getData()I"))
    private int modulation$decodeOffsetFromSpawnData(int packed) {
        getEntityData().set(MODULATION$SURFACE_OFFSET, PackedSurfaceOffset.unpack(packed));
        return PackedSurfaceOffset.face(packed);
    }
}
