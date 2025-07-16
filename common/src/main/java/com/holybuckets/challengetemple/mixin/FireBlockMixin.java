package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.core.ChallengeBlockBehavior;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Map;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void onBootstrap(CallbackInfo ci) {
        FireBlock fireBlock = (FireBlock) Block.byItem(net.minecraft.world.item.Items.FIRE.asItem());
        Map<Block, Pair<Integer, Integer>> flammableBlocks = ChallengeBlockBehavior.getFlammable();
        
        for (Map.Entry<Block, Pair<Integer, Integer>> entry : flammableBlocks.entrySet()) {
            fireBlock.setFlammable(entry.getKey(), entry.getValue().getLeft(), entry.getValue().getRight());
        }
    }
}
