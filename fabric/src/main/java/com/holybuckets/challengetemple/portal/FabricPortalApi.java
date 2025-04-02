package com.holybuckets.challengetemple.portal;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.api.PortalAPI;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;

public class PortalApi  {

    public boolean createPortal(double width, double height, Entity sourceEntity, ResourceKey<Level> toDimension, Vec3 dest) {
        Portal portal = PortalManipulation.placePortal(width, height, sourceEntity);
        if (portal == null) {
            return false;
        }
        portal.dimensionTo = toDimension;
        portal.setDestination(dest);
        McHelper.spawnServerEntity(portal);
        return true;
    }

}
