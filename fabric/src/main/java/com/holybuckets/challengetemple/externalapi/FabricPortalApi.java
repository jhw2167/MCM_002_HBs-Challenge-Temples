package com.holybuckets.challengetemple.externalapi;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.Portal;

public class FabricPortalApi implements PortalApi {


    @Override
    public boolean isPortal(Entity entity) {
        return entity != null && entity.getType() == Portal.entityType;
    }

    public Entity createPortal(double width, double height, Level fromLevel, Level toLevel,
                               Vec3 sourcePos, Vec3 destPos, Direction dir)
    {
        if(toLevel == null) return null;
        if(fromLevel == null) return null;

        Portal portal = new Portal(Portal.entityType, fromLevel);
        if (portal == null) return null;

        portal.setPosRaw(sourcePos.x, sourcePos.y, sourcePos.z);

        if(dir == Direction.NORTH) {
            portal.axisW = new Vec3(1, 0,0);
        } else if (dir == Direction.SOUTH) {
            portal.axisW = new Vec3(-1, 0,0);
        } else if (dir == Direction.EAST) {
            portal.axisW = new Vec3(0, 0, -1);
        } else if (dir == Direction.WEST) {
            portal.axisW = new Vec3(0, 0, 1);
        }
        portal.axisH = new Vec3(0, 1, 0);

        //Add support for "UP" and "DOWN" directions - portals in x,z plane,
        // width is x, height is z
        if (dir == Direction.UP) {
            portal.axisW = new Vec3(1, 0, 0);
            portal.axisH = new Vec3(0, 0, 1);
        } else if (dir == Direction.DOWN) {
            portal.axisW = new Vec3(-1, 0, 0);
            portal.axisH = new Vec3(0, 0, -1);
        }

        portal.width = width;
        portal.height = height;

        portal.dimensionTo = toLevel.dimension();
        portal.setDestination(destPos);

        McHelper.spawnServerEntity(portal);
        return portal;
    }

}
