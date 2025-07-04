package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.externalapi.FabricInventoryApi;
import eu.pb4.graves.grave.Grave;
import eu.pb4.graves.grave.GraveManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Grave.class, remap = false)
public class GraveMixin {

    @Inject(
        method = "shouldNaturallyBreak",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onShouldNaturallyBreak(CallbackInfoReturnable<Boolean> cir) {
        Grave grave = (Grave)(Object)this;
        //System.out.println("Checking if grave should naturally break: " + FabricInventoryApi.getInstance().hasGrave(grave));
        if( FabricInventoryApi.getInstance().hasGrave(grave) ) {
            cir.setReturnValue(false);
        }
    }
}


