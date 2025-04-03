package com.holybuckets.challengetemple.portal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public interface PortalApi {

    enum Direction { NORTH, SOUTH, EAST, WEST }

    Entity createPortal(double width, double height, Level fromLevel, Level toLevel,
                        Vec3 sourcePos, Vec3 destPos, Direction dir);
}
