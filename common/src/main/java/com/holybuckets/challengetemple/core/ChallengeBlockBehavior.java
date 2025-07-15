package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.block.ChallengeLog;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class ChallengeBlockBehavior {


    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted(ChallengeBlockBehavior::initSpecialProperties);
    }

    public static float MINEABLE_SPEED = 10f;
    public static float UNMINEABLE_SPEED = -1000f;

    public static Set<Block> CHALLENGE_MINEABLE;
    public static Set<Block> CHALLENGE_UNMINEABLE;
    public static void initSpecialProperties(ServerStartingEvent e)
    {
        CHALLENGE_MINEABLE = new HashSet<>();
        CHALLENGE_MINEABLE.add(ModBlocks.challengeCobble);

        CHALLENGE_UNMINEABLE = new HashSet<>();
        CHALLENGE_UNMINEABLE.add(ModBlocks.challengeLog);
        CHALLENGE_UNMINEABLE.add(Blocks.PISTON);
        CHALLENGE_UNMINEABLE.add(Blocks.STICKY_PISTON);

        CHALLENGE_UNMINEABLE.add( Blocks.CHEST );
        CHALLENGE_UNMINEABLE.add( ModBlocks.challengeCobble );  //checks mineable blocks first


        //Flammable blocks
        //ModBlocks.challengeLog.
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
            MobEffectInstance mei = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 4, false, false, false);
            p.addEffect( mei );
            return UNMINEABLE_SPEED;
        }

        return originalSpeed;
    }



}

