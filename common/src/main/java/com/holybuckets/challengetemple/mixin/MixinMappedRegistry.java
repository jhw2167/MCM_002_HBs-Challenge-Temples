package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.structure.GridStructurePlacement;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.core.MappedRegistry")
public class MixinMappedRegistry<T> {

    @Shadow
    private int nextId;


    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private void injectRegister(ResourceKey<T> key, T value, Lifecycle lifecycle, CallbackInfoReturnable<Holder.Reference<T>> cir) {
        if (key.registry().equals(Registries.STRUCTURE_SET.location()) && value instanceof StructureSet set)
        {
            System.out.println("Intercepted StructureSet registration: " + key.location());
            T newValue = (T) GridStructurePlacement.updatePlacementOnRegister(key, set);

            // manually invoke the original logic with modified value
            MappedRegistry<T> thisObj = (MappedRegistry<T>) (Object) this;
            Holder.Reference<T> ref = thisObj.registerMapping(this.nextId, key, newValue, lifecycle);
            cir.setReturnValue(ref);
        }
    }

}
