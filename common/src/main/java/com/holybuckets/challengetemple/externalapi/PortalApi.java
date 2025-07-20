package com.holybuckets.challengetemple.externalapi;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.minecraft.core.Direction;

public interface PortalApi {


    static int PORTAL_COOLDOWN = 5000;

    /**
     *
     * 1. Update APIs with new direction tag
     * 2. Try shitty portal algorithm and watch it fail
     * 3. Check GUI for challengeChest
     * 4. Fix bug where items don't save in chest
     * 5. Make sure ChallengeKeyBlocks updates all redstone blocks
     * @param width
     * @param height
     * @param fromLevel
     * @param toLevel
     * @param sourcePos
     * @param destPos
     * @param dir
     * @return
     */

    Entity createPortal(double width, double height, Level fromLevel, Level toLevel,
                        Vec3 sourcePos, Vec3 destPos, Direction dir);

    boolean isPortal(Entity entity);

    default boolean deletePortal(Entity entity) {
        entity.discard();
        return true;
    }

    boolean removePortal(Entity entity);
}
