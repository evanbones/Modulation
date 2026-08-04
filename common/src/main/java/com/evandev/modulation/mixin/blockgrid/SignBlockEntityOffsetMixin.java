package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.modules.blockgrid.ClientOffsetCache;
import com.evandev.modulation.modules.blockgrid.storage.SignOffsetHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityOffsetMixin extends BlockEntity implements SignOffsetHolder {
    @Unique
    private static final String modulation$KEY_X = "ModulationSignOffsetX";
    @Unique
    private static final String modulation$KEY_Y = "ModulationSignOffsetY";
    @Unique
    private static final String modulation$KEY_Z = "ModulationSignOffsetZ";

    @Unique
    private Vec3 modulation$signOffset = Vec3.ZERO;

    public SignBlockEntityOffsetMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Vec3 modulation$getSignOffset() {
        return this.modulation$signOffset;
    }

    @Override
    public void modulation$setSignOffset(Vec3 offset) {
        this.modulation$signOffset = offset;
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void modulation$writeSignOffset(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.modulation$signOffset != Vec3.ZERO) {
            tag.putDouble(modulation$KEY_X, this.modulation$signOffset.x);
            tag.putDouble(modulation$KEY_Y, this.modulation$signOffset.y);
            tag.putDouble(modulation$KEY_Z, this.modulation$signOffset.z);
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void modulation$readSignOffset(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        Vec3 stored = tag.contains(modulation$KEY_X)
                ? new Vec3(tag.getDouble(modulation$KEY_X), tag.getDouble(modulation$KEY_Y), tag.getDouble(modulation$KEY_Z))
                : Vec3.ZERO;
        boolean moved = !stored.equals(this.modulation$signOffset);
        this.modulation$signOffset = stored;
        if (moved && this.level != null && this.level.isClientSide) {
            ClientOffsetCache.refreshSection(this.level, getBlockPos());
        }
    }
}
