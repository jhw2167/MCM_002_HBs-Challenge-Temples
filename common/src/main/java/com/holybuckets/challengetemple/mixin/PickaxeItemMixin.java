package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.block.ModBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PickaxeItem.class)
public abstract class PickaxeItemMixin implements ISpeedModifier {

    //@Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void onGetDestroySpeed(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(modifySpeed(cir.getReturnValue(), state));
    }
}
