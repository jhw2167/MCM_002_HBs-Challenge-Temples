package com.holybuckets.challengetemple.mixin;

import de.maxhenkel.corpse.entities.CorpseEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CorpseEntity.class)
public class CorpseEntityMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(RemovalReason reason, CallbackInfo ci) {
        //System.out.println("[CorpseEntityMixin] REMOVE called! Reason: " + reason);
        // Or use a logger:
        // LOGGER.info("CorpseEntity.remove() called: " + reason);
    }

}
