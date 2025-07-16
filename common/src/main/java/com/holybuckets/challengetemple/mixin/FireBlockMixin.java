package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.core.ChallengeBlockBehavior;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Map;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Shadow @Final @Mutable
    private Object2IntMap<Block> igniteOdds;
    @Shadow @Final @Mutable
    private Object2IntMap<Block> burnOdds;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hbs$afterConstructor(BlockBehaviour.Properties properties, CallbackInfo ci) {
        Map<Block, Pair<Integer, Integer>> flammableBlocks = ChallengeBlockBehavior.getFlammable();
        for (Map.Entry<Block, Pair<Integer, Integer>> entry : flammableBlocks.entrySet()) {
            Pair p = entry.getValue();
            igniteOdds.put(entry.getKey(), (Integer) p.getLeft());
            burnOdds.put(entry.getKey(), (Integer) p.getRight());
        }
    }
}
