package com.holybuckets.challengetemple.structure;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class StructurePieceSettings {

    public static boolean checkApplyCustomSettings(StructureTemplateManager templates) {
        if (templates == null)  return false;
        for( ResourceLocation loc : GridStructurePlacement.USE_GRID.keySet() ) {
            if( templates.get(loc) != null ) {
                return true;
            }
        }
        return false;
    }
}
