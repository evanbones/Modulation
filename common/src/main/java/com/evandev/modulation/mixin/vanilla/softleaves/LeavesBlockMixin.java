package com.evandev.modulation.mixin.vanilla.softleaves;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.PassableFoliageModule;
import com.evandev.modulation.modules.vanillabackport.EntityLeafDrag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends Block {

    protected LeavesBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
        if (module != null && module.isPassableFoliageEnabled()) {
            if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() == null) {
                return super.getCollisionShape(state, level, pos, context);
            }
            return Shapes.empty();
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
        if (module != null && module.isPassableFoliageEnabled()) {
            if (entity instanceof EntityLeafDrag drag) {
                drag.modulation$applyLeafDrag(state, level, pos);
            }
        }
    }
}
