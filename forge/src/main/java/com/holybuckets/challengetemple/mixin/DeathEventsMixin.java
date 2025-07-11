package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.externalapi.ForgeInventoryApi;
import de.maxhenkel.corpse.corelib.death.PlayerDeathEvent;
import de.maxhenkel.corpse.events.DeathEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DeathEvents.class, remap = false)
public abstract class DeathEventsMixin {

    @Inject(method = "playerDeath", at = @At("HEAD"), cancellable = true)
    private void cancelCorpse(PlayerDeathEvent event, CallbackInfo ci) {
        if (ForgeInventoryApi.shouldCancelCorpseSpawn(event.getPlayer())) {
            ci.cancel();
        }
    }
}

