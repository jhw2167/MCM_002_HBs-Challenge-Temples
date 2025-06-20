package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.core.ChallengeRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

    @Inject(method = "placeInWorld", at = @At("HEAD"))
    private void onPlaceInWorld(
        ServerLevelAccessor world,
        BlockPos pos,
        BlockPos offset,
        StructurePlaceSettings settings,
        RandomSource random,
        int flags,
        CallbackInfoReturnable<Boolean> cir
    ) {
        //ChallengeRoom.placeInWorld(world, pos, offset, settings, random, flags);
    }
}
