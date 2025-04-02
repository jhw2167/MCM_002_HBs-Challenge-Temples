package com.holybuckets.challengetemple.portal;

import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.jmx.Server;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;

import java.util.List;

public class FabricPortalApi implements PortalApi {

    public boolean createPortal(double width, double height, Entity sourceEntity, LevelAccessor toDimension, Vec3 dest) {
        Portal portal = PortalManipulation.placePortal(width, height, sourceEntity);
        if (portal == null) return false;

        Iterable<ServerLevel> levels = GeneralConfig.getInstance().getServer().getAllLevels();
        for(ServerLevel l : levels) {
            if(l == toDimension) {
                portal.dimensionTo = l.dimension();
                break;
            }
        }

        if(portal.dimensionTo == null) return false;
        portal.setDestination(dest);
        McHelper.spawnServerEntity(portal);
        return true;
    }

}
