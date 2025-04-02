package com.holybuckets.challengetemple.portal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public interface PortalApi {

    boolean createPortal(double width, double height, Entity sourceEntity, LevelAccessor toDimension, Vec3 dest);
}
