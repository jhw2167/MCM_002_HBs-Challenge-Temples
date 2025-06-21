package com.holybuckets.challengetemple.portal;

import com.holybuckets.foundation.GeneralConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.api.PortalAPI.*;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.imm_ptl.core.teleportation.ServerTeleportationManager;

public class FabricPortalApi implements PortalApi {


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

        portal.width = width;
        portal.height = height;

        portal.dimensionTo = toLevel.dimension();
        portal.setDestination(destPos);

        McHelper.spawnServerEntity(portal);
        return portal;
    }

    public void storeInventory(ServerPlayer player) {

    }

}
