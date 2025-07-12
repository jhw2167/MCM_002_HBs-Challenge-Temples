package com.holybuckets.challengetemple.externalapi;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface PortalApi {

    enum Direction { NORTH, SOUTH, EAST, WEST, DOWN, UP }

    static int PORTAL_COOLDOWN = 5000;

    Entity createPortal(double width, double height, Level fromLevel, Level toLevel,
                        Vec3 sourcePos, Vec3 destPos, Direction dir);

    boolean isPortal(Entity entity);

    default boolean deletePortal(Entity entity) {
        entity.discard();
        return true;
    }
}
