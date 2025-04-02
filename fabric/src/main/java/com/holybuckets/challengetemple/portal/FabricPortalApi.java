package com.holybuckets.challengetemple.portal;

import com.holybuckets.foundation.GeneralConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;

public class FabricPortalApi implements PortalApi {

    public Entity createPortal(double width, double height, Entity sourceEntity, LevelAccessor toDimension, Vec3 dest)
    {

        Portal portal = PortalManipulation.placePortal(width, height, sourceEntity);
        if (portal == null) return null;

        Iterable<ServerLevel> levels = GeneralConfig.getInstance().getServer().getAllLevels();
        for(ServerLevel l : levels) {
            if(l == toDimension) {
                portal.dimensionTo = l.dimension();
                break;
            }
        }

        if(portal.dimensionTo == null) {
            portal.remove(Entity.RemovalReason.DISCARDED);
            return null;
        }
        portal.setDestination(dest);
        McHelper.spawnServerEntity(portal);
        return portal;
    }

}
