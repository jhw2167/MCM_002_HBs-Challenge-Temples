package com.holybuckets.challengetemple.menu;

import com.holybuckets.challengetemple.block.ChallengeChestCountingBlock;
import com.holybuckets.challengetemple.block.be.ChallengeChestCountingBlockEntity;
import net.blay09.mods.balm.api.DeferredObject;
import com.holybuckets.challengetemple.Constants;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ModMenus {

    public static DeferredObject<MenuType<ChallengeChestCountingMenu>> countingChestMenu;


    public static void initialize(BalmMenus menus)
    {
        countingChestMenu = menus.registerMenu(id("counting_chest_menu"),
            (syncId, inventory, buf) -> {
                BlockPos pos = buf.readBlockPos();
                Level level = inventory.player.level();
                BlockEntity be = inventory.player.level().getBlockEntity(pos);
                if( be instanceof ChallengeChestCountingBlockEntity) {
                    ChallengeChestCountingBlockEntity cbe = (ChallengeChestCountingBlockEntity) be;
                    cbe.setLevel(level);
                    return new ChallengeChestCountingMenu(syncId, inventory, cbe);
                }
                return null;
            });
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}


