package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.core.ChallengeBlockBehavior;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    protected void onGetDestroySpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
      cir.setReturnValue(ChallengeBlockBehavior
      .onPlayerDestroyBlockSpeed(cir.getReturnValue(), state, ((Inventory)(Object)this).player));
    }

}
