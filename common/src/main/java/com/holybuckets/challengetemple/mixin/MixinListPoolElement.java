package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.structure.StructurePieceSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ListPoolElement.class)
public abstract class MixinListPoolElement {

    private static final ThreadLocal<Boolean> suppress = ThreadLocal.withInitial(() -> false);

    @Inject(
        method = "getBoundingBox",
        at = @At("HEAD"),
        cancellable = true
    )
    private void injectGetBoundingBox(
        StructureTemplateManager manager,
        BlockPos pos,
        Rotation rotation,
        CallbackInfoReturnable<BoundingBox> cir
    ) {
        if (suppress.get()) return;
        try {
            suppress.set(true);
            Rotation r = StructurePieceSettings.checkApplyCustomSettings(manager)
                ? Rotation.NONE
                : rotation;
            BoundingBox result = ((SinglePoolElement) (Object) this)
                .getBoundingBox(manager, pos, r);
            cir.setReturnValue(result);
        } finally {
            suppress.set(false);
        }
    }


}

