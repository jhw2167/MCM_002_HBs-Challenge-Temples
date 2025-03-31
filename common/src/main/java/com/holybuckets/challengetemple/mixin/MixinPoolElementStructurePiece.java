package com.holybuckets.challengetemple.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.holybuckets.challengetemple.structure.StructurePieceSettings;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PoolElementStructurePiece.class)
public abstract class MixinPoolElementStructurePiece {

    @Shadow
    StructureTemplateManager structureTemplateManager;

    @Inject(
        method = "getRotation",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onGetRotation(CallbackInfoReturnable<Rotation> cir) {
        if (StructurePieceSettings.checkApplyCustomSettings(this.structureTemplateManager)) {
            cir.setReturnValue(Rotation.NONE);
        }
    }

    @Inject(
        method = "place",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onPlace(
        WorldGenLevel worldGenLevel,
        StructureManager structureManager,
        ChunkGenerator chunkGenerator,
        RandomSource randomSource,
        BoundingBox boundingBox,
        BlockPos blockPos,
        boolean bl,
        CallbackInfo ci
    ) {
        PoolElementStructurePiece self = (PoolElementStructurePiece) (Object) this;
        if( StructurePieceSettings.checkApplyCustomSettings(this.structureTemplateManager) )
        {
            boolean success = self.getElement().place(
                this.structureTemplateManager,
                worldGenLevel,
                structureManager,
                chunkGenerator,
                self.getPosition(),
                blockPos,
                Rotation.NONE,
                boundingBox,
                randomSource,
                bl
            );

            // Cancel the original method since we’ve already placed manually
            ci.cancel();
        }

    }
}

