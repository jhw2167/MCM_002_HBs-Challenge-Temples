package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.challengetemple.block.ChallengeBuildingBlock;
import com.holybuckets.challengetemple.block.ChallengeLog;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.event.UseBlockEvent;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

public class ChallengeBlockBehavior {

    public static final String CLASS_ID = "025";

    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted(ChallengeBlockBehavior::initSpecialProperties);
        reg.registerOnUseBlock(ChallengeBlockBehavior::onUseBlock);
    }

    public static float MINEABLE_SPEED = 10f;
    public static float UNMINEABLE_SPEED = -1000f;

    public static Set<Block> CHALLENGE_MINEABLE;
    public static Set<Block> CHALLENGE_UNMINEABLE;
    public static void initSpecialProperties(ServerStartingEvent e)
    {
        CHALLENGE_MINEABLE = new HashSet<>();
        CHALLENGE_MINEABLE.add(ModBlocks.challengeCobble);
        // Add all building blocks as mineable
        ModBlocks.BUILDING_BLOCKS.values().forEach(CHALLENGE_MINEABLE::add);

        CHALLENGE_UNMINEABLE = new HashSet<>();
        CHALLENGE_UNMINEABLE.add(ModBlocks.challengeLog);
        CHALLENGE_UNMINEABLE.add(Blocks.PISTON);
        CHALLENGE_UNMINEABLE.add(Blocks.STICKY_PISTON);

        CHALLENGE_UNMINEABLE.add( Blocks.CHEST );
        CHALLENGE_UNMINEABLE.add( ModBlocks.challengeCobble );  //checks mineable blocks first

        setFlammable();
    }

    /**
     *
     * @return Map<Block, Pair<ignitChance, burnTime>>
     */
    public static Map<Block, Pair<Integer, Integer>> getFlammable() {
        Map<Block, Pair<Integer, Integer>> flammableBlocks = new HashMap<>();
        //IGNITE CHANCE - 60 -> 0, burn time: 100 -> 0
        flammableBlocks.put(ModBlocks.challengeLog, Pair.of(25, 5));

        return flammableBlocks;
    }

    public static void setFlammable() {

        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        /*
            This method is private, so use reflection to set flammable blocks
             private void setFlammable(Block block, int i, int j) {
                this.igniteOdds.put(block, i);
                this.burnOdds.put(block, j);
            }
         */
        Map<Block, Pair<Integer, Integer>> blocks = getFlammable();
        // Set the flammable blocks
        try {
            Method setFlammable = FireBlock.class.getDeclaredMethod("setFlammable", Block.class, int.class, int.class);
            setFlammable.setAccessible(true);
            for(Map.Entry<Block, Pair<Integer, Integer>> entry : blocks.entrySet())
            {
                Block block = entry.getKey();
                Pair<Integer, Integer> odds = entry.getValue();
                setFlammable.invoke(fireBlock, block, odds.getLeft(), odds.getRight());
            }
        } catch (Exception e) {
            LoggerProject.logError("025001", "Failed to set flammable blocks using reflection, error: "
            + e.getMessage());
        }


    }

    //on inventory getDestroySpeed mixin
    public static float onPlayerDestroyBlockSpeed(float originalSpeed, BlockState state, Player p)
    {
        if(!ManagedChallenger.isActiveChallenger(p)) return originalSpeed;

        if (CHALLENGE_MINEABLE.contains(state.getBlock())) {
            if (originalSpeed > 1.0f) {
                return MINEABLE_SPEED;
            }
        }

        if (CHALLENGE_UNMINEABLE.contains(state.getBlock())) {
            MobEffectInstance mei = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5, 3, false, false, false);
            p.addEffect( mei );
            return UNMINEABLE_SPEED;
        }

        return originalSpeed;
    }

    /** if its a building block, change color
     * @param e UseBlockEvent
     */
    public static void onUseBlock(UseBlockEvent e) {
        Level level = e.getLevel();
        BlockState state = level.getBlockState(  e.getHitResult().getBlockPos() );
        if (state.getBlock() instanceof ChallengeBuildingBlock)
        {
            ItemStack heldItem = e.getPlayer().getMainHandItem();
            if(heldItem.isEmpty()) {
                ChallengeBuildingBlock.changeColor(level, e.getHitResult().getBlockPos(), state);
                e.setCanceled(true);
            }
        }
    }


}

