package com.holybuckets.challengetemple.mixin;

import com.holybuckets.foundation.event.BalmEventRegister;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.BalmRuntime;
import net.blay09.mods.balm.api.event.BalmEvent;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.PlayerChangedDimensionEvent;
import net.blay09.mods.balm.fabric.event.FabricBalmEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.teleportation.ServerTeleportationManager;

@Mixin(ServerTeleportationManager.class)
public class ServerTeleportationManagerMixin {

    @Inject(
        method = "changePlayerDimension",
        at = @At("HEAD")
    )
    private void onChangePlayerDimension(
        ServerPlayer player,
        ServerLevel fromWorld,
        ServerLevel toWorld,
        Vec3 newEyePos,
        CallbackInfo ci
    ) {
        System.out.println("TP!: ");
        if (player instanceof ServerPlayer)
        {
            ResourceKey<Level> fromDim = fromWorld.dimension();
            ResourceKey<Level> toDim = toWorld.dimension();

            if (!fromDim.equals(toDim)) {
                BalmEvents events = Balm.getEvents();
                PlayerChangedDimensionEvent event = new PlayerChangedDimensionEvent(
                    player, fromDim, toDim
                );
                events.fireEvent(event);
            }
        }
    }
}


