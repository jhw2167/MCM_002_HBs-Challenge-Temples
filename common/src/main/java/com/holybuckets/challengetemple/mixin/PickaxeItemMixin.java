package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.block.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin {

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void onGetDestroySpeed(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        float returnedSpeed = cir.getReturnValue();
        if (ModBlocks.MINEABLE.contains(state) && returnedSpeed > 1.0f ) {
            cir.setReturnValue(ModBlocks.MINEABLE_SPEED);
        }
    }

}
